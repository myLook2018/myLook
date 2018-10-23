import { RecomendationAnswer } from './recomendationAnswer.model';

export class RecomendationRequest {
    FirebaseUID?: string;
    title?: string;
    userName?: string;
    state?: boolean;
    description?: String;
    limitDate?: Date;
    updateDate?: Date;
    requestPhoto?: string; // Opcional
    localization?: [Number]; // Se toma desde donde se hizo o la del User?
    answers?: RecomendationAnswer[];
  }
