import { Dialog } from '@angular/cdk/dialog';
import { CdkDragDrop, moveItemInArray } from '@angular/cdk/drag-drop';
import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { PhaseDialogComponent } from '../../dialog/phase-dialog/phase-dialog.component';
import { Board } from '../../model/board';
import { Phase } from '../../model/phase';
import { User } from '../../model/user';
import { PhaseService } from '../../service/phase.service';
import { StorageService } from '../../service/storage.service';

@Component({
  selector: 'app-board',
  templateUrl: './board.component.html',
  styleUrl: './board.component.css'
})
export class BoardComponent implements OnInit {
  public user: User;
  public shared: Observable<Board>;
  public board: Board;

  get access(): string {
    if (this.board && this.user)
      return this.board.members.find(member => member.user.id == this.user.id)?.access ?? '';
    else return '';
  }

  get prefix(): string {
    if (this.board && this.board.createdAt == this.board.updatedAt)
      return 'Created';
    else
      return 'Last updated';
  }

  constructor(
    private phaseService: PhaseService,
    private storage: StorageService,
    private dialog: Dialog
  ) { }

  hasAccess(required: string[]): boolean {
    return required.includes(this.access);
  }

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

  onArrange(): void {
    this.board.updatedAt = new Date();
    this.board.updatedBy = this.user;
  }

  onPhaseRemoved(phase: Phase): void {
    if (phase)
      this.board.phases = this.board.phases.filter(p => p.id !== phase.id);
    this.onArrange();
  }

  drop(event: CdkDragDrop<Phase[]>): void {
    if (event.previousIndex !== event.currentIndex) {
      moveItemInArray(this.board.phases, event.previousIndex, event.currentIndex);
      this.board.phases.forEach((p, i) => p.pos = i);
      this.phaseService.move(
        this.board.id,
        this.board.phases[event.currentIndex].id,
        event.currentIndex
      ).subscribe({
        next: value => {
          console.log(value);
          this.onArrange();
        },
        error(err) {
          console.error(err);
        },
      });
    }
  }

  showPhaseDialog(): void {
    this.dialog.open<Phase>(
      PhaseDialogComponent,
      {
        data: {
          boardId: this.board.id,
          editable: this.hasAccess(["OWNER"]),
        }
      }
    ).closed.subscribe({
      next: phase => {
        if (phase)
          this.board.phases.push(phase);
      }
    });
  }
}
