import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { User } from '../../model/user';
import { AuthService } from '../../service/auth.service';
import { StorageService } from '../../service/storage.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent implements OnInit {
  public user: User | null = null;

  constructor(
    private router: Router,
    private auth: AuthService,
    private storage: StorageService
  ) {
    storage.activeUser.subscribe({
      next: user => this.user = user
    })
  }

  ngOnInit(): void {
    this.storage.activeUser.subscribe({
      next: user => this.user = user
    })
  }

  logout(): void {
    this.auth.logout().subscribe({
      next: value => {
        console.log(value);
        this.user = null;
        this.router.navigate(["login"]);
      },
      error(err) {
        console.error(err);
      },
    });
  }

  showUserInfo() {
    this.router.navigate(['dashboard/profile']);
  }
}
