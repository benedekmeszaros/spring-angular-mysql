import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Router } from '@angular/router';
import { Invitation } from '../../model/invitation';
import { MemberService } from '../../service/member.service';

@Component({
  selector: 'app-invitation',
  templateUrl: './invitation.component.html',
  styleUrl: './invitation.component.css'
})
export class InvitationComponent {
  @Output()
  public onDecision: EventEmitter<Invitation> = new EventEmitter();

  @Input()
  public invitation: Invitation;

  constructor(
    private memberService: MemberService,
    private router: Router
  ) { }

  onDeny(): void {
    this.memberService.deny(this.invitation.board.id, this.invitation.id).subscribe({
      next: message => {
        console.log(message);
        this.onDecision.emit(this.invitation);
      },
      error: err => {
        console.error(err);
      }
    })
  }

  onAccept(): void {
    this.memberService.accept(this.invitation.board.id, this.invitation.id).subscribe({
      next: member => {
        this.router.navigate([`dashboard/boards/${this.invitation.board.id}`])
        this.onDecision.emit(this.invitation);
      },
      error: err => {
        console.error(err);
      }
    })
  }
}
