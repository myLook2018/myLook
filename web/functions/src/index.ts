import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin';
// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.

// The Firebase Admin SDK to access the Firebase Realtime Database.
admin.initializeApp({
  credential: admin.credential.cert({
    projectId: 'mylook-develop',
    clientEmail: 'firebase-adminsdk-gsb4e@mylook-develop.iam.gserviceaccount.com',
    privateKey: '-----BEGIN PRIVATE KEY-----\nMIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDcyLbnyqyB6F13\nGgX5iGpiBci8QYza72Nk4MWSv97DE8EyyRVu1HQidFbjXk3MFniqJGOBq23PQ4vD\n4KmhSubgMwfzZNmrgo65VIcEwtGzK+mD3spbzW9PuQ6gp3YitWsIiH/bCLONuh5h\nr3f6c8Kef3yEI1gswy0zRhu01dx1g258YZ2Gho0Z7NS9b+6AjEVVi0iE0PDqKONH\n1gbS+qVSr4hTi/LFqW83aOcmCMfrfGB0+Pa+hxZDw+aCV7IstnP+bY21HXGMoMMe\nwt8LfxERM+giQXmHdEukkMOEnRKvJyIhQ67g+nxLM/a1xpuRdcRWZM1mDh2WDcxb\nBNGBU6BnAgMBAAECggEAJY+aGm4PEhzDhect/Alau+lrAKgQPBD88mURB8nPrpV2\nSVsPY+cOCOyM+aqKBr4N8jg1m5JcIKZHqV/XzAJvq9XStPRD2dtgx6PbQpKYVWiT\nliU5fQvCs6fylp9WyEnf9zgttEhTL7npwEm3WisGgauDtgFDV34u+umi/g1iL7WG\n+CxmUFbxyE8XYlFfHmqS7a6k1ibYF2wc9l2yQH6WQ+GrzLFFHWN+CjtHAhHIqFJQ\nIEP/a6bojdNSL4JWurK8ZHJu0uELoURnMlDn+nbKaaTldBEzBJ+cBuCUNjJRx9GX\nOXcxyJRvv7PCSk+Wmo2So4VZbMejP0F8BQp8yTE4kQKBgQD7zyzHX1u8EIKDHDZC\nUMCAXjun5QIOnRuxzlaFHqpggznjmf4Wkq8lVV6Nsnr1iS0HQezGD6N2Mg3nVH+E\nqemrzy2i7UHRSriH8Xmv0tiIzziBfRwo2gfI8kkTgetg5jUMAj5LxzY775Hm/uOR\nhnPt/XQRvMq78vde9w5siHEJEQKBgQDgdVuJe1i8LsOEQpk2oghJKu+7VQtGxVCY\n0jEGPY2dHiuRc9UcjeNUXvBuy/3mLHfFmz2EU1CAAr5R3UZfPi7Jmps2yOrHo6lU\ny8UBLTiuBJ/ATq8Q0+/9yfM2k6H0mOo1vJob8HGesEzjkicP6u1UOiZ/KCdnOA0W\nZFRmPg3R9wKBgQDkYBtuns+mGxsjKf0T3AUa51wXg51//4FUm5zgktVXPuzLvagy\ndMqgg3KD9LGL6uOJx4Kkhx5rXi459erJOQkSp43uTFv7I1eFA76ndgSo37eRyRQ3\nALc6ynL4XEZL1b2arJYvR/eqGDctV1Kxc03FNK+GPsFoAI3V9R+B06Ug4QKBgQDg\nFAkIHT2e0Eib9yiho+0FrnmZ2PLi+gTWK7CJfFWt7vqrwKmWUggKqKT612dnrCj9\n+1bUjaAly5dYfsDMQHXRCim97aKFH1virXLFE/Irt64L/AFFmW9TO5M0fvoGB8p5\nTwnCrq2fMH3yCmw4td2sYkkgVGPR5eEa45pmX7jITQKBgGOAASzHnfcq7tXZZ4mF\nIHTqVuZALslUxaIvnt1ZKuBlaxc6aoRAaGx4u+9vcYAqMuU33bl59YyNGbqIkUdA\nmfLsR8Ldw05N8MLPRNoEawIYnrAZ6kHXs6RXX8gYSvgXqqASFDIO4goRYXnlmmrR\nVsMMs9j9jRtKd9wfCimHplQL\n-----END PRIVATE KEY-----\n'
  }),
  databaseURL: "https://mylook-develop.firebaseio.com"
});

//This functions listens to the node '/Food menu/date/Food' for new insert and sends notification to a client
exports.sendRequestNotification = functions.database.ref('requestRecommendations/{docId}').onWrite((snapshot, context) => {
    
    console.log("Entro al sendRequest");
    
  //place your client app registration token here, this is created when a user first opens the app and it is stored in the db. 
  //You could also retrieve the token from the db but in this case it is hard coded
  // Instalación Mateo
  const registrationToken = "dPcWZwavcxY:APA91bG1UBc75NsgCTGc-Y8nioTmiKn-wJqxpMe3UIT5ZrZHS0LZmYLiMT3WwROvUyI_QeCEgTNCxGWw9-fj2KOGoXELXade-KycQzJidl1ggIrl7yoYnhILZD2GXx-g5ZxxakI4QY0c";

  //This is the payload for notification

  const payload = {
    data: {
      'title': 'Tomorrow\'s Menu',
      'message': 'Hello, kindly check the menu available for today',
      'is_background': 'true',
      'image': 'http://www.allwhitebackground.com/images/3/3430.jpg',
      'timestamp': '234'
    }
  };

  // Send a message to the device corresponding to the provided
  // registration token.
  return admin.messaging().sendToDevice(registrationToken, payload)
    .then((response) => {
      // Response is a message ID string.
      console.log('Successfully sent message:', response);

      //return a promise here since this function is asynchronous
      return "Yes";
    })
    .catch((error) => {
      console.log('Error sending message:', error);
    });
  //return snapshot.ref.parent.child('uppercaseFood').set(uppercase);
});