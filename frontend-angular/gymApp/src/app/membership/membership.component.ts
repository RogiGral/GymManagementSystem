import {Component, OnDestroy, OnInit} from '@angular/core';
import {NotificationService} from "../service/notification.service";
import {AuthenticationService} from "../service/authentication.service";
import {Role} from "../enum/role.enum";
import {MembershipService} from "../service/membership.service";
import {IMembershipType, IUserMembership} from "../model/membership_model";
import {Subscription} from "rxjs";
import {NotificationType} from "../enum/notification-type.enum";
import {HttpErrorResponse} from "@angular/common/http";
import {CustomHttpResponse} from "../model/custom-http-response_model";
import {NgForm} from "@angular/forms";
import {IUserWorkout} from "../model/workout_model";

@Component({
  selector: 'app-membership',
  templateUrl: './membership.component.html',
  styleUrls: ['./membership.component.css']
})
export class MembershipComponent implements OnInit, OnDestroy {

  private subscriptions: Subscription[] = [];
  public refreshing: boolean;
  public memberships: IMembershipType[];
  public selectedMembershipType: IMembershipType;
  private currentMembershipType: any;
  public editMembershipType = new IMembershipType();
  public todayDate = new Date();
  public paymentIntentData: any;

  constructor(
    private membershipService: MembershipService,
    private notificationService: NotificationService,
    private authenticationService: AuthenticationService
  ) { }

  ngOnInit(): void {
    this.getMemberships(true);
  }


  ngOnDestroy(): void{
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  public getMemberships(showNotification: boolean): void {
    this.refreshing = true;
    this.subscriptions.push(
      this.membershipService.getMembership().subscribe(
        (response: IMembershipType[]) => {
          this.memberships = response;
          this.refreshing = false;
          if (showNotification) {
            this.sendNotification(NotificationType.SUCCESS, `${response.length} membership(s) loaded successfully.`);
          }
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
          this.refreshing = false;
        }
      )
    );
  }

  public onDeleteMembershipType(id: number): void {
    this.subscriptions.push(
      this.membershipService.deleteMembershipType(id).subscribe(
        (response: CustomHttpResponse) => {
          this.sendNotification(NotificationType.SUCCESS, response.message);
          this.getMemberships(false);
        },
        (error: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, error.error.message);
        }
      )
    );
  }

  public onAddNewMembershipType(newMembership: NgForm): void {
    const formData = this.membershipService.createMembershipTypeFormDate(null,newMembership.value);
    this.subscriptions.push(
      this.membershipService.addMembershipType(formData).subscribe(
        (response: IMembershipType) => {
          this.getMemberships(false);
          this.clickButton('new-membership-close');
          this.sendNotification(NotificationType.SUCCESS, response.name + ' added successfully.');
        },
        (error: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, error.error.message);
        }
      )
    );
  }
  public onUpdateMembershipType(): void {
    const formData = this.membershipService.createMembershipTypeFormDate(this.currentMembershipType, this.editMembershipType);
    this.subscriptions.push(
      this.membershipService.updateMembershipType(formData).subscribe(
        (response: IMembershipType) => {
          this.clickButton('closeEditMembershipTypeModalButton');
          this.getMemberships(false);
          this.sendNotification(NotificationType.SUCCESS, `${response.type} updated successfully`);
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
        }
      )
    );
  }

  public onEditMembershipType(editMembershipType: IMembershipType): void {
    this.editMembershipType = editMembershipType;
    this.currentMembershipType = editMembershipType.name;
    this.clickButton('openMembershipTypeEdit');
  }

  public onSelectMembership(selectedMembership: IMembershipType): void {
    this.selectedMembershipType = selectedMembership;
    this.clickButton('openMembershipTypeInfo');
  }

  public onPayForMembership(selectedMembership: IMembershipType): void {
    this.selectedMembershipType = selectedMembership;
    this.createPaymentIntent();
    this.clickButton('openPayForMembership');
  }

  public saveNewMembership(): void {
    this.clickButton('new-membership-save');
  }

  public get isAdmin(): boolean {
    return this.getUserRole() === Role.ADMIN || this.getUserRole() === Role.SUPER_ADMIN;
  }

  public get isCoach(): boolean {
    return this.isAdmin || this.getUserRole() === Role.COACH;
  }

  public get isAdminOrCoach(): boolean {
    return this.isAdmin || this.isCoach;
  }

  private getUserRole(): string {
    return this.authenticationService.getUserFromLocalCache().role;
  }

  private sendNotification(notificationType: NotificationType, message: string): void {
    if (message) {
      this.notificationService.notify(notificationType, message);
    } else {
      this.notificationService.notify(notificationType, 'An error occurred. Please try again.');
    }
  }

  private clickButton(buttonId: string): void {
    document.getElementById(buttonId)!.click();
  }

  onJoinMembership(membershipType: IMembershipType)  {

    const formData = this.membershipService.createMembershipFormJoinData(
      this.authenticationService.getUserFromLocalCache(),
      membershipType)

    this.subscriptions.push(
      this.membershipService.joinMembership(formData).subscribe(
        (response: IUserMembership) => {
          this.clickButton('closePaymentFormButton');
          this.sendNotification(NotificationType.SUCCESS,`${response.userId.username} added successfully to membership: ${response.membershipTypeId.type}`);
          this.getMemberships(false);
        },
        (error: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, error.error.message);
        } ,() =>{
          this.sleep(2000).then(() => { window.location.reload(); });
        }
      )
    );
  }

  createPaymentIntent()  {

    const formData = this.membershipService.createPaymentIntentFormData("pln",
      this.selectedMembershipType.price,
      this.authenticationService.getUserFromLocalCache().userId)

    this.subscriptions.push(
      this.membershipService.createPaymentIntent(formData).subscribe(
        (response: string) => {
          this.paymentIntentData = response
          this.sendNotification(NotificationType.INFO,`Payment created`);
          this.getMemberships(false);
        },
        (error: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, error.error.message);
        }
      )
    );
  }
  confirmPaymentIntent()  {

    const formData = this.membershipService.setPaymentIntentStatusFormData(this.paymentIntentData.id)

    this.subscriptions.push(
      this.membershipService.confirmPaymentIntent(formData).subscribe(
        (response: string) => {
          this.paymentIntentData = response
          this.sendNotification(NotificationType.INFO,`Payment confirmed`);
          this.getMemberships(false);
        },
        (error: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, error.error.message);
        }
      )
    );
  }

  cancelPaymentIntent() {

    const formData = this.membershipService.setPaymentIntentStatusFormData(this.paymentIntentData.id)

    this.subscriptions.push(
      this.membershipService.cancelPaymentIntent(formData).subscribe(
        (response: string) => {
          this.paymentIntentData = response
          this.sendNotification(NotificationType.INFO, `Payment canceled`);
          this.getMemberships(false);
        },
        (error: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, error.error.message);
        }
      )
    );
  }


  onSendPaymentForm(paymentForm: NgForm) {
    this.confirmPaymentIntent();
    this.onJoinMembership(this.selectedMembershipType)
  }

  sleep(ms:number) {
    return new Promise(resolve => setTimeout(resolve, ms));
  }
}
