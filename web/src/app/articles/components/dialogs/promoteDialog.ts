import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';

@Component({
  selector: 'app-promote-dialog',
  templateUrl: 'promoteDialog.html',
})
export class PromoteDialogComponent {
   minDate = new Date();
   dueDate;
  constructor(
    public dialogRef: MatDialogRef<PromoteDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data) {
    }

  onNoClick(): void {
    console.log('date ->' + this.dueDate);
    this.dialogRef.close();
  }
}
