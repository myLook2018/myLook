import { AngularFirestore, AngularFirestoreCollection} from 'angularfire2/firestore';
import { Injectable } from '@angular/core';
import * as firebase from 'firebase';
import { Promotion } from '../components/promotions/model/promotion';
import * as jsPDF from 'jspdf';
import { logo } from '../components/promotions/logo';
@Injectable()
export class PromotionsService {
  mpURL = 'https://us-central1-app-mylook.cloudfunctions.net/postMercadopagoCheckout';
  promotionsCollection: AngularFirestoreCollection<Promotion>;
  promotions: Promotion[] = [];
  // tslint:disable-next-line:no-inferrable-types
  promotionsPath: string = 'promotions';
  articlesPath = 'articles';
  db: firebase.firestore.Firestore;
  require: any;
  isCached = false;

  constructor(public fst: AngularFirestore) {
    console.log(`en el collector`);
    this.promotionsCollection = this.fst.collection(this.promotionsPath);
    // Required for side-effects
    this.db = firebase.firestore();
  }

  getPromotions(storeId) {
    // tslint:disable-next-line:no-shadowed-variable
    return new Promise<any>((resolve, reject) => {
      if (!this.isCached) {
        console.log(`estamos preguntando por este ID` + storeId);
        this.db.collection(this.promotionsPath).where('storeId', '==', storeId)
          .get().then((querySnapshot) => {
            querySnapshot.forEach((doc) => {
              const data = doc.data();
              data.documentId = doc.id;
              this.promotions.push(data);
            });
          }).then(() => {
            console.log('promotions', this.promotions);
            this.isCached = true;
            resolve(this.promotions);
          })
          .catch(function (error) {
            console.log('Error getting documents: ', error);
            reject(error);
          });
      } else {
        console.log('devolviendo cache');
        resolve(this.promotions);
      }
    });
  }

  async getArticleImage(promotion) {
    const doc = this.db.doc(`${this.articlesPath}/${promotion}`);
    const snapshot = await doc.get();
    const value = snapshot.data();
    const ALFIN = {picture: value.picturesArray[0], name: value.title, code: value.code};
    return ALFIN;
  }

  updateStoreInformation( storeInformation ) {

  }

  cleanCache() {
    this.promotions = [];
    this.isCached = false;
  }

