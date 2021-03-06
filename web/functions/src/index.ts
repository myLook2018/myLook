Object.defineProperty(exports, "__esModule", { value: true });
const functions = require("firebase-functions");
const admin = require("firebase-admin");
const promisePool = require('es6-promise-pool');
const PromisePool = promisePool.PromisePool;
const secureCompare = require('secure-compare');
// Maximum concurrent account deletions.
const MAX_CONCURRENT = 3;
let request = require('request');
let mercadopago = require('mercadopago');

mercadopago.configure({
    client_id: '1059447032112952',
    client_secret: '3at5jmJl40HnPV0kBGPVjmjL4PCA9Iyp'
});

// Método que inicializa las funciones, sin esto no anda
admin.initializeApp(functions.config().firebase)

/**
 * Si en el registro/log aparece  "Function execution took N ms, finished with status: 'timeout' "
 * la función se está ejecutando, pero es un promise no es instantáneo. Tarda entre 3 y 5 minutos.
 */

/**
 * Envia notificaciones a los suscritos a un tópico, esto puede servir para Usuarios destacados. Lo dejo para tomar referencia
 * @param snap Contiene la información del cambio
 * @param snap.before.data() JSON del documento previo al cambio
 * @param snap.after.data() JSON del documento posterior al cambio
 */
exports.pushNotifications = functions.firestore.document('requestRecommendations/{docId}').onWrite((snap: any, context: any) => {
    const newValue = snap.after.data();
    const previousValue = snap.before.data();

    const newAnswer = newValue.answers
    const oldAnswer = previousValue.answers
    if (newAnswer.length > oldAnswer.length) {
        const title = newValue.title
        const desc = newAnswer[newAnswer.length - 1].description
        const storeName = newAnswer[newAnswer.length - 1].storeName
        const payload = {
            notification: {
                title: "Nueva Recomendación para " + title + " de " + storeName,
                body: desc,
                sound: "default"
            }
        };
        const options = {
            priority: "high",
            timeToLive: 60 * 60 * 24
        }
        return { p: payload, o: options }
        return admin.messaging().sendToTopic("pushNotifications", payload, options)
    }
    return "Didnt write new recommendations"
});

exports.newAnswerNotification = functions.firestore.document('requestRecommendations/{docId}').onWrite((snap: any, context: any) => {
    const newValue = snap.after.data();
    const previousValue = snap.before.data();
    const id = snap.before.id
    const newAnswer = newValue.answers
    const oldAnswer = previousValue.answers

    if (newAnswer.length >= oldAnswer.length) {
        const title = newValue.title
        let userId = newValue.userId
        const desc = newAnswer[newAnswer.length - 1].description
        const storeName = newAnswer[newAnswer.length - 1].storeName
        const payload = {
            notification: {
                title: "Nueva Recomendación para " + title + " de " + storeName,
                body: desc,
                sound: "default"
            }, 
            data: {
                requestId: newValue.id
            }
        };
        const options = {
            priority: "high",
            timeToLive: 60 * 60 * 24
        }
        return admin.firestore().collection("clients").get()
            .then((snapshot: any) => {
                snapshot.forEach((doc: any) => {
                    console.log('user', 'userId ' + userId + ' - Doc user Id' + doc.data().userId + ' - docId ' + doc.id)
                    if (userId == doc.data().userId || userId == doc.id) {
                        console.log("Es este Usuario!!! " + userId)
                        const registrationToken = doc.data().installToken
                        var message = {
                            data: {
                                "title": "Nueva Recomendación para " + title + " de " + storeName,
                                "deepLink": "www.mylook.com/recommendation",
                                "body": desc,
                                "sound": "default",
                                "requestId": id
                            },
                            "token": registrationToken
                        };
                        console.log(JSON.stringify(message))
                        return admin.messaging().send(message)
                            .then((response) => {
                                // Response is a message ID string.
                                console.log('Successfully sent message:', response);
                            })
                            .catch((error) => {
                                console.log('Error sending message:', error);
                            });
                    }
                });

            });;


    }
});

