import { RecomendationAnswer } from './recomendationAnswer.model';

export class RecomendationRequest {
    FirebaseUID?: string;
    category?: string;
    sex?: string;
    title?: string;
    userName?: string;
    isClosed?: boolean;
    description?: String;
    limitDate?: Date;
    updateDate?: Date;
    requestPhoto?: string; // Opcional
    localization?: any; // Se toma desde donde se hizo o la del User?
    answers?: RecomendationAnswer[];
    size?: string;
    isNear: boolean;
    distance: number;
    tooltip: String;
  }
