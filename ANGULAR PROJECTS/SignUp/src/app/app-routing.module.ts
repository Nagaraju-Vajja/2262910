import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SignupComponent } from './signup/signup.component';
import { UserListComponent } from './user-list/user-list.component';

const routes: Routes = [
  { path: 'signup', component: SignupComponent },
  { path: 'user-list', component: UserListComponent },
  
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
