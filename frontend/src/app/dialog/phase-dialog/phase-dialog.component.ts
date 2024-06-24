import { DIALOG_DATA, DialogRef } from '@angular/cdk/dialog';
import { Component, Inject } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Phase } from '../../model/phase';
import { PhaseService } from '../../service/phase.service';

export interface PhaseDialogData {
  boardId: number,
  phase: Phase,
  editable: boolean;
}

@Component({
  selector: 'app-phase-dialog',
  templateUrl: './phase-dialog.component.html',
  styleUrl: './phase-dialog.component.css'
})
export class PhaseDialogComponent {
  public submitForm: FormGroup;
  public errorResponse = {
    isActive: false,
    message: null
  };

  get label(): AbstractControl {
    return this.submitForm.controls['label'];
  }

  constructor(
    private dialogRef: DialogRef<Phase>,
    @Inject(DIALOG_DATA) public data: PhaseDialogData,
    private phaseService: PhaseService,
    private fb: FormBuilder
  ) {
    this.submitForm = fb.group({
      label: [data.phase ? data.phase.label : null, Validators.required],
      description: [data.phase ? data.phase.description : null]
    })
    !data.editable && this.submitForm.controls['label'].disable();
    !data.editable && this.submitForm.controls['description'].disable();

    this.submitForm.valueChanges.subscribe({
      next: value => {
        this.errorResponse.isActive = false;
      }
    })
  }

  onClose(): void {
    this.dialogRef.close(this.data.phase);
  }

  onSubmit(event: Event): void {
    event.preventDefault();
    if (this.data.phase)
      this.update();
    else
      this.upload();
  }

  private upload(): void {
    this.phaseService.upload(
      this.data.boardId,
      this.submitForm.value.label,
      this.submitForm.value.description)
      .subscribe({
        next: phase => {
          this.dialogRef.close(phase);
        },
        error: err => {
          console.error(err);
          this.errorResponse.message = err.error;
          this.errorResponse.isActive = true;
        }
      })
  }

  private update(): void {
    this.phaseService.update(
      this.data.boardId,
      this.data.phase.id,
      this.submitForm.value.label,
      this.submitForm.value.description
    ).subscribe({
      next: phase => {
        this.dialogRef.close(phase);
      },
      error: err => {
        console.error(err);
        this.errorResponse.message = err.error;
        this.errorResponse.isActive = true;
      }
    });
  }
}
