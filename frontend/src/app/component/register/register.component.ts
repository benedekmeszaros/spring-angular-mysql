import { Component } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, ValidatorFn, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../service/auth.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
  public registerForm: FormGroup;
  public isPasswordVisible: boolean = false;
  public isConfirmPasswordVisible: boolean = false;
  public errorResponse = {
    isActive: false,
    message: null
  };

  get username(): AbstractControl {
    return this.registerForm.controls['username'];
  }

  get email(): AbstractControl {
    return this.registerForm.controls['email'];
  }

  get password(): AbstractControl {
    return this.registerForm.controls['password'];
  }

  get confirmPassword(): AbstractControl {
    return this.registerForm.controls['confirmPassword'];
  }

  constructor(
    private fb: FormBuilder,
    private auth: AuthService,
    private router: Router
  ) {
    this.registerForm = fb.group({
      username: [null, [Validators.required, Validators.minLength(3)]],
      email: [null, [Validators.required, Validators.email]],
      password: [null, [Validators.required, Validators.minLength(8), Validators.pattern("^(?=.*[A-Z]+)(?=.*[0-9]+).*$")]],
      confirmPassword: [null, [Validators.required, this.passwordMatch()]]
    });
    this.registerForm.valueChanges.subscribe({
      next: () => {
        this.errorResponse.isActive = false;
      }
    });
  }

  togglePasswordVisibility(): void {
    this.isPasswordVisible = !this.isPasswordVisible;
  }

  toggleConfirmPasswordVisibility(): void {
    this.isConfirmPasswordVisible = !this.isConfirmPasswordVisible;
  }

  onSubmit(event: Event): void {
    event.preventDefault();
    this.auth.registrate(
      this.registerForm.value.email,
      this.registerForm.value.username,
      this.registerForm.value.password
    )
      .subscribe({
        next: value => {
          console.log(value);
          this.router.navigate(['./login']);
        },
        error: err => {
          console.error(err.error);
          this.email.reset();
          this.errorResponse.isActive = true;
          this.errorResponse.message = err.error;
        },
      })
  }

  passwordMatch(): ValidatorFn {
    return (control: AbstractControl): { [key: string]: any } | null => {
      const confirmPassword = control.value;

      if (!this.registerForm)
        return null;

      const password = this.registerForm.get('password')?.value;
      return password === confirmPassword ? null : { passwordMismatch: true };
    }
  }
}
