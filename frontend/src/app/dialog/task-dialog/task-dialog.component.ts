import { DIALOG_DATA, DialogRef } from '@angular/cdk/dialog';
import { Component, Inject } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Task } from '../../model/task';
import { TaskService } from '../../service/task.service';

export interface TaskDialogData {
  boardId: number,
  phaseId: number,
  task: Task,
  editable: boolean
}

@Component({
  selector: 'app-task-dialog',
  templateUrl: './task-dialog.component.html',
  styleUrl: './task-dialog.component.css'
})
export class TaskDialogComponent {
  public submitForm: FormGroup;
  public errorResponse = {
    isActive: false,
    message: null
  };

  get label(): AbstractControl {
    return this.submitForm.controls['label'];
  }

  constructor(
    public dialogRef: DialogRef<Task>,
    @Inject(DIALOG_DATA) public data: TaskDialogData,
    private taskService: TaskService,
    private fb: FormBuilder
  ) {
    this.submitForm = fb.group({
      label: [data.task ? data.task.label : null, Validators.required],
      description: [data.task ? data.task.description : null]
    })

    !data.editable && this.submitForm.controls['label'].disable();
    !data.editable && this.submitForm.controls['description'].disable();

    this.submitForm.valueChanges.subscribe({
      next: () => {
        this.errorResponse.isActive = false;
      }
    })
  }

  onClose(): void {
    this.dialogRef.close(this.data.task);
  }

  onSubmit(event: Event): void {
    event.preventDefault();
    this.taskService.update(
      this.data.boardId,
      this.data.phaseId,
      this.data.task.id,
      this.submitForm.value.label,
      this.submitForm.value.description
    ).subscribe({
      next: task => {
        this.dialogRef.close(task);
      },
      error: err => {
        console.error(err);
        this.errorResponse.message = err.error;
        this.errorResponse.isActive = true;
      }
    });
  }
}
