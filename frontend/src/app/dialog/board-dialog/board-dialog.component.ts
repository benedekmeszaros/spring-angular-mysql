import { DialogRef } from '@angular/cdk/dialog';
import { Component } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { BoardService } from '../../service/board.service';

@Component({
  selector: 'app-board-dialog',
  templateUrl: './board-dialog.component.html',
  styleUrl: './board-dialog.component.css'
})
export class BoardDialogComponent {
  public submitForm: FormGroup;
  public errorResponse = {
    isActive: false,
    message: null
  };

  get title(): AbstractControl {
    return this.submitForm.controls['title'];
  }

  get description(): AbstractControl {
    return this.submitForm.controls['description'];
  }

  constructor(
    private dialogRef: DialogRef,
    private boardService: BoardService,
    private router: Router,
    private fb: FormBuilder
  ) {
    this.submitForm = fb.group({
      title: [null, Validators.required],
      description: [null]
    });
    this.submitForm.valueChanges.subscribe({
      next: () => {
        this.errorResponse.isActive = false;
      }
    })
  }

  onClose(): void {
    this.dialogRef.close();
  }

  onSubmit(event: Event): void {
    event.preventDefault();
    this.boardService.upload(
      this.submitForm.value.title,
      this.submitForm.value.description
    ).subscribe({
      next: boardId => {
        this.router.navigate([`dashboard/boards/${boardId}`]);
        this.dialogRef.close();
      },
      error: err => {
        console.error(err);
        this.errorResponse.message = err.error;
        this.errorResponse.isActive = true;
      }
    });
  }
}
