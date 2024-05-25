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
import {NgForm} from "@angular/forms";
import {UserService} from "../service/user.service";
import {ScoreService} from "../service/score.service";


@Component({
  selector: 'app-workout',
  templateUrl: './workout.component.html',
  styleUrls: ['./workout.component.css']
})
export class WorkoutComponent implements OnInit, OnDestroy {

  public refreshing = false;
  public selectedWorkout: IWorkout;
  public workouts: IWorkout[];
  public filteredWorkouts: IWorkout[];
  public editWorkout = new IWorkout();
  public listOfTrainers: User[] = [];
  public userWorkouts: IUserWorkout[];
  public trainerWorkouts: IWorkout[];
  public selectedUserWorkout: IWorkout;
  public selectedTrainerWorkout: IWorkout;
  public todayDate: string = new Date().toISOString().split('T')[0];
  public selectedDate: string = new Date().toISOString().split('T')[0];
  public listOfUserJoinedWorkout: User[];

  private subscriptions: Subscription[] = [];
  private currentWorkout: number;
  public currentWorkoutsPage: number = 1;
  public currentUserWorkoutsPage: number = 1;
  public currentTrainerWorkoutsPage: number = 1;

  constructor(
    private workoutService: WorkoutService,
    private userService: UserService,
    private notificationService: NotificationService,
    private authenticationService: AuthenticationService,
    private scoreService: ScoreService
  ) { }

  ngOnInit(): void {
    this.getWorkouts(true);
    this.getTrainers();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  public getTrainerWorkouts(): void {
    const currentDate = new Date();
    this.trainerWorkouts = this.workouts.filter(workout => {
      const workoutEndDate = new Date(workout.workoutEndDate); // Converts endDate to a Date object
      return workout.trainerUsername === this.authenticationService.getUserFromLocalCache().username &&
        workoutEndDate > currentDate; // Checks if the workout is in the future
    });
  }

  public getWorkouts(showNotification: boolean): void {
    this.refreshing = true;
    this.subscriptions.push(
      this.workoutService.getWorkouts().subscribe(
        (response: IWorkout[]) => {
          this.workouts = response;
          this.onDateChange()
          this.getTrainerWorkouts();
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
      //return workoutStartDate.toISOString().split('T')[0] === this.selectedDate;
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

  public onDeleteWorkout(id: number): void {
    this.subscriptions.push(
      this.workoutService.deleteWorkout(id).subscribe(
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
  public onSelectTrainerWorkout(selectedTrainerWorkout: IWorkout): void {
    this.selectedTrainerWorkout = selectedTrainerWorkout;
    this.getListOfUserJoinedWorkout(this.selectedTrainerWorkout.id)
    this.clickButton('openTrainerWorkoutInfo');
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
  public onSelectUserWorkout(selectedWorkout: IUserWorkout): void {
    this.selectedUserWorkout = selectedWorkout.workout;
    this.clickButton('openUserWorkoutInfo');
  }

  public onAddNewWorkout(workoutForm: NgForm): void {

    const isCyclical = workoutForm.value.isCyclical;
    const numberOfCycles = workoutForm.value.numberOfCycles;

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

  public addDaysToDateString(originalDateString: string): string {
    const originalDate = new Date(originalDateString);
    originalDate.setDate(originalDate.getDate() + 7);
    const formattedDateString = originalDate.toISOString().slice(0, 16);
    return formattedDateString;
  }

  public onUpdateWorkout(): void {
    const formData = this.workoutService.createWorkoutFormDate(this.currentWorkout, this.editWorkout);
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

  public onEditWorkout(editWorkout: IWorkout): void {
    this.editWorkout = editWorkout;
    this.currentWorkout = editWorkout.id;
    this.clickButton('openWorkoutEdit');
  }
  public saveNewWorkout(): void {
    this.clickButton('new-workout-save');
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

  addPoints(user:User) {
    const workout = this.selectedTrainerWorkout
    const formData = this.scoreService.createScoreFormData(user.username,100)
    this.subscriptions.push(
      this.scoreService.addScore(formData).subscribe(
        (response: CustomHttpResponse) => {
          this.sendNotification(NotificationType.SUCCESS, response.message);
        },
        (error: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, error.error.message);
        }
      ),
      this.workoutService.removeUserFromWorkout(user.id,workout.id).subscribe(
        (response: CustomHttpResponse) => {
          this.getListOfUserJoinedWorkout(workout.id);
          this.getTrainerWorkouts();
          this.sendNotification(NotificationType.SUCCESS, response.message);
        },
        (error: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, error.error.message);
        }
      )
    );
  }

  removePoints(user:User) {
    const workout = this.selectedTrainerWorkout;
    const formData = this.scoreService.createScoreFormData(user.username,100);
    this.subscriptions.push(
      this.scoreService.removeScore(formData).subscribe(
        (response: CustomHttpResponse) => {
          this.sendNotification(NotificationType.SUCCESS, response.message);
        },
        (error: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, error.error.message);
        }
      ),
      this.workoutService.removeUserFromWorkout(user.id,workout.id).subscribe(
        (response: CustomHttpResponse) => {
          this.getListOfUserJoinedWorkout(workout.id);
          this.getTrainerWorkouts();
          this.sendNotification(NotificationType.SUCCESS, response.message);
        },
        (error: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, error.error.message);
        }
      )
    );
  }

  private getListOfUserJoinedWorkout(workoutId: number){
    this.subscriptions.push(this.workoutService.listOfUserJoinedWorkout(workoutId).subscribe(
      (response: User[]) => {
        this.listOfUserJoinedWorkout = response;
      },
      (errorResponse: HttpErrorResponse) => {
        this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
        this.refreshing = false;
      }
    ));
  }
}
