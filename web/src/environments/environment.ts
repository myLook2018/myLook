// This file can be replaced during build by using the `fileReplacements` array.
// `ng build ---prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

export const environment = {
  production: false,
  firebase: {
    apiKey: 'AIzaSyBiBV35jkI7Ucmqy54kwY0zf3L-v9tMVl4',
    authDomain: 'app-mylook.firebaseapp.com',
    databaseURL: 'https://app-mylook.firebaseio.com',
    projectId: 'app-mylook',
    storageBucket: 'app-mylook.appspot.com',
    messagingSenderId: '722412293207',
    appId: '1:722412293207:web:86447671f28511e9'
  },
  maps: {
    key: 'AIzaSyDmuhZx-ew-zpQzQcjiqk2yJu5OonBuSMc'  }
  };

/*
 * In development mode, to ignore zone related error stack frames such as
 * `zone.run`, `zoneDelegate.invokeTask` for easier debugging, you can
 * import the following file, but please comment it out in production mode
 * because it will have performance impact when throw error
 */
// import 'zone.js/dist/zone-error';  // Included with Angular CLI.
