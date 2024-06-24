import { DIALOG_DATA, DialogRef } from '@angular/cdk/dialog';
import { Component, Inject } from '@angular/core';

export interface GenericDialogData {
  topic: string,
  description: string,
  acceptLabel: string,
  cancelLabel: string,
  onAccept: () => void,
  onCancel: () => void
}

@Component({
  selector: 'app-generic-dialog',
  templateUrl: './generic-dialog.component.html',
  styleUrl: './generic-dialog.component.css'
})
export class GenericDialogComponent {
  public isDisabled: boolean = false;

  constructor(
    public dialogRef: DialogRef,
    @Inject(DIALOG_DATA) public data: GenericDialogData,
  ) {
    if (data) {
      if (!data.topic)
        data.topic = "Topic";
      if (!data.description)
        data.description = "No description";
      if (!data.acceptLabel)
        data.acceptLabel = "OK";
      if (!data.cancelLabel)
        data.cancelLabel = "Cancel";
    }
  }

  onAccept(): void {
    if (this.data?.onAccept) {
      this.isDisabled = true;
      this.data.onAccept();
    }
    else
      this.dialogRef.close();
  }

  onCancel(): void {
    if (this.data?.onCancel) {
      this.isDisabled = true;
      this.data.onCancel();
    }
    else
      this.dialogRef.close();
  }
}
