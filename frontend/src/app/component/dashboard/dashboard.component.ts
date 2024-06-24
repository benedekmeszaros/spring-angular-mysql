import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { Board } from '../../model/board';
import { Invitation } from '../../model/invitation';
import { BoardService } from '../../service/board.service';
import { MemberService } from '../../service/member.service';
import { ProfileComponent } from '../profile/profile.component';
import { ProjectMenuComponent } from '../project-menu/project-menu.component';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {
  private sharedBoards: Observable<Board[]>;
  private sharedInvitations: Observable<Invitation[]>;
  public boards: Board[] = [];

  constructor(
    private boardService: BoardService,
    private memberService: MemberService
  ) { }

  ngOnInit(): void {
    this.sharedBoards = this.boardService.getBoards();
    this.sharedBoards.subscribe({
      next: boards => {
        this.boards = boards;
      },
      error: err => {
        console.error(err);
      }
    });
    this.sharedInvitations = this.memberService.getInvitations();
  }

  onOutletLoaded(component: any): void {
    if (component instanceof ProjectMenuComponent) {
      component.sharedBoards = this.sharedBoards;
      component.sharedInvitations = this.sharedInvitations;
    }
    if (component instanceof ProfileComponent)
      component.sharedBoards = this.sharedBoards;
  }
}
