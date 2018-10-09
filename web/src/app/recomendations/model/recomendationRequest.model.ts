import { RecomendationAnswer } from './recomendationAnswer.model';

export class RecomendationRequest {
    FirebaseUID?: string;
    UserName?: string;
    Description?: String;
    LimitDate?: Date;
    UpdateDate?: Date;
    ArticleUrl?: string; // Opcional
    Localization?: [Number]; // Se toma desde donde se hizo o la del User?
    Answers?: RecomendationAnswer[];
  }
