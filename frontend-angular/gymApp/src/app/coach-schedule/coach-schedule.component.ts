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

@Component({
  selector: 'app-coach-schedule',
  templateUrl: './coach-schedule.component.html',
  styleUrls: ['./coach-schedule.component.css'],

})
export class CoachScheduleComponent implements OnInit, OnDestroy {
  public currentDate: Date = new Date()
  public refreshing = false
  public selectedWorkout: IWorkout = new IWorkout()
  public workouts: IWorkout[] = []
  public listOfTrainers: User[] = []
  public trainerWorkouts: IWorkout[] = []
  public selectedTrainerWorkout: IWorkout = new IWorkout()
  public listOfUserJoinedWorkout: User[] = []
  public currentTrainerWorkoutsPage: number = 1
  public todayWorkouts: any[] = []
  private subscriptions: Subscription[] = []

  constructor(
    private workoutService: WorkoutService,
    private userService: UserService,
    private notificationService: NotificationService,
    private authenticationService: AuthenticationService,
    private scoreService: ScoreService
  ) { }

  public get isAdmin(): boolean {
    return this.getUserRole() === Role.ADMIN || this.getUserRole() === Role.SUPER_ADMIN
  }

  public get isCoach(): boolean {
    return this.isAdmin || this.getUserRole() === Role.COACH
  }

  public get isAdminOrCoach(): boolean {
    return this.isAdmin || this.isCoach
  }

  ngOnInit() {
    this.getWorkouts(false)
    this.getTodayWorkouts()
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
    const currentDate = new Date()
    this.trainerWorkouts = this.workouts.filter(workout => {
      const workoutEndDate = new Date(workout.workoutEndDate) // Converts endDate to a Date object
      return workout.trainerUsername === this.authenticationService.getUserFromLocalCache().username &&
        workoutEndDate > currentDate // Checks if the workout is in the future
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

  goToPreviousDay() {
    this.currentDate = new Date(this.currentDate); // Clone the current date
    this.currentDate.setDate(this.currentDate.getDate() - 1); // Go to previous day
    this.getTodayWorkouts(); // Update the workouts for the new date
  }

  goToNextDay() {
    this.currentDate = new Date(this.currentDate); // Clone the current date
    this.currentDate.setDate(this.currentDate.getDate() + 1); // Go to next day
    this.getTodayWorkouts(); // Update the workouts for the new date
  }

  public onSelectTrainerWorkout(selectedTrainerWorkout: IWorkout): void {
    this.selectedTrainerWorkout = selectedTrainerWorkout
    this.getListOfUserJoinedWorkout(this.selectedTrainerWorkout.id)
    this.clickButton('open')
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
}
