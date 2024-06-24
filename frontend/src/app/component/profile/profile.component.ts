import { Dialog } from '@angular/cdk/dialog';
import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { GenericDialogComponent } from '../../dialog/generic-dialog/generic-dialog.component';
import { PasswordDialogComponent } from '../../dialog/password-dialog/password-dialog.component';
import { UsernameDialogComponent } from '../../dialog/username-dialog/username-dialog.component';
import { Board } from '../../model/board';
import { User } from '../../model/user';
import { StorageService } from '../../service/storage.service';
import { UserService } from '../../service/user.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent implements OnInit {
  @ViewChild("picture")
  public pictureInput: ElementRef;
  public user: User;
  public sharedBoards: Observable<Board[]>;
  public boards: Board[] = [];

  constructor(
    private storage: StorageService,
    private userService: UserService,
    private dialog: Dialog,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.user = this.storage.getUser();
    this.sharedBoards.subscribe({
      next: boards => {
        this.boards = boards;
      }
    })
  }

  onImageChanged(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file: File = input.files[0];
      if (file.type.includes("image"))
        this.userService.updateAvatar(file)
          .subscribe({
            next: url => {
              this.user.avatar = url;
              this.storage.setUser(this.user);
              location.reload();
            },
            error: err => {
              console.error(err);
            }
          })
    }
  }

  uploadImage(): void {
    this.pictureInput.nativeElement.click();
  }

  showUsernameDialog(): void {
    this.dialog.open<string>(UsernameDialogComponent, {
      data: {
        username: this.user.fullName
      }
    }).closed.subscribe({
      next: fullName => {
        if (fullName) {
          this.user.fullName = fullName;
          this.storage.setUser(this.user);
          this.boards.filter(board => board.owner.id === this.user.id).
            forEach(board => board.owner.fullName = fullName);
        }
      }
    })
  }

  showPasswordDialog(): void {
    this.dialog.open(PasswordDialogComponent);
  }

  showSuspendDialog(): void {
    const dialogRef = this.dialog.open(GenericDialogComponent, {
      data: {
        topic: "Suspend Account",
        description: "Are you surely want to suspend your Account permamently?",
        acceptLabel: "Yes",
        onAccept: () => {
          this.userService.suspend().subscribe({
            next: message => {
              console.log(message);
              this.storage.clearStorage(false);
              this.storage.clearStorage(true);
              dialogRef.close();
              this.router.navigate(["./login"]);
            },
            error: err => {
              console.error(err);
              dialogRef.close();
            }
          })
        }
      }
    })
  }
}
