import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';

@Component({
  selector: 'app-delete-front-confirmation-dialog',
  templateUrl: 'delete-front-confirmation-dialog.html',
})
export class DeleteFrontConfirmationDialogComponent {

  constructor(
    public dialogRef: MatDialogRef<DeleteFrontConfirmationDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data) {
      console.log('data', data);
    }

  onNoClick(): void {
    this.dialogRef.close();
  }
}
