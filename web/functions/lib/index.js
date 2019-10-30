'use strict';
Object.defineProperty(exports, '__esModule', { value: true });
const functions = require('firebase-functions');
const admin = require('firebase-admin');
const promisePool = require('es6-promise-pool');
const PromisePool = promisePool.PromisePool;
const secureCompare = require('secure-compare');
const cors = require('cors')({ origin: true });
const Busboy = require('busboy');
const axios = require('axios');
const https = require('https');
const access_token = 'APP_USR-1059447032112952-040618-c6e69a975167f3e9a01ca5939306a4b6-181044052'
const mercadopago = require('mercadopago');
mercadopago.configure({
  client_id: '1059447032112952',
  client_secret: '3at5jmJl40HnPV0kBGPVjmjL4PCA9Iyp'
});

// Maximum concurrent account deletions.
const MAX_CONCURRENT = 3;
// Método que inicializa las funciones, sin esto no anda
admin.initializeApp(functions.config().firebase);
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
exports.premiumUserBroadcast = functions.firestore
  .document('diffusionMessages/{docId}')
  .onWrite((snap, context) => {
    const newPublication = snap.after.data();
    const topic = newPublication.topic
    console.log(JSON.stringify(newPublication))
    return admin.firestore().doc('clients/' + newPublication.clientId).get().then(snapshot => {
      console.log(newPublication.creationDate.seconds)
      const message = {
        data: {
          "title": newPublication.premiumUserName + ' publicó en su canal!',
          "deepLink": "www.mylook.com/diffusionChannel",
          "sound": "default",
          "topic": topic,
          "userImage": newPublication.userPhotoUrl,
          "body": newPublication.message,
          "premiumUserName": newPublication.premiumUserName
        },
        "topic": topic
      }

      return admin
        .messaging()
        .send(message)
        .then((response) => {
          // Response is a message ID string.
          console.log('Successfully sent message:', response);
        })
        .catch((error) => {
          console.log('Error sending message:', error);
        });

    })
  });

exports.newAnswerNotification = functions.firestore
  .document('requestRecommendations/{docId}')
  .onWrite((snap, context) => {
    const newValue = snap.after.data();
    const previousValue = snap.before.data();
    const newAnswer = newValue.answers;
    const oldAnswer = previousValue.answers;
    if (newAnswer.length >= oldAnswer.length) {
      const title = newValue.title;
      let userId = newValue.userId;
      const desc = newAnswer[newAnswer.length - 1].description;
      const storeName = newAnswer[newAnswer.length - 1].storeName;
      const payload = {
        notification: {
          title: 'Nueva Recomendación para ' + title + ' de ' + storeName,
          body: desc,
          sound: 'default'
        }
      };
      const options = {
        priority: 'high',
        timeToLive: 60 * 60 * 24
      };
      return admin
        .firestore()
        .collection('clients')
        .get()
        .then(snapshot => {
          snapshot.forEach(doc => {
            console.log(
              'user',
              'userId ' +
              userId +
              ' - Doc user Id' +
              doc.data().userId +
              ' - docId ' +
              doc.id
            );
            if (userId == doc.data().userId || userId == doc.id) {
              console.log('Es este Usuario!!! ' + userId);
              const registrationToken = doc.data().installToken;
              return admin
                .messaging()
                .sendToDevice(registrationToken, payload, options)
                .then(response => {
                  console.log('Successfully sent message:', response);
                })
                .catch(error => {
                  console.log('Error sending message:', error);
                });
            }
          });
        });
    }
  });
exports.closeRecommendations = functions.https.onRequest((req, res) => {
  const key = req.query.key;
  // Exit if the keys don't match.
  if (!secureCompare(key, functions.config().cron.key)) {
    console.log(
      'The key provided in the request does not match the key set in the environment. Check that',
      key,
      'matches the cron.key attribute in `firebase env:get`'
    );
    res
      .status(403)
      .send(
        'Security key does not match. Make sure your "key" URL query parameter matches the ' +
        'cron.key environment variable.'
      );
    return null;
  }
  return admin
    .firestore()
    .collection('requestRecommendations')
    .get()
    .then(snapshot => {
      snapshot.docs
        .filter(snap => {
          return (
            snap.data().limitDate < new Date().getTime() &&
            !snap.data().isClosed
          );
        })
        .forEach(element => {
          admin
            .firestore()
            .collection('requestRecommendations')
            .doc(element.id)
            .update({ isClosed: true })
            .then(result => {
              console.log('Cambio de estado exitoso!!', result);
            });
        });
    });
});
/**
 * When requested this Function will delete every user accounts that has been inactive for one year.
 *
 */