  downloadDocument(data, type) {
    console.log('data', data);
  /*
  Generates and downloads the PDF version of the ong dashboard
  imagenes: array con imagenes de graficos
  data: object with the ong stats
  */
  const doc = new jsPDF({format: 'a4'});
  // Header

  // tslint:disable-next-line: max-line-length

  doc.addImage(logo, 'PNG', 160, 2, 40, 15);
  doc.setFontSize(12);
  doc.setFontStyle('normal');
  doc.text('Código de recibo', 5, 10);
  const id = type === 'promotion' ? data.info.documentId : data.info.id;
  doc.text(id, 5, 18);

  // doc.text('Detalle de Venta de Promoción', 70, 10);
  doc.line(5, 20, 205, 20);

  doc.setFontStyle('bold');

  const title = type === 'promotion' ? 'Detalle de promoción contratada' : 'Detalle de campaña de cupones contratada';
  const titleStartFrom = type === 'promotion' ? 70 : 60;
  doc.text(title, titleStartFrom , 10);
  // doc.text('Estadísticas de la organización', 67, 26);

  doc.setFontStyle('normal');
  doc.setFontSize(11);
  doc.text(`Nombre de la tienda:`, 6, 30);
  doc.text(`${data.store.storeName}`, 70, 30);

  const elementName = type === 'promotion' ? 'Nombre de la prenda:' : 'Título de la campaña:';
  doc.text(elementName, 6, 40);
  doc.text(`${data.info.title}`, 70, 40);

  if (type === 'promotion') {
    doc.text(`Código de la prenda:`, 6, 50);
    doc.text(`${data.info.code}`, 70, 50);
  } else {
    doc.text(`Tipo de campaña:`, 6, 50);
    const campaignType = data.info.campaignType === 0 ? 'Campaña Estandar' : 'Campaña Premium';
    doc.text(campaignType, 70, 50);
  }

  const startDateLabel = type === 'promotion' ? `Inicio de promoción:` : `Inicio de campaña:`;
  doc.text(startDateLabel, 6, 60);
  const dateStart = type === 'promotion' ? data.info.startOfPromotion.toDate() : data.info.startDate.toDate();
  doc.text(`${formatDate(dateStart)}`, 70, 60);

  const endDateLabel = type === 'promotion' ? `Finaliación de promoción:` : `Finalizacióm de campaña:`;
  doc.text(endDateLabel, 6, 70);
  const dateEnd = type === 'promotion' ? data.info.endOfPromotion.toDate() : data.info.dueDate.toDate();
  doc.text(`${formatDate(dateEnd)}`, 70, 70);

  if (type === 'promotion') {
    const promocionType = (data.info.promotionLevel === 3) ? 'Premium' : 'Estandar';
    doc.text(`Tipo de promoción:`, 6, 80);
    doc.text(`${promocionType}`, 70, 80);
  } else {
    const cantVoucherLabel = 'Cantidad de cupones generados:';
    doc.text(cantVoucherLabel, 6, 80);
    doc.text(`${data.info.clientsId.length}`, 70, 80);
  }

  // Aca van todos los de una promocion

  if (type === 'promotion') {
    // doc.setFontStyle('bold');
  const payType = data.info.payMethod === 'debit_card' ? 'Tarjeta de Débito' : 'Tarjeta de Crédito';
  doc.text(`Forma de Pago:`, 6, 90);
  doc.text(`${payType}`, 70, 90);

  const payMethod = data.info.paymentMethod === 'debvisa' ? 'Visa' : 'Master Card';
  doc.text(`Tipo de tarjeta:`, 6, 100);
  doc.text(`${payMethod}`, 70, 100);

  const cardOwner = data.info.cardOwner ? data.info.cardOwner : 'No disponible';
  doc.text(`Nombre del Titular:`, 6, 110);
  doc.text(`${cardOwner}`, 70, 110);

  const last4digits = data.info.lastFourDigits ? data.info.lastFourDigits : 'No disponible';
  doc.text(`Número de tarjeta:`, 6, 120);
  doc.text(`xxxx-xxxx-xxxx-${last4digits}`, 70, 120);

  const idMercadoPago = data.info.idMercadoPago ? data.info.idMercadoPago : 'No disponible';
  doc.text(`Código de Transacción:`, 6, 130);
  doc.text(`${idMercadoPago}`, 70, 130);

  // precio final
  doc.setFontStyle('bold');
  doc.text(`Precio Final: $${data.info.promotionCost}`, 160, 140);

  // lineas para rodear el precio final
  doc.line(5, 135, 205, 135);
  doc.line(5, 145, 205, 145);

  } else {
    // Aca van los datos extras de una campaña de cupones
  doc.text(`Genero Apuntado:`, 6, 90);
  const genderPreference = data.info.campaignType === 1 ? data.info.genderFocus : 'Todos';
  doc.text(genderPreference, 70, 90);

  doc.text(`Rango de Edad:`, 6, 100);
  const ageRange = (data.info.campaignType === 1) ? `Entre ${data.info.fromAge} y ${data.info.toAge} años` : 'Indistinto';
  doc.text(ageRange, 70, 100);

  const payType = data.info.payMethod === 'debit_card' ? 'Tarjeta de Débito' : 'Tarjeta de Crédito';
  doc.text(`Forma de Pago:`, 6, 110);
  doc.text(`${payType}`, 70, 110);

  const payMethod = data.info.paymentMethod === 'debvisa' ? 'Visa' : 'Master Card';
  doc.text(`Tipo de tarjeta:`, 6, 120);
  doc.text(`${payMethod}`, 70, 120);

  const cardOwner = data.info.cardOwner ? data.info.cardOwner : 'No disponible';
  doc.text(`Nombre del Titular:`, 6, 130);
  doc.text(`${cardOwner}`, 70, 130);

  const last4digits = data.info.lastFourDigits ? data.info.lastFourDigits : 'No disponible';
  doc.text(`Número de tarjeta:`, 6, 140);
  doc.text(`xxxx-xxxx-xxxx-${last4digits}`, 70, 140);

  const idMercadoPago = data.info.idMercadoPago ? data.info.idMercadoPago : 'No disponible';
  doc.text(`Código de Transacción:`, 6, 150);
  doc.text(`${idMercadoPago}`, 70, 150);

  // precio final
  doc.setFontStyle('bold');
  const totalCost = type === 'promotion' ? data.info.promotionCost : data.info.campaignCost;
  doc.text(`Precio Final: $${totalCost}`, 160, 160);

  // lineas para rodear el precio final
  doc.line(5, 155, 205, 155);
  doc.line(5, 165, 205, 165);
  }

  // Footer
  doc.line(5, 285, 205, 285);
  doc.text(formatDate(new Date()), 5, 292);
  doc.text('1', 200, 292);

  function formatDate(date) {
    const monthNames = [
      'Enero', 'Febrero', 'Marzo',
      'Abril', 'Mayo', 'Junio', 'Julio',
      'Agosto', 'Septiembre', 'Octubre',
      'Noviembre', 'Diciembre'
    ];

    const dayOfWeek = [
      'Domingo', 'Lunes', 'Martes', 'Miercoles', 'Jueves', 'Viernes', 'Sábado'
    ];

    const dayIndex = date.getDay();
    const day = date.getDate();
    const monthIndex = date.getMonth();
    const year = date.getFullYear();

    return  dayOfWeek[dayIndex] + ', ' + day + ' de ' + monthNames[monthIndex] + ' de ' + year;
  }

  console.log(formatDate(new Date()));  // show current date-time in console
  doc.save('Recibo - ' + data.info.title + '-' + formatDate(new Date()) + '.pdf');
  }
}
