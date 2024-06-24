import { DIALOG_DATA, DialogRef } from '@angular/cdk/dialog';
import { Component, Inject } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Membership } from '../../model/membership';
import { MemberService } from '../../service/member.service';

export interface InvitationDialogData {
  boardId: number,
  member: Membership
}

@Component({
  selector: 'app-invitation-dialog',
  templateUrl: './invitation-dialog.component.html',
  styleUrl: './invitation-dialog.component.css'
})
export class InvitationDialogComponent {
  public isUpdate: boolean;
  public submitForm: FormGroup;
  public access: string = 'Access';
  public errorResponse = {
    isActive: false,
    message: null
  };

  get enabled(): boolean {
    return (this.isUpdate && this.data.member.access !== this.access.toLocaleUpperCase()) ||
      (!this.isUpdate && this.access != 'Access');
  }

  get email(): AbstractControl {
    return this.submitForm.controls['email'];
  }

  constructor(
    private dialogRef: DialogRef<string>,
    @Inject(DIALOG_DATA) public data: InvitationDialogData,
    private memberService: MemberService,
    private fb: FormBuilder
  ) {
    this.submitForm = fb.group({
      email: [null, [Validators.required, Validators.email]],
    });

    this.submitForm.valueChanges.subscribe({
      next: () => {
        this.errorResponse.isActive = false;
      }
    });

    if (data.member) {
      data.member && this.submitForm.get('email')?.setValue(data.member.user.email);
      data.member && this.submitForm.get('email')?.disable();
      this.access = data.member.access;
      this.isUpdate = data.member.user.email != undefined;
    }
  }

  setAccess(access: string): void {
    this.access = access;
  }

  onClose(): void {
    this.dialogRef.close();
  }

  onSubmit(event: Event): void {
    event.preventDefault();
    if (this.data.member)
      this.update();
    else
      this.invite();
  }

  private update(): void {
    this.memberService.update(
      this.data.boardId,
      this.data.member.id,
      this.access
    ).subscribe({
      next: member => {
        this.dialogRef.close(this.access);
      },
      error: err => {
        console.log(err);
        this.errorResponse.message = err.error;
        this.errorResponse.isActive = true;
      }
    })
  }

  private invite(): void {
    this.memberService.invite(
      this.data.boardId,
      this.submitForm.value.email,
      this.access.toLocaleUpperCase()
    ).subscribe({
      next: message => {
        console.log(message);
        this.dialogRef.close();
      },
      error: err => {
        console.error(err);
        this.email.reset();
        this.errorResponse.message = err.error;
        this.errorResponse.isActive = true;
      }
    });
  }
}
