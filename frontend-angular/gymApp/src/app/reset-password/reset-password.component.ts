import {Component, OnDestroy, OnInit} from '@angular/core';
import {Subscription} from "rxjs";
import {Router} from "@angular/router";
import {AuthenticationService} from "../service/authentication.service";
import {NotificationType} from "../enum/notification-type.enum";
import {User, UserResetPassword} from "../model/user_model";
import {HttpErrorResponse, HttpResponse} from "@angular/common/http";
import {UserService} from "../service/user.service";
import {CustomHttpResponse} from "../model/custom-http-response_model";
import {NotificationService} from "../service/notification.service";

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.css']
})
export class ResetPasswordComponent implements OnInit, OnDestroy {
  public showLoading: boolean = false;

  private subscriptions: Subscription[] = [];

  constructor(private router: Router,
              private userService: UserService,
              private authenticationService: AuthenticationService,
              private notificationService: NotificationService,) { }

  ngOnInit(): void {
  }

  ngOnDestroy(): void{
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  public onResetPassword(userResetPassword: UserResetPassword): void {
    this.showLoading = true;
    this.subscriptions.push(
      this.userService.resetPassword(userResetPassword.email).subscribe(
        (response: CustomHttpResponse) => {
          this.sendErrorNotification(NotificationType.SUCCESS, response.message);
          this.showLoading = false;
        } ,
        (errorResponse: HttpErrorResponse) => {
          this.sendErrorNotification(NotificationType.ERROR, errorResponse.error.message);
          this.showLoading = false;
        }
      )
    );
  }

  private sendErrorNotification(notificationType: NotificationType, message: string) {
    if(message){
      this.notificationService.notify(notificationType,message);
    } else {
      this.notificationService.notify(notificationType,"An error occured")
    }
  }

}
