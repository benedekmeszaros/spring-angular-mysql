import { Dialog } from '@angular/cdk/dialog';
import { Component, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { GenericDialogComponent } from '../../dialog/generic-dialog/generic-dialog.component';
import { ProfileDialogComponent } from '../../dialog/profile-dialog/profile-dialog.component';
import { Board } from '../../model/board';
import { User } from '../../model/user';
import { BoardService } from '../../service/board.service';
import { StorageService } from '../../service/storage.service';

@Component({
  selector: 'app-board-details',
  templateUrl: './board-details.component.html',
  styleUrl: './board-details.component.css'
})
export class BoardDetailsComponent implements OnInit {
  public user: User;
  public shared: Observable<Board>;
  public board: Board;
  public boardForm: FormGroup;
  public errorResponse = {
    isActive: false,
    message: null
  };

  get access(): string {
    if (this.board && this.user)
      return this.board.members.find(member => member.user.id == this.user.id)?.access ?? '';
    else return '';
  }

  get title(): AbstractControl {
    return this.boardForm.controls['title'];
  }

  constructor(
    private storage: StorageService,
    private dialog: Dialog,
    private boardService: BoardService,
    private rotuer: Router,
    private fb: FormBuilder
  ) {
    this.boardForm = fb.group({
      title: [null, Validators.required],
      description: [null]
    });
    this.boardForm.valueChanges.subscribe({
      next: () => {
        this.errorResponse.isActive = false;
      }
    })
  }

  hasAccess(required: string[]): boolean {
    return required.includes(this.access);
  }

  modified(): boolean {
    return this.board.title == this.boardForm.value.title &&
      this.board.description == this.boardForm.value.description;
  }

  resetDetails(): void {
    this.boardForm.get('title')?.setValue(this.board.title);
    this.boardForm.get('description')?.setValue(this.board.description);
  }

  showProfileDialog(): void {
    this.dialog.open(ProfileDialogComponent, { data: { user: this.board.owner } });
  }

  showUpdateDialog(): void {
    const dialogRef = this.dialog.open(GenericDialogComponent, {
      data: {
        topic: "Update Board",
        description: "Are you surely want to update this board?",
        acceptLabel: "Yes",
        cancelLabel: "No",
        onAccept: () => {
          this.boardService.update(
            this.board.id,
            this.boardForm.value.title,
            this.boardForm.value.description
          ).subscribe({
            next: board => {
              this.board = board;
              dialogRef.close();
            },
            error: err => {
              console.error(err);
              this.resetDetails();
              dialogRef.close();
              this.errorResponse.isActive = true;
              this.errorResponse.message = err.error;
            }
          });
        }
      }
    })
  }

  showDeleteDialog(): void {
    const dialogRef = this.dialog.open(GenericDialogComponent, {
      data: {
        topic: "Delete Board",
        description: "Are you surely want to delete this board?",
        acceptLabel: "Yes",
        cancelLabel: "No",
        onAccept: () => {
          this.boardService.remove(this.board.id)
            .subscribe({
              next: message => {
                console.log(message);
                dialogRef.close();
                this.rotuer.navigate(['dashboard']);
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

  ngOnInit(): void {
    this.shared.subscribe(
      {
        next: (value) => {
          this.user = this.storage.getUser();
          this.board = value;
          this.boardForm.get('title')?.setValue(this.board.title);
          this.boardForm.get('description')?.setValue(this.board.description);

          !this.hasAccess(['OWNER']) && this.boardForm.get('title')?.disable();
          !this.hasAccess(['OWNER']) && this.boardForm.get('description')?.disable();
        }
      }
    );
  }
}
