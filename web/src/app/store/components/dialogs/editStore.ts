import { Component, Inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AngularFireStorage, AngularFireUploadTask } from 'angularfire2/storage';
import { MatDialog, MAT_DIALOG_DATA, MatDialogRef } from '@angular/material';
import { StoreService } from '../../../auth/services/store.service';
import { Store } from '../../model/store.model';
import { Router } from '@angular/router';

@Component({
    selector: 'app-editstore',
    templateUrl: 'editStore.html'
})
export class EditStoreComponent {
    storeForm: FormGroup;
    store: Store;

    constructor(
        private fb: FormBuilder,
        private router: Router,
        // private storage: AngularFireStorage,
        public dialogRef: MatDialogRef<EditStoreComponent>,
        private service: StoreService,
        @Inject(MAT_DIALOG_DATA) store: any

    ) {

        this.store = store.data;
        this.createForm();
    }

    createForm() {
        this.storeForm = this.fb.group({
            // completar los datos de la tienda
            name: [this.store.storeName, Validators.required],
            phone: [this.store.storePhone, Validators.nullValidator],
            mail: [this.store.storeMail, Validators.required],
            desc: [this.store.storeDescription, Validators.nullValidator],
            facebook: [this.store.facebookLink, Validators.nullValidator],
            instagram: [this.store.instagramLink, Validators.nullValidator],
            twitter: [this.store.twitterLink, Validators.nullValidator],
            address: [this.store.storeAddress, Validators.required],
            addressNumber: [this.store.storeAddressNumber, Validators.required],
            storeFloor: [this.store.storeFloor, Validators.nullValidator],
            storePosition: [[20, 60], Validators.nullValidator]
        });
    }

    onNoClick(): void {
        this.dialogRef.close();
    }

    saveChanges(form): void {
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
    }
}
