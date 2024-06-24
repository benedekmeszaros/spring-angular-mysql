import { Dialog } from '@angular/cdk/dialog';
import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { InvitationDialogComponent } from '../../dialog/invitation-dialog/invitation-dialog.component';
import { Board } from '../../model/board';
import { Membership } from '../../model/membership';
import { User } from '../../model/user';
import { StorageService } from '../../service/storage.service';

@Component({
  selector: 'app-board-members',
  templateUrl: './board-members.component.html',
  styleUrl: './board-members.component.css'
})
export class BoardMembersComponent implements OnInit {
  private user: User;
  public shared: Observable<Board>;
  public board: Board;

  get access(): string {
    if (this.board && this.user)
      return this.board.members.find(member => member.user.id == this.user.id)?.access ?? '';
    else return '';
  }

  constructor(
    private storage: StorageService,
    private dialog: Dialog
  ) { }

  ngOnInit(): void {
    this.shared.subscribe(
      {
        next: (value) => {
          this.user = this.storage.getUser();
          this.board = value;
        }
      }
    );
  }

  removeMember(member: Membership): void {
    if (member)
      this.board.members = this.board.members.filter(m => m.id !== member.id);
  }

  showInviteDialog(): void {
    this.dialog.open(InvitationDialogComponent, {
      data: {
        boardId: this.board.id
      }
    });
  }
}
