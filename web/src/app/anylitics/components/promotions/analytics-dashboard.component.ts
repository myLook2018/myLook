import { Component, OnInit, OnDestroy } from '@angular/core';
import { StoreModel } from 'src/app/auth/models/store.model';
import { FormGroup, FormBuilder } from '@angular/forms';
import { AnyliticService } from '../../services/anylitics.service';
import { PromotionsService } from '../../services/promotions.service';
import { DataService } from 'src/app/service/dataService';
import { MatTableDataSource } from '@angular/material';
import { ArticleService } from 'src/app/articles/services/article.service';

@Component({
  selector: 'app-analytics-dashboard',
  templateUrl: './analytics-dashboard.component.html',
  styleUrls: ['./analytics-dashboard.component.scss']
})
export class AnyliticsDashboardComponent implements OnInit, OnDestroy {
  actualStore = new StoreModel();
  interactions = [];
  // ------------------ de los graficos -------------------------------
  level1Articles;
  level2Articles;
  level3Articles;
  articlesByPromotionLevel = [1, 1, 1];
  levelInteractionCounter = [0, 0, 0];
  promotionImpact = [0, 0]; // Los likes son el primero, los dislike el segundo
  likes;
  dislikes;
  tortaTooltip = 'Indica la cantidad de prendas promocionadas que le gustaron o no al usuario, al encontrarse con tus prendas.';
  barrasTooltip = 'Indica la cantidad de veces en promedio que tu prenda se vio en detalle según su nivel de promoción.';

  // ------------------ de la tabla ------------------------------------
  displayedColumns = ['Articulo', 'Codigo', 'FechaInicio', 'FechaFin', 'NivelPromocion', 'PrecioFinal', 'Descargar'];
  promotionTableDataSource: any;
  promotionsData;
  locale: string;
  renderInteractions = false;
  storeArticles = [];
  constructor(
    fb: FormBuilder,
    public anyliticService: AnyliticService,
    private promotionsService: PromotionsService,
    private dataService: DataService,
    private articleService: ArticleService) {
  }

  ngOnInit() {
    console.log('-+-+-+-+-+-Inicializando Promociones -+-+-+-+-+-');
    this.dataService.getStoreInfo().then(store => {
      this.actualStore = store;
      console.log('actual store', this.actualStore);
      this.getPromotionsDone();
      this.anyliticService.getInteractions(this.actualStore.storeName).then((res) => {
        this.interactions = res;
        console.log('estas son tus interacciones ', this.interactions);
        this.articleService.getArticlesCopado(this.actualStore.storeName).then( articles => {
          this.storeArticles = articles;
          this.filterInteractions();
        });
      });
    });
  }

  ngOnDestroy(): void {
    console.log('destruyendo subscripciones');
  }

  // ----------------------------------- Todo Interacciones --------------------------------------------------------

  filterInteractions() {
    this.calculateArticlesByPromotion();
    this.calculateClicksOnArticle();
    this.calculatePromotionImpact();
    setTimeout(() => {
      this.renderInteractions = true;
    }, 2100);
  }

  calculateArticlesByPromotion() {
      const level3Interactions = this.interactions.filter( interaction => interaction.promotionLevel === 3);
      const itemsCountLevel3 = [];
      level3Interactions.forEach( interaction => {
        if (!itemsCountLevel3.includes(interaction.articleId)) { itemsCountLevel3.push(interaction.articleId); }
      });
      this.articlesByPromotionLevel[2] = itemsCountLevel3.length;


      const level2Interactions = this.interactions.filter( interaction => interaction.promotionLevel === 2);
      const itemsCountLevel2 = [];
      level2Interactions.forEach( interaction => {
        if (!itemsCountLevel2.includes(interaction.articleId)) { itemsCountLevel2.push(interaction.articleId); }
      });
      this.articlesByPromotionLevel[1] = itemsCountLevel2.length;


      const level1Interactions = this.interactions.filter( interaction => interaction.promotionLevel === 1);
      const itemsCountLevel1 = [];
      level1Interactions.forEach( interaction => {
        if (!itemsCountLevel1.includes(interaction.articleId)) { itemsCountLevel1.push(interaction.articleId); }
      });
      this.articlesByPromotionLevel[0] = itemsCountLevel1.length;
  }

  calculateClicksOnArticle() {
    const clickOnArticleInteractions = this.interactions.filter( interaction => interaction.clickOnArticle === true );
    clickOnArticleInteractions.forEach( interaction => this.levelInteractionCounter[interaction.promotionLevel - 1]++);
    console.log('visit interactions', this.levelInteractionCounter);
    this.level1Articles = this.levelInteractionCounter[0] / this.articlesByPromotionLevel[0];
    this.level2Articles = this.levelInteractionCounter[1] / this.articlesByPromotionLevel[1];
    this.level3Articles = this.levelInteractionCounter[2] / this.articlesByPromotionLevel[2];
  }

  calculatePromotionImpact() {
    const promotedInteractions = this.interactions.filter( interaction => interaction.promotionLevel > 1);
    const promotedInteractionsWithOutClick = promotedInteractions.filter( interaction => interaction.clickOnArticle === false);
    console.log('interacciones con promociones sin los visit', promotedInteractionsWithOutClick);
    promotedInteractionsWithOutClick.forEach( interaction => {
      if (interaction.liked) {
        this.promotionImpact[0]++;
      } else { this.promotionImpact[1]++; }
    });
    this.likes = this.promotionImpact[0];
    this.dislikes = this.promotionImpact[1];
  }

  // ----------------------------------- Todo la tablita -----------------------------------------------------------
  async getPromotionsDone() {
    await this.promotionsService.getPromotions(this.actualStore.firebaseUID).then( promotions => {
      console.log('promotions que me corresponden', promotions);
      this.promotionsData = promotions;
      this.promotionsData.forEach( promotion => {
        this.promotionsService.getArticleImage(promotion.articleId).then( data => {
           promotion.image = data.picture;
           promotion.title = data.name;
           promotion.code = data.code;
          });
      });
      console.log('promotions que me corresponden luego de agregar image', promotions);
      this.promotionTableDataSource = new MatTableDataSource(this.promotionsData);
    });
  }

  getTotalCost() {
    return this.promotionsData.map(promotion => promotion.promotionCost).reduce((acc, value) => acc + value, 0);
  }

  getPromotionLevel(level) {
    return (level === 3) ? 'Premium' : 'Estandar';
  }

  getImagesFromPromotion (articleId) {

  }

  downloadPromotion( element: any ) {
    console.log(element);
    const data = {info: element, store: this.actualStore };
    this.promotionsService.downloadDocument(data, 'promotion');
  }

}


