import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from './service/auth.service';
import { StorageService } from './service/storage.service';
import { UserService } from './service/user.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {

  public isLoading: boolean = false;

  constructor(
    private auth: AuthService,
    private storage: StorageService,
    private userService: UserService,
    private router: Router
  ) { }

  ngOnInit(): void {
    if (!this.auth.isAuthenticated() && this.storage.getRememberMe()) {
      this.isLoading = true;
      this.auth.refresh().subscribe({
        next: value => {
          this.userService.getMe().subscribe({
            next: user => {
              this.storage.setUser(user);
              this.router.navigate(["dashboard"]);
              this.isLoading = false;
            },
            error: err => {
              console.error(err);
              this.isLoading = false;
            },
          })
        },
        error: err => {
          console.error(err);
          this.router.navigate(["login"]);
          this.storage.setRememberMe(false);
          this.isLoading = false;
        }
      })
    }
  }

}
