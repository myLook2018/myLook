export class FirebaseUserModel {
  image: string;
  name: string;
  provider: string;
  userId: string;
  userName: string;
  userPhone: string;
  displayPicture: string;
  storeID: string;
  firebaseId: string;

  constructor() {
    this.image = '';
    this.name = '';
    this.provider = '';
    this.userId = '';
    this.userName = '';
    this.userPhone = '';
    this.displayPicture = '';
    this.storeID = '';
    this.firebaseId = '';
  }
}
