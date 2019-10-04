// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });

// The Firebase Admin SDK to access the Firebase Realtime Database.
'use strict';

const functions = require('firebase-functions');
const sanitizer = require('./sanitizer');
const admin = require('firebase-admin');
admin.initializeApp();

let db = admin.firestore();

exports.sendFollowerNotification = functions.database.ref('/followers/{followedUid}/{followerUid}')
    .onWrite(async (change, context) => {
      const followerUid = context.params.followerUid;
      const followedUid = context.params.followedUid;
      // If un-follow we exit the function.
      if (!change.after.val()) {
        return console.log('User ', followerUid, 'un-followed user', followedUid);
      }
      console.log('We have a new follower UID:', followerUid, 'for user:', followedUid);

      // Get the list of device notification tokens.
      const getDeviceTokensPromise = admin.database()
          .ref(`/users/${followedUid}/notificationTokens`).once('value');

      // Get the follower profile.
      const getFollowerProfilePromise = admin.auth().getUser(followerUid);

      // The snapshot to the user's tokens.
      let tokensSnapshot;

      // The array containing all the user's tokens.
      let tokens;

      const results = await Promise.all([getDeviceTokensPromise, getFollowerProfilePromise]);
      tokensSnapshot = results[0];
      const follower = results[1];

      // Check if there are any device tokens.
      if (!tokensSnapshot.hasChildren()) {
        return console.log('There are no notification tokens to send to.');
      }
      console.log('There are', tokensSnapshot.numChildren(), 'tokens to send notifications to.');
      console.log('Fetched follower profile', follower);

      // Notification details.
      const payload = {
        notification: {
          title: 'You have a new follower!',
          body: `${follower.displayName} is now following you.`,
          icon: follower.photoURL
        }
      };

      // Listing all tokens as an array.
      tokens = Object.keys(tokensSnapshot.val());
      // Send notifications to all tokens.
      const response = await admin.messaging().sendToDevice(tokens, payload);
      // For each message check if there was an error.
      const tokensToRemove = [];
      response.results.forEach((result, index) => {
        const error = result.error;
        if (error) {
          console.error('Failure sending notification to', tokens[index], error);
          // Cleanup the tokens who are not registered anymore.
          if (error.code === 'messaging/invalid-registration-token' ||
              error.code === 'messaging/registration-token-not-registered') {
            tokensToRemove.push(tokensSnapshot.ref.child(tokens[index]).remove());
          }
        }
      });
      return Promise.all(tokensToRemove);
    });

exports.sendNotification = functions.firestore.document('users/{usersUid}/requestPickUp/{requestPickUpId}')
  .onUpdate(async(change, context) =>{

    //Basic log
    console.log('Your status just changed for id ', context.params.usersUid);
    
    const newValue = change.after.data();
    const previousValue = change.before.data();
    const statusChange = newValue.status;

    let userToken = db.collection('users').doc(context.params.usersUid);
    let getMyDocs = userToken.get().then(doc => {
      if (!doc.exists) {
        console.log('No such document!');
        throw new Error("no such document");
      } else {
        console.log('Document data:', doc.data());
        var fcmToken = doc.data().fcmToken;

        const payload = {
          notification: {
            title: 'Your order had received',
            body: `We will pick up soon`,
            sound: 'default'
          }
        };
        if(statusChange === 'pending'){
          return admin.messaging().sendToDevice(fcmToken, payload); 
        }else{
          throw new Error("No Change");
        }
      }
    }).catch(error => {
      console.log("Error ",error);
    })

    //Send notifications to all tokens.
    //const sendMsg = admin.messaging().sendToDevice(myData.fcmToken, payload);
    //return sendMsg;
    
});

exports.inboxNotif = functions.firestore.document('users/{usersUid}/inbox/{inboxId}')
  .onCreate(async(snap, context) =>{

    //Basic log: console.log('Your status just changed for id ', context.params.usersUid);
    
    const newValue = snap.data();

    //Get user token
    let userToken = db.collection('users').doc(context.params.usersUid);
    let getMyDocs = userToken.get().then(doc => {
      if (!doc.exists) {
        console.log('No such document!');
        throw new Error("no such document");
      } else {
        console.log('Document data:', doc.data());
        var fcmToken = doc.data().fcmToken;

        const payload = {
          notification: {
            title: newValue.catgorize,
            body: newValue.header,
            sound: 'default'
          }
        };

        return admin.messaging().sendToDevice(fcmToken, payload); 
      }
    }).catch(error => {
      console.log("Error ",error);
    })

    //Send notifications to all tokens.
    //const sendMsg = admin.messaging().sendToDevice(myData.fcmToken, payload);
    //return sendMsg;

});

exports.createUser = functions.https.onCall((data, context) => {
  // ...
  const newValue = data.text;
  const idUser = context.auth.uid;
  console.log("my id ",idUser);

      let dataUser = {
        banned: false,
        counter: 0.00
      };
      
      // Add a new document in collection "cities" with ID 'LA'
      //let setDoc = db.collection('users').doc(idUser).collection("adminSet").document("counterRequest").set(dataUser)
      let dbAdmin = db.collection('users').doc(idUser).collection("adminSet").doc("counterRequest");

      return dbAdmin
        .set(dataUser, {merge: true});
});