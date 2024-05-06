import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {IUserWorkout, IWorkout} from "../model/workout_model";
import {CustomHttpResponse} from "../model/custom-http-response_model";
import {User} from "../model/user_model";

@Injectable({
  providedIn: 'root'
})
export class WorkoutService {
  private host = environment.apiUrl

  constructor(private http: HttpClient) { }

  public getWorkouts(): Observable<IWorkout[]>{
    return  this.http.get<IWorkout[]>(`${this.host}/workout/list`);
  }
  public addWorkout(formdata: FormData): Observable<IWorkout>{
    return  this.http.post<IWorkout>(`${this.host}/workout/add`,formdata);
  }
  public updateWorkout(formdata: FormData): Observable<IWorkout>{
    return  this.http.post<IWorkout>(`${this.host}/workout/update`,formdata);
  }
  public deleteWorkout(id: number): Observable<CustomHttpResponse>{
    return this.http.delete<CustomHttpResponse>(`${this.host}/workout/delete/${id}`)
  }

  public getUserWorkouts(userId: number): Observable<IUserWorkout[]>{
    return this.http.get<IUserWorkout[]>(`${this.host}/user-workout/list/${userId}`);
  }
  public joinWorkout(formdata: FormData): Observable<IUserWorkout>{
    return this.http.post<IUserWorkout>(`${this.host}/user-workout/add`,formdata)
  }
  public deleteUserWorkout(userWorkoutId: number): Observable<CustomHttpResponse>{
    return this.http.delete<CustomHttpResponse>(`${this.host}/user-workout/delete/${userWorkoutId}`)
  }
  public listOfUserJoinedWorkout(workoutId: number): Observable<User[]>{
    return this.http.get<User[]>(`${this.host}/user-workout/list_joined/${workoutId}`);
  }
  createWorkoutFormJoinData(user: User,workout: IWorkout): FormData {
    const formData = new FormData();
    formData.append('workoutId', workout.id.toString());
    formData.append('userId', user.id.toString());
    return formData;
  }

  createWorkoutFormDate(id: any,workout: IWorkout): FormData {
    const formData = new FormData();
    formData.append('workoutId', id);
    formData.append('workoutName', workout.workoutName);
    formData.append('trainerUsername', workout.trainerUsername);
    formData.append('roomNumber', workout.roomNumber.toString());
    formData.append('workoutStartDate', workout.workoutStartDate.toString());
    formData.append('workoutEndDate', workout.workoutEndDate.toString());
    formData.append('capacity', workout.capacity.toString());
    formData.append('participantsNumber', workout.participantsNumber.toString());
    return formData;
  }
}
