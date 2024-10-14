import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {HTTP_INTERCEPTORS, HttpClient, HttpClientModule} from "@angular/common/http";
import {AuthenticationService} from "./service/authentication.service";
import {UserService} from "./service/user.service";
import {AuthInterceptor} from "./interceptor/auth.interceptor";
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
import {WorkoutService} from "./service/workout.service";
import {MembershipService} from "./service/membership.service";
import {ScoreService} from "./service/score.service";
import {NgxPaginationModule} from "ngx-pagination";
import {TranslateLoader, TranslateModule} from "@ngx-translate/core";
import {TranslateHttpLoader} from "@ngx-translate/http-loader";


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
    FormsModule,
    NgxPaginationModule,
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: HttpLoaderFactory,
        deps: [HttpClient],
      },
    }),
  ],
  providers: [NotificationService, AuthenticationService, UserService,WorkoutService,MembershipService,ScoreService,
    {provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true}],
  bootstrap: [AppComponent]
})
export class AppModule {
}

export function HttpLoaderFactory(http: HttpClient) {
  return new TranslateHttpLoader(http, './assets/i18n/', '.json');
}
