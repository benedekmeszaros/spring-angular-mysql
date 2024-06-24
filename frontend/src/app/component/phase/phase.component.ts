import { Dialog } from '@angular/cdk/dialog';
import { CdkDragDrop, moveItemInArray, transferArrayItem } from '@angular/cdk/drag-drop';
import { Component, ElementRef, EventEmitter, Input, Output, ViewChild } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, ValidatorFn, Validators } from '@angular/forms';
import { GenericDialogComponent } from '../../dialog/generic-dialog/generic-dialog.component';
import { PhaseDialogComponent } from '../../dialog/phase-dialog/phase-dialog.component';
import { Phase } from '../../model/phase';
import { Task } from '../../model/task';
import { PhaseService } from '../../service/phase.service';
import { TaskService } from '../../service/task.service';

@Component({
  selector: 'app-phase',
  templateUrl: './phase.component.html',
  styleUrl: './phase.component.css'
})
export class PhaseComponent {
  @Output()
  public onArranged: EventEmitter<Task> = new EventEmitter();
  @Output()
  public onRemoved: EventEmitter<Phase> = new EventEmitter();

  @Input()
  public boardId: number;
  @Input()
  public access: string;
  @Input()
  public phase: Phase;
  @ViewChild("inputField")
  public inputField: ElementRef;
  public addTaskForm: FormGroup;
  public isAddTaskVisible: boolean = false;

  get taskLabels(): string[] {
    if (this.phase)
      return this.phase.tasks.map(task => task.label);
    else
      return [];
  }

  constructor(
    private phaseService: PhaseService,
    private taskService: TaskService,
    private dialog: Dialog,
    private fb: FormBuilder
  ) {
    this.addTaskForm = fb.group({
      label: [null, [Validators.required, this.taskLabelValidator()]]
    });
  }

  get id(): string {
    return `phase-${this.phase.id}`;
  }

  private extractId(id: string): number {
    return Number(id.split("-")[1]);
  }

  hasAccess(required: string[]): boolean {
    return required.includes(this.access);
  }

  addTask(): void {
    this.taskService.upload(
      this.boardId,
      this.phase.id,
      this.addTaskForm.value.label,
      ""
    ).subscribe({
      next: (task) => {
        this.phase.tasks.push(task);
        this.addTaskForm.reset();
      },
      error(err) {
        console.error(err);
      },
    })
  }

  showAddTask(): void {
    this.isAddTaskVisible = true;
    setTimeout(() => {
      this.inputField.nativeElement.focus();
    }, 10);
  }

  hideAddTask(): void {
    this.isAddTaskVisible = false;
    this.addTaskForm.reset();
  }

  showDetails(): void {
    this.dialog.open<Phase>(PhaseDialogComponent, {
      data: {
        boardId: this.boardId,
        phase: this.phase,
        editable: this.hasAccess(["OWNER"]),
      }
    }).closed.subscribe({
      next: phase => {
        if (phase)
          this.phase = phase;
      }
    });
  }

  onRemoveTask(task: Task): void {
    if (task) {
      this.phase.tasks = this.phase.tasks.filter(t => t.id !== task.id);
      this.onArranged.emit();
    }
  }

  showRemoveDialog(): void {
    const dialogRef = this.dialog.open(GenericDialogComponent, {
      data: {
        topic: "Remove Phase",
        description: `You surely wan't to delete "${this.phase.label}" phase?`,
        acceptLabel: "Remove",
        onAccept: () => {
          this.phaseService.remove(this.boardId, this.phase.id).subscribe({
            next: message => {
              this.onRemoved.emit(this.phase);
              dialogRef.close();
            },
            error: err => {
              console.error(err);
              dialogRef.close();
            }
          });
        }
      }
    });
  }

  drop(event: CdkDragDrop<Task[]>): void {
    if (event.previousContainer === event.container && event.previousIndex === event.currentIndex)
      return;

    if (event.previousContainer === event.container) {
      moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
      this.taskService.move(
        this.boardId,
        this.extractId(event.container.id),
        event.container.data[event.currentIndex].id,
        this.extractId(event.container.id),
        event.currentIndex)
        .subscribe({
          next: value => {
            console.log(value);
            this.onArranged.emit(event.container.data[event.currentIndex]);
          },
          error(err) {
            console.error(err);
          },
        });
    } else {
      transferArrayItem(
        event.previousContainer.data,
        event.container.data,
        event.previousIndex,
        event.currentIndex,
      );
      event.previousContainer.data.forEach((t, i) => t.pos = i);
      this.taskService.move(
        this.boardId,
        this.extractId(event.previousContainer.id),
        event.container.data[event.currentIndex].id,
        this.extractId(event.container.id),
        event.currentIndex)
        .subscribe({
          next: value => {
            console.log(value);
            this.onArranged.emit(event.container.data[event.currentIndex]);
          },
          error(err) {
            console.error(err);
          },
        });
    }
    event.container.data.forEach((t, i) => t.pos = i);
  }

  taskLabelValidator(): ValidatorFn {
    return (control: AbstractControl): { [key: string]: any } | null => {
      const label = control.value;
      return this.taskLabels.includes(label) ? { preservedLabel: true } : null;
    }
  }
}
