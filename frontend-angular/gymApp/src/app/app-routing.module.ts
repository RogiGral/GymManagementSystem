import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {LoginComponent} from "./login/login.component";
import {RegisterComponent} from "./register/register.component";
import {UserComponent} from "./user/user.component";
import {AuthenticationGuard} from "./guard/authentication-guard.service";
import {LandingPageComponent} from "./landing-page/landing-page.component";
import {UserProfileComponent} from "./user-profile/user-profile.component";
import {WorkoutComponent} from "./workout/workout.component";
import {MembershipComponent} from "./membership/membership.component";
import {MainComponent} from "./main/main.component";
import {ResetPasswordComponent} from "./reset-password/reset-password.component";
const routes: Routes = [
  {path: 'login', component: LoginComponent},
  {path: 'register', component: RegisterComponent},
  {path: 'landing-page', component: LandingPageComponent},
  {path: 'reset-password', component: ResetPasswordComponent},
  {
    path: 'dashboard', component: MainComponent, canActivate: [AuthenticationGuard], children: [
      {path: 'users', component: UserComponent, canActivate: [AuthenticationGuard]},
      {path: 'user_profile', component: UserProfileComponent, canActivate: [AuthenticationGuard]},
      {path: 'workouts', component: WorkoutComponent, canActivate: [AuthenticationGuard]},
      {path: 'memberships', component: MembershipComponent, canActivate: [AuthenticationGuard]},
    ]
  },


  {path: '', redirectTo: '/landing-page', pathMatch: 'full'}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
