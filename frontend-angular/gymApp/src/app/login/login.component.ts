import {Component, OnDestroy, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {AuthenticationService} from "../service/authentication.service";
import {NotifierService} from "angular-notifier";
import {User} from "../model/user_model";
import {Subscription} from "rxjs";
import {HttpErrorResponse, HttpResponse} from "@angular/common/http";
import {NotificationType} from "../enum/notification-type.enum";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit, OnDestroy {

  public showLoading: boolean = false;

  private subscriptions: Subscription[] = [];

  constructor(private router: Router,
              private authenticationService: AuthenticationService,
              private notifier: NotifierService) { }

  ngOnInit(): void {
    this.showLoading = false;
    if (this.authenticationService.isUserLoggedIn()) {
      this.router.navigateByUrl('/dashboard/user_profile');
    } else {
      this.router.navigateByUrl('/login');
    }
  }

  ngOnDestroy(): void{
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  public onLogin(user: User): void {
    this.showLoading = true;
    this.subscriptions.push(
      this.authenticationService.login(user).subscribe(
        (response: HttpResponse<User>) => {
          const token = response.headers.get('Jwt-Token');
          this.authenticationService.saveToken(token);
          this.authenticationService.addUserToLocalCache(response.body);
          this.router.navigateByUrl('/dashboard');
          this.showLoading = false;
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendErrorNotification(NotificationType.ERROR, errorResponse.error.message);
          this.showLoading = false;
        }
      )
    );
  }


  private sendErrorNotification(notificationType: NotificationType, message: string) {
    if(message){
      this.notifier.notify(notificationType,message);
    } else {
      this.notifier.notify(notificationType,"An error occured")
    }
  }
}
