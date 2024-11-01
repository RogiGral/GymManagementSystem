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

declare var $: any;


@Component({
  selector: 'app-workout',
  templateUrl: './workout.component.html',
  styleUrls: ['./workout.component.css']
})
export class WorkoutComponent implements OnInit, OnDestroy {

  public currentDate: Date = new Date()
  public refreshing = false
  public selectedWorkout: IWorkout = new IWorkout()
  public selectedDay: Date | null = null;
  public selectedHour: number | null = null;
  public workouts: IWorkout[] = []
  public hours = Array.from({ length: 15 }, (_, i) => i + 8); // 0 to 23 hours
  public isGridDisabled: boolean = false;
  public currentWeekStart: Date;
  public currentWeekEnd: Date;
  public weekDays: Array<{ date: Date }> = [];
  private subscriptions: Subscription[] = []
  private userWorkouts: IUserWorkout[];

  constructor(
    private workoutService: WorkoutService,
    private userService: UserService,
    private notificationService: NotificationService,
    private authenticationService: AuthenticationService
  ) { }

  ngOnInit() {
    this.setWeekDates();
    this.getWorkouts(false)
  }

  isUserInWorkout(workout: IWorkout): boolean {
    return this.userWorkouts.some(userWorkout => userWorkout.workout.id === workout.id);
  }

  setWeekDates() {
    const start = this.getStartOfWeek(this.currentDate);
    const end = this.getEndOfWeek(this.currentDate);
    this.currentWeekStart = start;
    this.currentWeekEnd = end;

    this.weekDays = Array.from({ length: 7 }, (_, i) => ({
      date: new Date(start.getFullYear(), start.getMonth(), start.getDate() + i),
    }));
  }

  getStartOfWeek(date: Date): Date {
    const day = date.getDay(); // Get the current day of the week (0-6)
    const diff = date.getDate() - day + (day === 0 ? -6 : 1); // Adjust when day is Sunday
    return new Date(date.setDate(diff));
  }

  getEndOfWeek(date: Date): Date {
    const startOfWeek = this.getStartOfWeek(date);
    return new Date(startOfWeek.setDate(startOfWeek.getDate() + 6));
  }

  goToPreviousWeek() {
    this.currentDate.setDate(this.currentDate.getDate() - 7);
    this.setWeekDates();
  }

  getWorkoutsForDay(day: Date) {
    return this.workouts.filter(workout => {
      const workoutDate = new Date(workout.workoutStartDate);
      return workoutDate.toDateString() === day.toDateString();
    });
  }

  getWorkoutsForDayAndHour(day: Date, hour: number) {
    return this.getWorkoutsForDay(day).filter(workout => {
      return new Date(workout.workoutStartDate).getHours() === hour;
    });
  }

  goToNextWeek() {
    this.currentDate.setDate(this.currentDate.getDate() + 7);
    this.setWeekDates();
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

  isToday(date: Date): boolean {
    const today = new Date();
    return date.getDate() === today.getDate() &&
      date.getMonth() === today.getMonth() &&
      date.getFullYear() === today.getFullYear();
  }

  onEmptyCellClick(day: Date, hour: number) {
    this.selectedDay = day;
    this.selectedHour = hour;
    const startDate = new Date(day);
    startDate.setHours(hour, 0, 0);
    const endDate = new Date(startDate);
    endDate.setHours(hour + 1, 0, 0);
    this.selectedWorkout.workoutStartDate = this.formatDateForInput(startDate);
    this.selectedWorkout.workoutEndDate = this.formatDateForInput(endDate);
    $('#addWorkoutModal').modal('show');
  }

  formatDateForInput(date: Date): string {
    return new Date(date.getTime() - (date.getTimezoneOffset() * 60000)).toISOString().slice(0, 16);
  }

  private sendNotification(notificationType: NotificationType, message: string): void {
    if (message) {
      this.notificationService.notify(notificationType, message);
    } else {
      this.notificationService.notify(notificationType, 'An error occurred. Please try again.');
    }
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

  onJoinWorkout(workout: IWorkout) {
    this.isGridDisabled = true;
    const formData = this.workoutService.createWorkoutFormJoinData(
      this.authenticationService.getUserFromLocalCache(),
      workout
    );
    this.subscriptions.push(
      this.workoutService.joinWorkout(formData).subscribe(
        (response: IUserWorkout) => {
          this.sendNotification(
            NotificationType.SUCCESS,
            `${response.user.username} added successfully to workout: ${response.workout.workoutName}`
          );
          this.getWorkouts(false);
          setTimeout(() => {
            this.isGridDisabled = false;
          }, 2000);
        },
        (error: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, error.error.message);
        }
      )
    );
  }

  onLeaveWorkout(workoutId: number) {
    this.subscriptions.push(
      this.workoutService.deleteUserWorkout(this.authenticationService.getUserFromLocalCache().id,workoutId).subscribe(
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

  onWorkoutClickToggle(workout: IWorkout) {
    if (this.isUserInWorkout(workout)) {
      this.onLeaveWorkout(workout.id);
    } else {
      this.onJoinWorkout(workout);
    }
  }

}
