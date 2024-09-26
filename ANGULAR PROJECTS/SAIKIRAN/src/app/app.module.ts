// app.module.ts
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';
import { AppComponent } from './app.component';
import { LoginComponent } from './login/login.component';
import { NavBarComponent } from './nav-bar/nav-bar.component';
import { EmployeeFormComponent } from './employee-form/employee-form.component';
import { AppRoutingModule } from './app-routing.module';
import { SignupComponent } from './signup/signup.component';
import { AuthModule } from './auth/auth.module';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { AppInterceptor } from './app.interceptor';
import { ForgotPasswordComponent } from './forgot-password/forgot-password.component';
import { MovieComponent } from './movie/movie.component';
import { BsDropdownModule } from 'ngx-bootstrap/dropdown';

@NgModule({
  imports: [BrowserModule, FormsModule,AppRoutingModule,AuthModule,ReactiveFormsModule,HttpClientModule,BsDropdownModule.forRoot(),
     RouterModule.forRoot([
    { path: 'login', component: LoginComponent },
    { path: 'signup', component: SignupComponent }, ])],
  declarations: [AppComponent, LoginComponent, NavBarComponent, EmployeeFormComponent, SignupComponent, ForgotPasswordComponent, MovieComponent,],
  bootstrap: [AppComponent],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: AppInterceptor, multi: true },
  ],
})
export class AppModule {}
