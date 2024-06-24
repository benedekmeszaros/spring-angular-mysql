import { Dialog } from '@angular/cdk/dialog';
import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { BoardDialogComponent } from '../../dialog/board-dialog/board-dialog.component';
import { Board } from '../../model/board';
import { Invitation } from '../../model/invitation';
import { User } from '../../model/user';
import { StorageService } from '../../service/storage.service';

@Component({
  selector: 'app-project-menu',
  templateUrl: './project-menu.component.html',
  styleUrl: './project-menu.component.css'
})
export class ProjectMenuComponent implements OnInit {
  private user: User;
  public sharedBoards: Observable<Board[]>;
  public sharedInvitations: Observable<Invitation[]>;
  public boards: Board[] = [];
  public invitations: Invitation[] = [];

  constructor(
    private storage: StorageService,
    private dialog: Dialog,
  ) { }

  get myProjects(): Board[] {
    return this.boards.filter(board => board.owner.id === this.user.id);
  }

  get otherProjects(): Board[] {
    return this.boards.filter(board => board.owner.id !== this.user.id);
  }

  ngOnInit(): void {
    this.user = this.storage.getUser();

    this.sharedBoards.subscribe({
      next: boards => {
        this.boards = boards;
      }
    });

    this.sharedInvitations.subscribe({
      next: invitations => {
        this.invitations = invitations;
      },
      error(err) {
        console.error(err);
      },
    });
  }

  removeInvitation(invitation: Invitation) {
    if (invitation)
      this.invitations = this.invitations.filter(i => i.id !== invitation.id);
  }

  showCreateBoardDialog(): void {
    this.dialog.open(BoardDialogComponent);
  }
}