exports.closeRecommendations = functions.https.onRequest((req: any, res: any) => {
    const key = req.query.key;

    // Exit if the keys don't match.
    if (!secureCompare(key, functions.config().cron.key)) {
        console.log('The key provided in the request does not match the key set in the environment. Check that', key,
            'matches the cron.key attribute in `firebase env:get`');
        res.status(403).send('Security key does not match. Make sure your "key" URL query parameter matches the ' +
            'cron.key environment variable.');
        return null;
    }
    return admin.firestore().collection("requestRecommendations").get()
        .then((snapshot: any) => {
            snapshot.docs.filter((snap: any) => {
                return snap.data().limitDate < (new Date).getTime() && !snap.data().isClosed
            }).forEach((element: any) => {
                admin.firestore().collection("requestRecommendations").doc(element.id).update({ isClosed: true }).then((result: any) => {
                    console.log("Cambio de estado exitoso!!", result)
                })
            });

        });

});


exports.newMercadoPagoDoc = functions.firestore.document('prueba/{userId}')
    .onCreate((snap:any, context: any) => {
    // Get an object representing the document
    // e.g. {'name': 'Marie', 'age': 66}
    const preference = snap.data();
    mercadopago.preferences.create(preference)
        .then(function (preferenceASW: any) {
          return 'si, anda la cloud functon y salio todo bien';
        }).catch(function (error: any) {
          return 'algo salio mal. Si, no tengo idea que, de nada por lo especifico. Toma este erorr ' + error;
        });
    // make the request
});

/**
 * When requested this Function will delete every user accounts that has been inactive for one year.
 *
 */
exports.accountcleanup = functions.https.onRequest((req: any, res: any) => {
    const key = req.query.key;

    // Exit if the keys don't match.
    if (!secureCompare(key, functions.config().cron.key)) {
        console.log('The key provided in the request does not match the key set in the environment. Check that', key,
            'matches the cron.key attribute in `firebase env:get`');
        res.status(403).send('Security key does not match. Make sure your "key" URL query parameter matches the ' +
            'cron.key environment variable.');
        return null;
    }

    // Fetch all user details.
    return getInactiveUsers().then((inactiveUsers: [any]) => {
        // Use a pool so that we delete maximum `MAX_CONCURRENT` users in parallel.
        const promisePool = new PromisePool(() => deleteInactiveUser(inactiveUsers), MAX_CONCURRENT);
        return promisePool.start();
    }).then(() => {
        console.log('User cleanup finished');
        res.send('User cleanup finished');
        return null;
    });
});

/**
 * Deletes one inactive user from the list.
 */
function deleteInactiveUser(inactiveUsers: [any]) {
    if (inactiveUsers.length > 0) {
        const userToDelete = inactiveUsers.pop();

        // Delete the inactive user.
        return admin.auth().deleteUser(userToDelete.uid).then(() => {
            console.log('Deleted user account', userToDelete.uid, 'because of inactivity');
            return null;
        }).catch((error: Error) => {
            console.error('Deletion of inactive user account', userToDelete.uid, 'failed:', error);
            return null;
        });
    }
    return null;
}

/**
 * Returns the list of all inactive users.
 */
function getInactiveUsers(users = [], nextPageToken?: any) {
    return admin.auth().listUsers(1000, nextPageToken).then((result: any) => {
        // Find users that have not signed in in the last year.
        const inactiveUsers = result.users.filter(
            (user: any) => Date.parse(user.metadata.lastSignInTime) < (Date.now() - 365 * 24 * 60 * 60 * 1000));

        // Concat with list of previously found inactive users if there was more than 1000 users.
        users = users.concat(inactiveUsers);

        // If there are more users to fetch we fetch them.
        if (result.pageToken) {
            return getInactiveUsers(users, result.pageToken);
        }
        return users;
    });
}

exports.newMercadoPagoDoc = functions.firestore.document('prueba/{userId}')
    .onCreate((snap:any, context: any) => {
    // Get an object representing the document
    // e.g. {'name': 'Marie', 'age': 66}
    const preference = snap.data();
    mercadopago.preferences.create(preference)
        .then(function (preferenceASW: any) {
          return 'si, anda la cloud functon y salio todo bien';
        }).catch(function (error: any) {
          return 'algo salio mal. Si, no tengo idea que, de nada por lo especifico. Toma este erorr ' + error;
        });
    // make the request
});
