import { Component } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../service/auth.service';
import { StorageService } from '../../service/storage.service';
import { UserService } from '../../service/user.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  public isLoading: boolean = false;
  public loginForm: FormGroup;
  public isPasswordVisible = false;
  public errorResponse = {
    isActive: false,
    message: null
  };

  get email(): AbstractControl {
    return this.loginForm.controls['email'];
  }

  get password(): AbstractControl {
    return this.loginForm.controls['password'];
  }

  constructor(
    private fb: FormBuilder,
    private auth: AuthService,
    private router: Router,
    private storage: StorageService,
    private userService: UserService
  ) {
    this.loginForm = fb.group({
      email: [null, Validators.required],
      password: [null, Validators.required],
      remember: [false]
    });
    this.loginForm.valueChanges.subscribe({
      next: () => {
        this.errorResponse.isActive = false;
      }
    });
  }

  toggleVisibility(): void {
    this.isPasswordVisible = !this.isPasswordVisible;
  }

  onSubmit(event: Event): void {
    event.preventDefault();
    this.isLoading = true;
    this.auth.login(this.loginForm.value.email, this.loginForm.value.password)
      .subscribe({
        next: (value) => {
          this.userService.getMe().subscribe({
            next: user => {
              this.storage.setRememberMe(this.loginForm.value.remember);
              this.storage.setUser(user);
              this.router.navigate(["dashboard"]);
            },
            error: err => {
              console.error(err);
              this.isLoading = false;
            },
          })
        },
        error: err => {
          console.error(err);
          this.email.reset();
          this.password.reset();
          this.errorResponse.message = err.error;
          this.errorResponse.isActive = true;
          this.isLoading = false;
        },
      });
  }
}