exports.accountcleanup = functions.https.onRequest((req, res) => {
  const key = req.query.key;
  // Exit if the keys don't match.
  if (!secureCompare(key, functions.config().cron.key)) {
    console.log(
      'The key provided in the request does not match the key set in the environment. Check that',
      key,
      'matches the cron.key attribute in `firebase env:get`'
    );
    res
      .status(403)
      .send(
        'Security key does not match. Make sure your "key" URL query parameter matches the ' +
        'cron.key environment variable.'
      );
    return null;
  }
  // Fetch all user details.
  return getInactiveUsers()
    .then(inactiveUsers => {
      // Use a pool so that we delete maximum `MAX_CONCURRENT` users in parallel.
      const promisePool = new PromisePool(
        () => deleteInactiveUser(inactiveUsers),
        MAX_CONCURRENT
      );
      return promisePool.start();
    })
    .then(() => {
      console.log('User cleanup finished');
      res.send('User cleanup finished');
      return null;
    });
});
/**
 * Deletes one inactive user from the list.
 */
function deleteInactiveUser(inactiveUsers) {
  if (inactiveUsers.length > 0) {
    const userToDelete = inactiveUsers.pop();
    // Delete the inactive user.
    return admin
      .auth()
      .deleteUser(userToDelete.uid)
      .then(() => {
        console.log(
          'Deleted user account',
          userToDelete.uid,
          'because of inactivity'
        );
        return null;
      })
      .catch(error => {
        console.error(
          'Deletion of inactive user account',
          userToDelete.uid,
          'failed:',
          error
        );
        return null;
      });
  }
  return null;
}
/**
 * Returns the list of all inactive users.
 */
function getInactiveUsers(users = [], nextPageToken) {
  return admin
    .auth()
    .listUsers(1000, nextPageToken)
    .then(result => {
      // Find users that have not signed in in the last year.
      const inactiveUsers = result.users.filter(
        user =>
          Date.parse(user.metadata.lastSignInTime) <
          Date.now() - 365 * 24 * 60 * 60 * 1000
      );
      // Concat with list of previously found inactive users if there was more than 1000 users.
      users = users.concat(inactiveUsers);
      // If there are more users to fetch we fetch them.
      if (result.pageToken) {
        return getInactiveUsers(users, result.pageToken);
      }
      return users;
    });
}

exports.postMercadopagoCheckout = functions.https.onRequest((req, res) => {
  cors(req, res, () => {
    if (req.method !== 'POST') {
      return res.status(500).json({
        message: 'Only post method allowed'
      });
    }

    mercadopago.preferences.create(req.body).then(response => {
      return res.status(200).json({
        message: 'checkout created',
        initPoint: response.body.init_point
      });
    }).catch(error => {
      console.log('error', error);
      return res.status(500).json({
        message: 'Algo salio mal con mercadopago',
        error: error.message
      });
    });
  });
});

exports.getMercadoPagoNotification = functions.https.onRequest((req, res) => {
  cors(req, res, () => {

    https.get(`https://api.mercadopago.com/v1/payments/${req.query.id}?access_token=APP_USR-1059447032112952-040618-c6e69a975167f3e9a01ca5939306a4b6-181044052`, (resp) => {

      resp.on("data", function (d) {
        //d.external_reference tiene el id del doc y los codigos de nivel de promocion

        const laData = JSON.parse(d.toString());
        const external_reference = laData.external_reference

        // 0:promotionLevel - 1:duracion en dias - 2:uid del articulo
        var articlePromotionLevel = '';
        var promotionDuration = '';
        var articleToPromoteId = '';
        var voucherReference = '';
        if (external_reference.includes('-')) {
          var articleInformation = external_reference.split("-");
          articlePromotionLevel = parseInt(articleInformation[0]);
          promotionDuration = parseInt(articleInformation[1]);
          articleToPromoteId = articleInformation[2];
        } else {
          voucherReference = external_reference;
        }


        // La platita que nos ingresó
        const promotionCost = parseInt(laData.transaction_amount);

        // metodo de pago
        const paymentMethod = laData.payment_method_id
        //forma de pago
        const payMethod = laData.payment_type_id
        console.log('toda la data', laData);
        // ultimos cuatro digitos
        const lastFourDigits = laData.card.last_four_digits
        // titular de la tarjeta
        const cardOwner = laData.card.cardholder.name

        // storeName
        const storeName = laData.payer.first_name

        //id de la transaccion de mercadopago
        const idMercadoPago = laData.id


        if (!voucherReference) {
        admin.firestore().collection('articles').doc(articleToPromoteId).update({ promotionLevel: articlePromotionLevel }).then(result => {
          admin.firestore().collection('stores').where('storeName', '==', storeName).get().then(snapshot => {
            snapshot.forEach(doc => {

              let end = new Date;
              let duration = promotionDuration;
              end.setDate(end.getDate() + duration);
              const promotion = {
                articleId: articleToPromoteId,
                startOfPromotion: new Date,
                endOfPromotion: end,
                storeId: doc.id,
                storeName: storeName,
                payMethod: payMethod,
                promotionLevel: articlePromotionLevel,
                promotionCost: promotionCost,
                idMercadoPago: idMercadoPago,
                paymentMethod: paymentMethod,
                lastFourDigits: lastFourDigits,
                cardOwner: cardOwner
              };

              admin.firestore().collection('promotions').add(promotion).then((docRef) => {
                return res.status(200).json({
                  message: 'OK'
                });
              });

            });
          });
        });
      } else {
        admin.firestore().collection('voucherCampaing').doc(voucherReference).update({
          idMercadoPago: idMercadoPago,
          paymentMethod: paymentMethod,
          lastFourDigits: lastFourDigits,
          cardOwner: cardOwner,
          payMethod: payMethod
        }).then(() => {
          return res.status(200).json({
            message:'OK'
          });
        })
      }
      }).on("error", (err) => {
        console.log("Error: " + err.message);
      });;

    });
  });
});
exports.closePromotions = functions.https.onRequest((req, res) => {
  admin
    .firestore()
    .collection('promotions')
    .get()
    .then(snapshot => {
      snapshot.docs
        .filter(snap => {
          console.log(snap.data().endOfPromotion.seconds)
          console.log(admin.firestore.Timestamp.now().seconds)
          return snap.data().endOfPromotion.seconds < admin.firestore.Timestamp.now().seconds

        })
        .forEach(element => {
          admin
            .firestore()
            .collection('articles')
            .doc(element.data().articleId)
            .update({ promotionLevel: 1 })
            .then(result => {
              console.log('Nivel de promoción de prenda modificado', result);
              return res.status(200).json({
                message: 'OK'
              });
            }).catch(error => {
              console.log("Error: " + error);

            });
        });
    });
});


