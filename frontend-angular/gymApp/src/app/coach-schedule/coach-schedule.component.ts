import {Component, OnDestroy, OnInit} from '@angular/core'
import {WorkoutService} from "../service/workout.service"
import {UserService} from "../service/user.service"
import {NotificationService} from "../service/notification.service"
import {AuthenticationService} from "../service/authentication.service"
import {ScoreService} from "../service/score.service"
import {IUserWorkout, IWorkout} from "../model/workout_model"
import {User} from "../model/user_model"
import {Subscription} from "rxjs"
import {NotificationType} from "../enum/notification-type.enum"
import {HttpErrorResponse} from "@angular/common/http"
import {CustomHttpResponse} from "../model/custom-http-response_model"
import {NgForm} from "@angular/forms"
import {Role} from "../enum/role.enum"
import {animate, style, transition, trigger} from '@angular/animations'

declare var $: any;

@Component({
  selector: 'app-coach-schedule',
  templateUrl: './coach-schedule.component.html',
  styleUrls: ['./coach-schedule.component.css'],

})
export class CoachScheduleComponent implements OnInit, OnDestroy {
  public currentDate: Date = new Date()
  public refreshing = false
  public selectedWorkout: IWorkout = new IWorkout()
  public selectedDay: Date | null = null;
  public selectedHour: number | null = null;
  public workouts: IWorkout[] = []
  public hours = Array.from({ length: 15 }, (_, i) => i + 8); // 0 to 23 hours
  public listOfTrainers: User[] = []
  public trainerWorkouts: IWorkout[] = []
  public selectedTrainerWorkout: IWorkout = new IWorkout()
  public listOfUserJoinedWorkout: User[] = []
  public currentTrainerWorkoutsPage: number = 1
  public todayWorkouts: any[] = []
  private subscriptions: Subscription[] = []

  public currentWeekStart: Date;
  public currentWeekEnd: Date;

  weekDays: Array<{ date: Date }> = [];

  constructor(
    private workoutService: WorkoutService,
    private userService: UserService,
    private notificationService: NotificationService,
    private authenticationService: AuthenticationService,
    private scoreService: ScoreService
  ) { }


  ngOnInit() {
    this.setWeekDates();
    this.getTrainers();
    this.getWorkouts(false)
    this.getTodayWorkouts()
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

  onWorkoutClick(workout: any) {
    this.selectedWorkout = workout;
    this.selectedTrainerWorkout = workout;
    $('#workoutOptionsModal').modal('show');
  }

  viewUsers() {
    this.getListOfUserJoinedWorkout(this.selectedWorkout.id)
    this.clickButton('openWorkoutUserList')
    $('#workoutOptionsModal').modal('hide');
  }

  viewWorkoutInfo() {
    this.clickButton('openWorkoutInfo')
    $('#workoutOptionsModal').modal('hide');
  }

  editWorkout() {
    this.clickButton('openWorkoutEdit')
    $('#workoutOptionsModal').modal('openWorkoutDelete');
  }

  deleteWorkout() {
    this.clickButton('openWorkoutDelete')
    $('#workoutOptionsModal').modal('hide');
  }

  goToPreviousWeek() {
    this.currentDate.setDate(this.currentDate.getDate() - 7);
    this.setWeekDates();
  }

  goToNextWeek() {
    this.currentDate.setDate(this.currentDate.getDate() + 7);
    this.setWeekDates();
  }

  getWorkoutsForDay(day: Date) {
    return this.trainerWorkouts.filter(workout => {
      const workoutDate = new Date(workout.workoutStartDate);
      return workoutDate.toDateString() === day.toDateString();
    });
  }

  public onUpdateWorkout(): void {
    const formData = this.workoutService.createWorkoutFormDate(this.selectedWorkout.id, this.selectedWorkout);
    this.subscriptions.push(
      this.workoutService.updateWorkout(formData).subscribe(
        (response: IWorkout) => {
          this.clickButton('closeEditWorkoutModalButton');
          this.getWorkouts(false);
          this.sendNotification(NotificationType.SUCCESS, `${response.workoutName} updated successfully`);
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
        }
      )
    );
  }

  public onDeleteWorkout(id: number): void {
    this.subscriptions.push(
      this.workoutService.deleteWorkout(id).subscribe(
        (response: CustomHttpResponse) => {
          this.sendNotification(NotificationType.SUCCESS, response.message);
          this.getWorkouts(false);
          $('#viewWorkoutDelete').modal('hide');
        },
        (error: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, error.error.message);
        }
      )
    );
  }

  getWorkoutsForDayAndHour(day: Date, hour: number) {
    return this.getWorkoutsForDay(day).filter(workout => {
      return new Date(workout.workoutStartDate).getHours() === hour;
    });
  }


  public get isAdmin(): boolean {
    return this.getUserRole() === Role.ADMIN || this.getUserRole() === Role.SUPER_ADMIN
  }

  public get isCoach(): boolean {
    return this.isAdmin || this.getUserRole() === Role.COACH
  }

  public get isAdminOrCoach(): boolean {
    return this.isAdmin || this.isCoach
  }

