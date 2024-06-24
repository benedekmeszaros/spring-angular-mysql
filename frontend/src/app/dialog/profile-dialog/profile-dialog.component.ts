import { DIALOG_DATA, DialogRef } from '@angular/cdk/dialog';
import { Component, Inject } from '@angular/core';
import { User } from '../../model/user';

export interface ProfileDialogData {
  user: User
}

@Component({
  selector: 'app-profile-dialog',
  templateUrl: './profile-dialog.component.html',
  styleUrl: './profile-dialog.component.css'
})
export class ProfileDialogComponent {

  constructor(
    private dialogRef: DialogRef,
    @Inject(DIALOG_DATA) public data: ProfileDialogData,
  ) { }

  onClose(): void {
    this.dialogRef.close();
  }
}
