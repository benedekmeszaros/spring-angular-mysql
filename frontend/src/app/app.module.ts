import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { DialogModule } from '@angular/cdk/dialog';
import { DragDropModule } from '@angular/cdk/drag-drop';
import { CdkMenuModule } from '@angular/cdk/menu';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { ReactiveFormsModule } from '@angular/forms';
import { NgIconsModule } from '@ng-icons/core';
import { heroArrowLeftOnRectangle, heroArrowTopRightOnSquare, heroChevronLeft, heroEllipsisVertical, heroEye, heroEyeSlash, heroInformationCircle, heroPencil, heroPencilSquare, heroPlus, heroRectangleGroup, heroUser, heroUsers, heroXCircle } from '@ng-icons/heroicons/outline';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BoardDetailsComponent } from './component/board-details/board-details.component';
import { BoardEntryComponent } from './component/board-entry/board-entry.component';
import { BoardMembersComponent } from './component/board-members/board-members.component';
import { BoardComponent } from './component/board/board.component';
import { DashboardComponent } from './component/dashboard/dashboard.component';
import { EditorComponent } from './component/editor/editor.component';
import { InvitationComponent } from './component/invitation/invitation.component';
import { LoginComponent } from './component/login/login.component';
import { MemberComponent } from './component/member/member.component';
import { NavbarComponent } from './component/navbar/navbar.component';
import { PageNotFoundComponent } from './component/page-not-found/page-not-found.component';
import { PhaseComponent } from './component/phase/phase.component';
import { ProfileComponent } from './component/profile/profile.component';
import { ProjectMenuComponent } from './component/project-menu/project-menu.component';
import { RegisterComponent } from './component/register/register.component';
import { SidebarNavComponent } from './component/sidebar/sidebar-nav/sidebar-nav.component';
import { SidebarComponent } from './component/sidebar/sidebar.component';
import { TaskComponent } from './component/task/task.component';
import { BoardDialogComponent } from './dialog/board-dialog/board-dialog.component';
import { GenericDialogComponent } from './dialog/generic-dialog/generic-dialog.component';
import { InvitationDialogComponent } from './dialog/invitation-dialog/invitation-dialog.component';
import { NotificationDialogComponent } from './dialog/notification-dialog/notification-dialog.component';
import { PasswordDialogComponent } from './dialog/password-dialog/password-dialog.component';
import { PhaseDialogComponent } from './dialog/phase-dialog/phase-dialog.component';
import { ProfileDialogComponent } from './dialog/profile-dialog/profile-dialog.component';
import { TaskDialogComponent } from './dialog/task-dialog/task-dialog.component';
import { UsernameDialogComponent } from './dialog/username-dialog/username-dialog.component';
import { httpInterceptor } from './interceptor/http.interceptor';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    PageNotFoundComponent,
    RegisterComponent,
    DashboardComponent,
    BoardComponent,
    PhaseComponent,
    TaskComponent,
    NavbarComponent,
    SidebarComponent,
    EditorComponent,
    ProfileComponent,
    ProjectMenuComponent,
    BoardDetailsComponent,
    BoardMembersComponent,
    SidebarNavComponent,
    BoardEntryComponent,
    PhaseDialogComponent,
    GenericDialogComponent,
    TaskDialogComponent,
    BoardDialogComponent,
    MemberComponent,
    InvitationComponent,
    InvitationDialogComponent,
    NotificationDialogComponent,
    PasswordDialogComponent,
    UsernameDialogComponent,
    ProfileDialogComponent
  ],
  imports: [
    DialogModule,
    CdkMenuModule,
    BrowserModule,
    AppRoutingModule,
    DragDropModule,
    ReactiveFormsModule,
    NgIconsModule.withIcons({
      heroUser, heroRectangleGroup, heroArrowTopRightOnSquare,
      heroUsers, heroInformationCircle, heroPencilSquare,
      heroPlus, heroEllipsisVertical, heroXCircle,
      heroArrowLeftOnRectangle, heroChevronLeft, heroPencil,
      heroEye, heroEyeSlash
    })
  ],
  providers: [provideHttpClient(withInterceptors([httpInterceptor]))],
  bootstrap: [AppComponent]
})
export class AppModule { }
