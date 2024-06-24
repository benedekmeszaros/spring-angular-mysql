import { Dialog } from '@angular/cdk/dialog';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { GenericDialogComponent } from '../../dialog/generic-dialog/generic-dialog.component';
import { InvitationDialogComponent } from '../../dialog/invitation-dialog/invitation-dialog.component';
import { ProfileDialogComponent } from '../../dialog/profile-dialog/profile-dialog.component';
import { Membership } from '../../model/membership';
import { MemberService } from '../../service/member.service';

@Component({
  selector: 'app-member',
  templateUrl: './member.component.html',
  styleUrl: './member.component.css'
})
export class MemberComponent {
  @Output()
  public onRemoved: EventEmitter<Membership> = new EventEmitter();

  @Input()
  public member: Membership;
  @Input()
  public access: string;
  @Input()
  public ownerId: number;

  constructor(
    private memberService: MemberService,
    private dialog: Dialog
  ) { }

  hasAccess(required: string[]): boolean {
    return required.includes(this.access);
  }

  showProfielDialog(): void {
    this.dialog.open(ProfileDialogComponent, { data: { user: this.member.user } });
  }

  showUpdateDialog(): void {
    this.dialog.open<string>(InvitationDialogComponent, {
      data: {
        boardId: this.member.boardId,
        member: this.member
      }
    }).closed.subscribe({
      next: access => {
        if (access)
          this.member.access = access.toLocaleUpperCase();
      }
    });
  }

  showRemoveDialog(): void {
    const dialogRef = this.dialog.open(GenericDialogComponent,
      {
        data: {
          topic: "Remove Member",
          description: `Are you surely want to remove "${this.member.user.fullName}" from this board?`,
          acceptLabel: "Remove",
          onAccept: () => {
            this.memberService.remove(this.member.boardId, this.member.id).subscribe({
              next: message => {
                console.log(message);
                this.onRemoved.emit(this.member);
                dialogRef.close();
              },
              error: err => {
                console.error(err);
                dialogRef.close();
              }
            });
          }
        }
      }
    )
  }
}
