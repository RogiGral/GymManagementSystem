import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {AuthenticationService} from "./service/authentication.service";
import {UserService} from "./service/user.service";
import {AuthInterceptor} from "./interceptor/auth.interceptor";
import {NotificationModule} from "./notification.module";
import {NotificationService} from "./service/notification.service";
import {LoginComponent} from './login/login.component';
import {UserComponent} from './user/user.component';
import {RegisterComponent} from './register/register.component';
import {FormsModule} from "@angular/forms";
import {LandingPageComponent} from './landing-page/landing-page.component';
import { MainComponent } from './main/main.component';
import { WorkoutComponent } from './workout/workout.component';
import { MembershipComponent } from './membership/membership.component';
import { UserProfileComponent } from './user-profile/user-profile.component';
import {ResetPasswordComponent} from "./reset-password/reset-password.component";

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    UserComponent,
    RegisterComponent,
    LandingPageComponent,
    MainComponent,
    WorkoutComponent,
    MembershipComponent,
    UserProfileComponent,
    ResetPasswordComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    NotificationModule,
    FormsModule
  ],
  providers: [NotificationService, AuthenticationService, UserService,
    {provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true}],
  bootstrap: [AppComponent]
})
export class AppModule {
}