function createVoucherCode(){
  const characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
  let code = ''
  for (let i = 0; i < 8; i++) {
    let index = Math.random()*characters.length
    code = code + (characters.charAt(index))
  }
  return code
}
 exports.createCampaignCoupons = functions.firestore.document('voucherCampaing/{voucherCampaignId}')
  .onUpdate( async (snap, context) => {
    const newCampaign = snap.after.data();

    if (newCampaign.idMercadoPago && newCampaign.idMercadoPago != null && newCampaign.idMercadoPago != 0 && newCampaign.idMercadoPago != '') {
      admin.firestore().collection("subscriptions").where("storeName", '==', newCampaign.storeName ).get().then( subscriptionSnap => {
        let suscribedUsersId = subscriptionSnap.docs.map(suscription => {
          return suscription.get("userId")
        })

          newCampaign.clientsId.forEach((clientId) => {
            console.log("Client id")
            console.log(clientId)
            admin.firestore().collection("clients").doc(clientId).get().then(async(clientSnap) => {
              console.log("ClientSnap")
              console.log(clientSnap.data())
              let voucher = {
                storeName: newCampaign.storeName, 
                storeId: newCampaign.storeId,
                description:newCampaign.description,
                title: newCampaign.title,
                startDate: newCampaign.startDate,
                dueDate: newCampaign.dueDate,
                voucherType: newCampaign.voucherType,
                campaignType: newCampaign.campaignType,
                used:false,
                usedDate: null,
                code: '',
                clientId: clientId,
                subscribed: suscribedUsersId.includes(clientSnap.get("userId")),
                dni: clientSnap.get("dni"),
                installToken: clientSnap.get("installToken"),
                birthday: clientSnap.get("birthday"),
                gender: null,
                age: null
              }
              let created = false;
              while (!created) {
                let code = createVoucherCode()
                voucher.code = code
                console.log("Voucher"+JSON.stringify(voucher))
                await admin.firestore().collection("vouchers").doc(code).create(voucher).then(voucherResponse => {
                  console.log("Created new Voucher:"+ voucherResponse.id)
                  created = true
                }).catch(error => {
                  console.log("Voucher could not be created "+error)
                })
                
              }
              
            });
    
          });
        })

    }
  })

  exports.newVoucherNotification = functions.firestore.document('vouchers/{docId}')
  .onWrite((snap, context) => {
    console.log("New voucher")
      const registrationToken = snap.after.data().installToken;
      var message = {
          data: {
              "title": "¡"+snap.after.data().storeName+" te mandó un nuevo cupón!",
              "deepLink": "www.mylook.com/coupon",
              "body": snap.after.data().description,
              "sound": "default",
              "voucherCode": snap.after.data().code,
              "storeId": snap.after.data().storeId,
              "storeName": snap.after.data().storeName, 
              "couponTitle":snap.after.data().title
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
      
    
  });