import { Component, OnInit } from '@angular/core';
import {NotificationType} from "../enum/notification-type.enum";
import {NotificationService} from "../service/notification.service";
import {AuthenticationService} from "../service/authentication.service";
import {Router} from "@angular/router";
import {User} from "../model/user_model";
import {NgForm} from "@angular/forms";
import {IWorkout} from "../model/workout_model";
import {HttpErrorResponse} from "@angular/common/http";
import {UserService} from "../service/user.service";
import {CustomHttpResponse} from "../model/custom-http-response_model";
import {Subscription} from "rxjs";

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.css']
})
export class UserProfileComponent implements OnInit {

  public user: User;
  private subscriptions: Subscription[] = [];

  constructor(private notificationService: NotificationService,
              private authenticationService: AuthenticationService,
              private userService: UserService,
              private router: Router) { }

  ngOnInit(): void {
    this.user = this.authenticationService.getUserFromLocalCache()
  }

  public onLogOut(): void {
    this.authenticationService.logOut();
    this.router.navigate(['/login']);
    this.sendNotification(NotificationType.SUCCESS, `You've been successfully logged out`);
  }

  private sendNotification(notificationType: NotificationType, message: string): void {
    if (message) {
      this.notificationService.notify(notificationType, message);
    } else {
      this.notificationService.notify(notificationType, 'An error occurred. Please try again.');
    }
  }

  public onSetNewPassword(newPasswordForm: NgForm): void {
    const oldPassword = newPasswordForm.value.oldPassword;
    const newPassword = newPasswordForm.value.newPassword;
    const newPasswordRepeat = newPasswordForm.value.newPasswordRepeat;

    if (newPassword !== newPasswordRepeat) {
      this.sendNotification(NotificationType.ERROR, 'New passwords do not match');
      return;
    }

    this.subscriptions.push(
      this.userService.setPassword(this.user.email, oldPassword, newPassword).subscribe(
        (response: CustomHttpResponse) => {
          newPasswordForm.reset();
          this.sendNotification(NotificationType.SUCCESS, response.message);
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
        }
      )
    );
  }
}
