import { DIALOG_DATA, DialogRef } from '@angular/cdk/dialog';
import { Component, Inject } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserService } from '../../service/user.service';

export interface UsernameDialogData {
  username: string
}

@Component({
  selector: 'app-username-dialog',
  templateUrl: './username-dialog.component.html',
  styleUrl: './username-dialog.component.css'
})
export class UsernameDialogComponent {
  public submitForm: FormGroup;

  get username(): AbstractControl {
    return this.submitForm.controls['username'];
  }

  constructor(
    private dialogRef: DialogRef<string>,
    @Inject(DIALOG_DATA) public data: UsernameDialogData,
    private userService: UserService,
    private fb: FormBuilder
  ) {
    this.submitForm = fb.group({
      username: [null, [Validators.required, Validators.minLength(3)]],
    })

    data.username && this.submitForm.get('username')?.setValue(data.username);
  }

  onClose(): void {
    this.dialogRef.close();
  }

  onSubmit(event: Event): void {
    event.preventDefault();
    this.userService.updateUsername(
      this.submitForm.value.username
    ).subscribe({
      next: user => {
        this.dialogRef.close(user.fullName);
      },
      error: err => {
        console.error(err);
        this.dialogRef.close(undefined);
      }
    })
  }
}
