import { Dialog } from '@angular/cdk/dialog';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { GenericDialogComponent } from '../../dialog/generic-dialog/generic-dialog.component';
import { TaskDialogComponent } from '../../dialog/task-dialog/task-dialog.component';
import { Task } from '../../model/task';
import { TaskService } from '../../service/task.service';

@Component({
  selector: 'app-task',
  templateUrl: './task.component.html',
  styleUrl: './task.component.css'
})
export class TaskComponent {
  @Output()
  public onRemoved: EventEmitter<Task> = new EventEmitter();
  @Output()
  public onUpdated: EventEmitter<Task> = new EventEmitter();

  @Input()
  public task: Task;
  @Input()
  public boardId: number;
  @Input()
  public phaseId: number;
  @Input()
  public access: string;

  constructor(
    private dialog: Dialog,
    private taskService: TaskService
  ) { }

  hasAccess(required: string[]): boolean {
    return required.includes(this.access);
  }

  showTaskDetails(): void {
    this.dialog.open<Task>(TaskDialogComponent, {
      data: {
        boardId: this.boardId,
        phaseId: this.phaseId,
        task: this.task,
        editable: this.hasAccess(['OWNER', "EDITOR"])
      }
    }).closed.subscribe({
      next: task => {
        if (task)
          this.task = task;
      }
    });
  }

  showRemoveDialog(): void {
    const dialogRef = this.dialog.open(GenericDialogComponent, {
      data: {
        topic: "Remove Task",
        description: `You surely wan't to delete "${this.task.label}" task?`,
        acceptLabel: "Remove",
        onAccept: () => {
          this.taskService.delete(this.boardId, this.phaseId, this.task.id).subscribe({
            next: message => {
              this.onRemoved.emit(this.task);
              dialogRef.close();
            },
            error: err => {
              console.error(err);
              dialogRef.close();
            }
          })
        }
      }
    });
  }
}
