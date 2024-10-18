import {Component, OnDestroy, OnInit} from '@angular/core';
import {NotificationType} from "../enum/notification-type.enum";
import {NotificationService} from "../service/notification.service";
import {AuthenticationService} from "../service/authentication.service";
import {Router} from "@angular/router";
import {User} from "../model/user_model";
import {NgForm} from "@angular/forms";
import {HttpErrorResponse} from "@angular/common/http";
import {UserService} from "../service/user.service";
import {CustomHttpResponse} from "../model/custom-http-response_model";
import {interval, Subscription} from "rxjs";
import {MembershipService} from "../service/membership.service";
import {IUserMembership} from "../model/membership_model";
import {QrcodeService} from "../service/qrcode.service";
import { v4 as uuidv4 } from 'uuid';

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.css']
})
export class UserProfileComponent implements OnInit, OnDestroy {

  public userMembership: IUserMembership;

  public user: User;
  public qrCodeUUID: string;
  public generatedQRCode: string | null = null;
  public expirationDate: Date = new Date();
  public remainingTime: string = ""; // This will show the remaining time
  public timerSubscription: Subscription = new Subscription();
  private subscriptions: Subscription[] = [];


  constructor(private notificationService: NotificationService,
              private authenticationService: AuthenticationService,
              private membershipService: MembershipService,
              private userService: UserService,
              private qrCodeService: QrcodeService,
              private router: Router) {
    this.expirationDate = new Date(new Date().getTime() + 2 * 60 * 1000);
  }

  ngOnInit(): void {
    this.user = this.authenticationService.getUserFromLocalCache()
    this.membershipService.getUserMembership(this.user.id).subscribe(
      (response: IUserMembership) => {
        this.userMembership = response
      }
    )
    this.qrCodeUUID = this.addQrCode(JSON.stringify({user: this.user.userId}));
    this.startTimer();

  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  public onLogOut(): void {
    this.authenticationService.logOut();
    this.router.navigate(['/login']);
    this.sendNotification(NotificationType.SUCCESS, `You've been successfully logged out`);
  }

  public goToMemberships(): void{
    this.router.navigate(['/dashboard/memberships']);
  }

  private sendNotification(notificationType: NotificationType, message: string): void {
    if (message) {
      this.notificationService.notify(notificationType, message);
    } else {
      this.notificationService.notify(notificationType, 'An error occurred. Please try again.');
    }
  }

  startTimer() {
    this.timerSubscription = interval(1000).subscribe(() => {
      const now = new Date();
      const timeLeft = this.expirationDate.getTime() - now.getTime();

      if (timeLeft > 0) {
        this.remainingTime = this.formatTime(timeLeft);
      } else {
        this.remainingTime = 'QR code expired';
        this.timerSubscription.unsubscribe();
      }
    });
  }

  renewQrCode() {
    this.qrCodeUUID = this.addQrCode(JSON.stringify({user: this.user.userId})); // Renew QR code
    this.expirationDate = new Date(new Date().getTime() + 2 * 60 * 1000); // Reset expiration to 2 minutes
    this.startTimer();
  }


  formatTime(ms: number) {
    const totalSeconds = Math.floor(ms / 1000);
    const minutes = Math.floor(totalSeconds / 60);
    const seconds = totalSeconds % 60;

    return `${minutes}m ${seconds}s`;
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


  public addQrCode(data: string){
    const uuid = uuidv4()
    const formData = this.qrCodeService.createQrCodeFormData(uuid,data)
    this.subscriptions.push(
      this.qrCodeService.addQrCode(formData).subscribe(
        (response: any) => {
          this.sendNotification(NotificationType.SUCCESS, "QR code created");
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
        }
      )
    )
    return uuid
  }

  public isActive() {
    const todayDate = new Date().toLocaleDateString().split('T')[0];
    const userMembershipEndDate = new Date(this.userMembership!.endDate).toLocaleDateString().split('T')[0];

    if(todayDate<=userMembershipEndDate){
      return true;
    } else {
      return false;
    }
  }

  cancelMembership() {
    this.subscriptions.push(
      this.membershipService.leaveMembership(this.userMembership.userId.id).subscribe(
        (response: CustomHttpResponse) => {
          this.sendNotification(NotificationType.SUCCESS, response.message);
        },
        (error: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, error.error.message);
        },() =>{
          this.sleep(2000).then(() => { window.location.reload(); });

        }
      )
    );
  }
  sleep(ms:number) {
    return new Promise(resolve => setTimeout(resolve, ms));
  }
}
