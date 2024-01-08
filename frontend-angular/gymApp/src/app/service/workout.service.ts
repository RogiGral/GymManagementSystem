import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {IWorkout} from "../model/workout_model";
import {CustomHttpResponse} from "../model/custom-http-response_model";

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

  createWorkoutFormDate(workout: IWorkout): FormData {
    const formData = new FormData();
    formData.append('workoutName', workout.workoutName);
    formData.append('trainerUsername', workout.trainerUsername);
    formData.append('roomNumber', workout.roomNumber.toString());
    formData.append('workoutStartDate', workout.workoutStartDate.toString());
    formData.append('workoutEndDate', workout.workoutEndDate.toString());
    formData.append('capacity', workout.capacity.toString());
    formData.append('participantNumber', workout.participantsNumber.toString());
    return formData;
  }
}
