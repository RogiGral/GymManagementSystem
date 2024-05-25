import {User} from "./user_model";

export class IWorkout {
  public id: number
  public workoutName: string
  public roomNumber: number
  public trainerUsername: string
  public capacity: number
  public participantsNumber: number
  public workoutStartDate: any
  public workoutEndDate: any
  public workoutDifficulty: string


  constructor() {
    this.id = 0;
    this.workoutName = '';
    this.roomNumber =  0;
    this.trainerUsername = '';
    this.capacity =  0;
    this.participantsNumber =  0;
    this.workoutStartDate = null;
    this.workoutEndDate = null;
    this.workoutDifficulty='';
  }

}

export class IUserWorkout {
  public id: number
  public user: User
  public workout: IWorkout

  constructor() {
    this.id = 0;
    this.user = new User();
    this.workout = new IWorkout();
  }

}



