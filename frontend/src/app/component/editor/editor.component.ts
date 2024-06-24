import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { Board } from '../../model/board';
import { BoardService } from '../../service/board.service';
import { BoardDetailsComponent } from '../board-details/board-details.component';
import { BoardMembersComponent } from '../board-members/board-members.component';
import { BoardComponent } from '../board/board.component';

@Component({
  selector: 'app-editor',
  templateUrl: './editor.component.html',
  styleUrl: './editor.component.css'
})
export class EditorComponent implements OnInit {
  public isLoading: boolean = false;
  private shared: Observable<Board>;
  private board: Board;

  constructor(
    private boardService: BoardService,
    private router: Router,
    private route: ActivatedRoute
  ) { }

  get title(): string {
    if (!this.board)
      return "board";
    else
      return `${this.board.title} board`;
  }

  ngOnInit(): void {
    this.isLoading = true;
    this.route.paramMap.subscribe(params => {
      const boardId: number = Number(params.get("id"));
      this.shared = this.boardService.getBoard(boardId);
      this.shared.subscribe(
        {
          next: (value) => {
            this.board = value;
            this.isLoading = false;
          },
          error: err => {
            this.router.navigate(["dashboard"])
            this.isLoading = false;
          },
        }
      );;
    });
  }

  onOutletLoaded(component: any): void {
    if (component instanceof BoardComponent || BoardMembersComponent || BoardDetailsComponent)
      component.shared = this.shared;
  }
}
