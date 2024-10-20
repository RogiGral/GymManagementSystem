import {Component, OnDestroy, OnInit} from '@angular/core';
import {WorkoutService} from "../service/workout.service";
import {NotificationService} from "../service/notification.service";
import {AuthenticationService} from "../service/authentication.service";
import {Subscription} from "rxjs";
import {User} from "../model/user_model";
import {IUserWorkout, IWorkout} from "../model/workout_model";
import {NotificationType} from "../enum/notification-type.enum";
import {HttpErrorResponse} from "@angular/common/http";
import {CustomHttpResponse} from "../model/custom-http-response_model";
import {Role} from "../enum/role.enum";
import {UserService} from "../service/user.service";


@Component({
  selector: 'app-workout',
  templateUrl: './workout.component.html',
  styleUrls: ['./workout.component.css']
})
export class WorkoutComponent implements OnInit, OnDestroy {

  public refreshing = false;
  public selectedWorkout: IWorkout = new IWorkout();
  public workouts: IWorkout[] = [];
  public filteredWorkouts: IWorkout[] = [];
  public editWorkout = new IWorkout();
  public listOfTrainers: User[] = [];
  public userWorkouts: IUserWorkout[] = [];
  public selectedUserWorkout: IWorkout = new IWorkout();
  public todayDate: string = new Date().toISOString().split('T')[0];
  public selectedDate: string = new Date().toISOString().split('T')[0];

  private subscriptions: Subscription[] = [];
  private currentWorkout: number = 0;
  public currentWorkoutsPage: number = 1;
  public currentUserWorkoutsPage: number = 1;
  public currentTrainerWorkoutsPage: number = 1;

  constructor(
    private workoutService: WorkoutService,
    private userService: UserService,
    private notificationService: NotificationService,
    private authenticationService: AuthenticationService
  ) { }

  ngOnInit(): void {
    this.getWorkouts(true);
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  public getWorkouts(showNotification: boolean): void {
    this.refreshing = true;
    this.subscriptions.push(
      this.workoutService.getWorkouts().subscribe(
        (response: IWorkout[]) => {
          this.workouts = response;
          this.onDateChange()
          this.refreshing = false;
          if (showNotification) {
            this.sendNotification(NotificationType.SUCCESS, `${response.length} workout(s) loaded successfully.`);
          }
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
          this.refreshing = false;
        }
      ),
      this.workoutService.getUserWorkouts(this.authenticationService.getUserFromLocalCache().id).subscribe(
        (response: IUserWorkout[]) => {
          this.userWorkouts = response;
          this.refreshing = false;
          if (showNotification) {
            this.sendNotification(NotificationType.SUCCESS, `${response.length} user workout(s) loaded successfully.`);
          }
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
          this.refreshing = false;
        }
      )
    );
  }

  onDateChange(): void {
    this.filteredWorkouts = this.workouts.filter(workout => {
      const workoutStartDate = new Date(workout.workoutStartDate).toLocaleDateString().split('T')[0];
      const selectedDate = new Date(this.selectedDate).toLocaleDateString().split('T')[0];
      return workoutStartDate === selectedDate;
    });
  }
  addDay(): void {
    this.currentWorkoutsPage = 1;
    const currentDate = new Date(this.selectedDate);
    currentDate.setDate(currentDate.getDate() + 1);
    this.selectedDate = currentDate.toISOString().split('T')[0];
    this.getWorkouts(false)
    this.onDateChange();
  }

  removeDay(): void {
    this.currentWorkoutsPage = 1;
    const currentDate = new Date(this.selectedDate);
    currentDate.setDate(currentDate.getDate() - 1);
    this.selectedDate = currentDate.toISOString().split('T')[0];
    this.getWorkouts(false)
    this.onDateChange();
  }

  disableRemoveDayButton(): boolean {
    if(this.todayDate==this.selectedDate){
      return true
    }
    return  false
  }

  private sendNotification(notificationType: NotificationType, message: string): void {
    if (message) {
      this.notificationService.notify(notificationType, message);
    } else {
      this.notificationService.notify(notificationType, 'An error occurred. Please try again.');
    }
  }

  public onSelectWorkout(selectedWorkout: IWorkout): void {
    this.selectedWorkout = selectedWorkout;
    this.clickButton('openWorkoutInfo');
  }

  public onSelectUserWorkout(selectedWorkout: IUserWorkout): void {
    this.selectedUserWorkout = selectedWorkout.workout;
    this.clickButton('openUserWorkoutInfo');
  }

  public onEditWorkout(editWorkout: IWorkout): void {
    this.editWorkout = editWorkout;
    this.currentWorkout = editWorkout.id;
    this.clickButton('openWorkoutEdit');
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

  private clickButton(buttonId: string): void {
    document.getElementById(buttonId)!.click();
  }

  onJoinWorkout(workout: IWorkout) {
    const formData = this.workoutService.createWorkoutFormJoinData(this.authenticationService.getUserFromLocalCache(),workout)
    this.subscriptions.push(
      this.workoutService.joinWorkout(formData).subscribe(
        (response: IUserWorkout) => {
          this.sendNotification(NotificationType.SUCCESS,`${response.user.username} added successfully to workout: ${response.workout.workoutName}`);
          this.getWorkouts(false);
        },
        (error: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, error.error.message);
        }
      )
    );
  }

  onLeaveWorkout(id: number) {
    this.subscriptions.push(
      this.workoutService.deleteUserWorkout(id).subscribe(
        (response: CustomHttpResponse) => {
          this.sendNotification(NotificationType.SUCCESS, response.message);
          this.getWorkouts(false);
        },
        (error: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, error.error.message);
        }
      )
    );
  }

}
