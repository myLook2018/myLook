import { Injectable } from '@angular/core';
import { MessageService } from 'primeng/api';

@Injectable({
  providedIn: 'root',
})

export class ToastsService {

  constructor(
    private messageService: MessageService,
  ) { }


  showToastMessage( summary, type,  description = '' ) {
      this.messageService.add({
        severity: type,
        summary: summary,
        detail: description,
        life: 5000
      });
  }

}

export const TOASTSTYPES = {
  SUCCESS: 'success',
  WARN: 'warn',
  ERROR: 'error',
  INFO: 'info'
};
