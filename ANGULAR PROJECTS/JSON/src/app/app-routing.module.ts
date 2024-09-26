import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SignupComponent } from './signup/signup.component';
import { UserListComponent } from './user-list/user-list.component';

const routes: Routes = [
   { path: 'signup', component: SignupComponent },
  { path: 'user-list', component: UserListComponent },
  { path: '', redirectTo: '/signup', pathMatch: 'full' } // Redirect to /signup by default
];

@NgModule({
  imports: [RouterModule.forRoot(routes),],
  exports: [RouterModule]
})
export class AppRoutingModule { }
