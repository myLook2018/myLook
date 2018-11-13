import { Component, Inject, EventEmitter } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialog, MAT_DIALOG_DATA, MatDialogRef } from '@angular/material';
import { StoreService } from '../../../auth/services/store.service';
import { Router } from '@angular/router';
import { StoreModel } from 'src/app/auth/models/store.model';

@Component({
    selector: 'app-editstore',
    templateUrl: 'editStore.html'
})
export class EditStoreComponent {
    onAdd = new EventEmitter();
    storeForm: FormGroup;
    constructor(
        private fb: FormBuilder,
        private router: Router,
        // private storage: AngularFireStorage,
        public dialogRef: MatDialogRef<EditStoreComponent>,
        private service: StoreService,
        @Inject(MAT_DIALOG_DATA) public store: StoreModel
    ) {
        this.createForm();
    }

    createForm() {
        this.storeForm = this.fb.group({
            // completar los datos de la tienda
            storeName: [this.store.storeName, Validators.required],
            storeMail: [this.store.storeMail, Validators.required],
            ownerName: [this.store.ownerName, Validators.required],
            profilePh: [this.store.profilePh, Validators.required],
            coverPh: [this.store.coverPh, Validators.required],
            storePhone: [this.store.storePhone, Validators.nullValidator],
            storeCity: [this.store.storeCity, Validators.required],
            storeProvince: [this.store.storeProvince, Validators.required],
            storeAddress: [this.store.storeAddress, Validators.required],
            storeAddressNumber: [this.store.storeAddressNumber, Validators.required],
            storeFloor: [this.store.storeFloor, Validators.nullValidator],
            storePosition: [[20, 60], Validators.nullValidator],
            storeDescription: [this.store.storeDescription, Validators.nullValidator],
            instagramLink: [this.store.instagramLink, Validators.nullValidator],
            facebookLink: [this.store.facebookLink, Validators.nullValidator],
            twitterLink: [this.store.twitterLink, Validators.nullValidator],
        });
    }

    onNoClick(): void {
        this.dialogRef.close();
    }

    sendData() {
        this.onAdd.emit(this.storeForm.value);
    }

    /*saveChanges(form): void {
        const newInfo: Store = {
            storeName: form.name,
            storeAddress: form.address,
            storeDescription: form.desc,
            storeFloor: form.storeFloor,
            storeMail: form.mail,
            storePhone: form.phone,
            storePosition: form.storePosition,
            storeAddressNumber: form.addressNumber,
            userName: this.store.userName,
            facebookLink: form.facebook,
            twitterLink: form.twitter,
            instagramLink: form.instagram
        };
        console.log(newInfo);
        console.log(this.store);
        this.service.editStoreInformation(newInfo).then(snap => {
            this.onNoClick();
            this.router.navigate(['/store/' + this.store.userName]);
        });
    }*/
}