  getTodayWorkouts() {
    const today = this.currentDate.toDateString()
    this.todayWorkouts = this.trainerWorkouts.filter(workout =>
      new Date(workout.workoutStartDate).toDateString() === today
    )
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe())
  }

  public getTrainerWorkouts(): void {
    this.trainerWorkouts = this.workouts.filter(workout => {
      return workout.trainerUsername === this.authenticationService.getUserFromLocalCache().username // Checks if the workout is in the future
    })
  }

  public getWorkouts(showNotification: boolean): void {
    this.refreshing = true
    this.subscriptions.push(
      this.workoutService.getWorkouts().subscribe(
        (response: IWorkout[]) => {
          this.workouts = response
          this.getTrainerWorkouts()
          this.refreshing = false
          if (showNotification) {
            this.sendNotification(NotificationType.SUCCESS, `${response.length} workout(s) loaded successfully.`)
          }
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message)
          this.refreshing = false
        }
      ),
    )
  }

  addPoints(user:User) {
    const workout = this.selectedTrainerWorkout
    const formData = this.scoreService.createScoreFormData(user.username,100)
    this.subscriptions.push(
      this.scoreService.addScore(formData).subscribe(
        (response: CustomHttpResponse) => {
          this.sendNotification(NotificationType.SUCCESS, response.message)
        },
        (error: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, error.error.message)
        }
      ),
      this.workoutService.removeUserFromWorkout(user.id,workout.id).subscribe(
        (response: CustomHttpResponse) => {
          this.getListOfUserJoinedWorkout(workout.id)
          this.getTrainerWorkouts()
          this.sendNotification(NotificationType.SUCCESS, response.message)
        },
        (error: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, error.error.message)
        }
      )
    )
  }

  removePoints(user:User) {
    const workout = this.selectedTrainerWorkout
    const formData = this.scoreService.createScoreFormData(user.username,100)
    this.subscriptions.push(
      this.scoreService.removeScore(formData).subscribe(
        (response: CustomHttpResponse) => {
          this.sendNotification(NotificationType.SUCCESS, response.message)
        },
        (error: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, error.error.message)
        }
      ),
      this.workoutService.removeUserFromWorkout(user.id,workout.id).subscribe(
        (response: CustomHttpResponse) => {
          this.getListOfUserJoinedWorkout(workout.id)
          this.getTrainerWorkouts()
          this.sendNotification(NotificationType.SUCCESS, response.message)
        },
        (error: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, error.error.message)
        }
      )
    )
  }

  private sendNotification(notificationType: NotificationType, message: string): void {
    if (message) {
      this.notificationService.notify(notificationType, message)
    } else {
      this.notificationService.notify(notificationType, 'An error occurred. Please try again.')
    }
  }

  private getUserRole(): string {
    return this.authenticationService.getUserFromLocalCache().role
  }

  private clickButton(buttonId: string): void {
    document.getElementById(buttonId)!.click()
  }

  private getListOfUserJoinedWorkout(workoutId: number){
    this.subscriptions.push(this.workoutService.listOfUserJoinedWorkout(workoutId).subscribe(
      (response: User[]) => {
        this.listOfUserJoinedWorkout = response
      },
      (errorResponse: HttpErrorResponse) => {
        this.sendNotification(NotificationType.ERROR, errorResponse.error.message)
        this.refreshing = false
      }
    ))
  }

  public addDaysToDateString(originalDateString: string): string {
    const originalDate = new Date(originalDateString);
    originalDate.setDate(originalDate.getDate() + 7);
    const year = originalDate.getFullYear();
    const month = ('0' + (originalDate.getMonth() + 1)).slice(-2);
    const day = ('0' + originalDate.getDate()).slice(-2);
    const hours = ('0' + originalDate.getHours()).slice(-2);
    const minutes = ('0' + originalDate.getMinutes()).slice(-2);
    return `${year}-${month}-${day}T${hours}:${minutes}`;
  }

  public onAddNewWorkout(workoutForm: NgForm): void {

    const isCyclical = workoutForm.value.isCyclical;
    const numberOfCycles = workoutForm.value.numberOfCycles;

    workoutForm.value.workoutStartDate = this.selectedWorkout.workoutStartDate;
    workoutForm.value.workoutEndDate = this.selectedWorkout.workoutEndDate;
    const addWorkoutRecursively = (cycleCount: number) => {
      if (cycleCount <= numberOfCycles) {
        if(cycleCount!=0){
          workoutForm.value.workoutStartDate = this.addDaysToDateString(workoutForm.value.workoutStartDate)
          workoutForm.value.workoutEndDate = this.addDaysToDateString(workoutForm.value.workoutEndDate)
        }
        const formData = this.workoutService.createWorkoutFormDate(null, workoutForm.value);

        this.subscriptions.push(
          this.workoutService.addWorkout(formData).subscribe(
            (response: IWorkout) => {
              this.sendNotification(NotificationType.SUCCESS, `${response.workoutName} added successfully`);
              addWorkoutRecursively(cycleCount + 1);
            },
            (errorResponse: HttpErrorResponse) => {
              this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
            }
          )
        );
      } else {
        this.clickButton('new-workout-close');
        this.getWorkouts(false);
        workoutForm.reset();
      }
    };

    if (isCyclical) {
      addWorkoutRecursively(0);
    } else {
      const formData = this.workoutService.createWorkoutFormDate(null, workoutForm.value);
      this.subscriptions.push(
        this.workoutService.addWorkout(formData).subscribe(
          (response: IWorkout) => {
            this.clickButton('new-workout-close');
            this.getWorkouts(false);
            workoutForm.reset();
            this.sendNotification(NotificationType.SUCCESS, `${response.workoutName} added successfully`);
          },
          (errorResponse: HttpErrorResponse) => {
            this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
          }
        )
      );
    }
  }

  public getTrainers(): void {
    this.refreshing = true;
    this.subscriptions.push(
      this.userService.getUsers().subscribe(
        (response: User[]) => {
          const trainers = response.filter(user => user.role==="ROLE_COACH" || user.role==="ROLE_ADMIN");
          this.listOfTrainers.push(...trainers);
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
          this.refreshing = false;
        }
      )
    );
  }

  public saveNewWorkout(): void {
    this.clickButton('new-workout-save');
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

}
