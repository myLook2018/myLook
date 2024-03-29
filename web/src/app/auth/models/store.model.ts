export class StoreModel {
  firebaseUID: string;
  firebaseUserId: string;
  storeName: string;
  storeMail: string;
  ownerName: string;
  profilePh: string;
  coverPh: string;
  storePhone: string;
  facebookLink: string;
  storeProvince: string;
  storeCity: string;
  storeAddressNumber: string;
  storeFloor: string;
  storePosition: string;
  storeDescription: string;
  instagramLink: string;
  twitterLink: string;
  storeAddress: string;
  provider: string;
  storeLatitude: number;
  storeLongitude: number;
  storeTower: string;
  storeDept: string;
  storefronts: Array<any>;
  registerDate: Date;

  constructor() {
    this.firebaseUID = '';
    this.firebaseUserId = '';
    this.storeName = '';
    this.storeMail = '';
    this.ownerName = '';
    this.profilePh = '/assets/noProfilePic.png';
    this.coverPh = '';
    this.storePhone = '';
    this.facebookLink = '';
    this.storeProvince = '';
    this.storeCity = '';
    this.storeAddressNumber = '';
    this.storeFloor = '';
    this.storePosition = '';
    this.storeDescription = '';
    this.instagramLink = '';
    this.twitterLink = '';
    this.storeAddress = '';
    this.provider = '';
    this.storeLatitude = 0;
    this.storeLongitude = 0;
    this.storeTower = '';
    this.storeDept = '';
    this.storefronts = [];
    this.registerDate = new Date();
  }
}
