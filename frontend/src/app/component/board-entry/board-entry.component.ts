import { Component, Input } from '@angular/core';
import { Router } from '@angular/router';
import { Board } from '../../model/board';

@Component({
  selector: 'app-board-entry',
  templateUrl: './board-entry.component.html',
  styleUrl: './board-entry.component.css',
  host: {
    "(click)": "onClick()"
  }
})
export class BoardEntryComponent {
  @Input()
  public board: Board;

  get description(): string {
    return this.board.description && this.board.description.length > 0 ? this.board.description : "No description found";
  }

  constructor(
    private router: Router
  ) { }

  onClick(): void {
    this.router.navigate([`dashboard/boards/${this.board.id}`]);
  }
}
