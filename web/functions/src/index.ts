Object.defineProperty(exports, "__esModule", { value: true });
const functions = require("firebase-functions");
const admin = require("firebase-admin");


// Método que inicializa las funciones, sin esto no anda
admin.initializeApp(functions.config().firebase)

/**
 * Envia notificaciones a los suscritos a un tópico, esto puede servir para Usuarios destacados. Lo dejo para tomar referencia
 * @param snap Contiene la información del cambio
 * @param snap.before.data() JSON del documento previo al cambio
 * @param snap.after.data() JSON del documento posterior al cambio
 */
exports.pushNotifications = functions.firestore.document('requestRecommendations/{docId}').onWrite((snap: any, context: any) => {
    const newValue = snap.after.data();
    const previousValue = snap.before.data();

    const title = newValue.title
    const newAnswer = newValue.answers
    const oldAnswer = previousValue.answers
    if (newAnswer.length > oldAnswer) {
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

        return admin.messaging().sendToTopic("pushNotifications", payload, options)
    }
});

exports.newAnswerNotification = functions.firestore.document('requestRecommendations/{docId}').onWrite((snap: any, context: any) => {
    const newValue = snap.after.data();
    const previousValue = snap.before.data();

    const title = newValue.title
    const newAnswer = newValue.answers
    const oldAnswer = previousValue.answers
    if (newAnswer.length > oldAnswer.length) {
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
        let userId = newValue.userId
        return admin.firestore().ref('/clients')
            .orderByChild('userId').equalTo(userId)
            .then((snapshot: any) => {
                const registrationToken = snapshot.val().installToken
                admin.messaging().sendToDevice(registrationToken, payload, options)
                    .then((response: Response) => {
                        console.log('Successfully sent message:', response);
                    })
                    .catch((error: Error) => {
                        console.log('Error sending message:', error);
                    });
            });;


    }
});


