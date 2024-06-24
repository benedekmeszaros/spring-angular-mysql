import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BoardDetailsComponent } from './component/board-details/board-details.component';
import { BoardMembersComponent } from './component/board-members/board-members.component';
import { BoardComponent } from './component/board/board.component';
import { DashboardComponent } from './component/dashboard/dashboard.component';
import { EditorComponent } from './component/editor/editor.component';
import { LoginComponent } from './component/login/login.component';
import { PageNotFoundComponent } from './component/page-not-found/page-not-found.component';
import { ProfileComponent } from './component/profile/profile.component';
import { ProjectMenuComponent } from './component/project-menu/project-menu.component';
import { RegisterComponent } from './component/register/register.component';
import { authGuard } from './guard/auth.guard';

const routes: Routes = [
  { path: "login", component: LoginComponent },
  { path: "register", component: RegisterComponent },
  { path: "dashboard", redirectTo: "dashboard/boards", pathMatch: "full" },
  {
    path: "dashboard", component: DashboardComponent, canActivate: [authGuard], children: [
      { path: "boards", component: ProjectMenuComponent },
      { path: "profile", component: ProfileComponent }
    ]
  },
  { path: "dashboard/boards/:id", redirectTo: "dashboard/boards/:id/editor", pathMatch: "full" },
  {
    path: "dashboard/boards/:id", component: EditorComponent, canActivate: [authGuard], children: [
      { path: "editor", component: BoardComponent },
      { path: "details", component: BoardDetailsComponent },
      { path: "members", component: BoardMembersComponent }
    ]
  },
  { path: "", redirectTo: "login", pathMatch: "full" },
  { path: "**", component: PageNotFoundComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
