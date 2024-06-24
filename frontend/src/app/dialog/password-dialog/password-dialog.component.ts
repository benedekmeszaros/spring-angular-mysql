import { DialogRef } from '@angular/cdk/dialog';
import { Component } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, ValidatorFn, Validators } from '@angular/forms';
import { UserService } from '../../service/user.service';

@Component({
  selector: 'app-password-dialog',
  templateUrl: './password-dialog.component.html',
  styleUrl: './password-dialog.component.css'
})
export class PasswordDialogComponent {
  public submitForm: FormGroup;
  public isPasswordVisible: boolean = false;
  public isConfirmPasswordVisible: boolean = false;
  public errorResponse = {
    isActive: false,
    message: null
  };

  get password(): AbstractControl {
    return this.submitForm.controls['password'];
  }

  get confirmPassword(): AbstractControl {
    return this.submitForm.controls['confirmPassword'];
  }

  constructor(
    private dialogRef: DialogRef,
    private userService: UserService,
    private fb: FormBuilder
  ) {
    this.submitForm = fb.group({
      password: [null, [Validators.required, Validators.minLength(8), Validators.pattern("^(?=.*[A-Z]+)(?=.*[0-9]+).*$")]],
      confirmPassword: [null, [Validators.required, this.passwordMatch()]]
    });

    this.submitForm.valueChanges.subscribe({
      next: () => {
        this.errorResponse.isActive = false;
      }
    })
  }

  togglePasswordVisibility(): void {
    this.isPasswordVisible = !this.isPasswordVisible;
  }

  toggleConfirmPasswordVisibility(): void {
    this.isConfirmPasswordVisible = !this.isConfirmPasswordVisible;
  }

  onClose(): void {
    this.dialogRef.close();
  }

  passwordMatch(): ValidatorFn {
    return (control: AbstractControl): { [key: string]: any } | null => {
      const confirmPassword = control.value;

      if (!this.submitForm)
        return null;

      const password = this.submitForm.get('password')?.value;
      return password === confirmPassword ? null : { passwordMismatch: true };
    }
  }

  onSubmit(event: Event): void {
    event.preventDefault();
    this.userService.updatePassword(
      this.submitForm.value.password
    ).subscribe({
      next: message => {
        this.dialogRef.close();
      },
      error: err => {
        console.error(err);
        this.errorResponse.isActive = true;
        this.errorResponse.message = err.error;
      }
    });
  }
}
