import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {Observable} from "rxjs";
import {User} from "../model/user_model";
import {CustomHttpResponse} from "../model/custom-http-response_model";
import {IUserMembership} from "../model/membership_model";

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private host = environment.apiUrl

  constructor(private http: HttpClient) { }

  public getUsers(): Observable<User[]>{
    return  this.http.get<User[]>(`${this.host}/user/list`);
  }
  public addUser(formdata: FormData): Observable<User>{
    return  this.http.post<User>(`${this.host}/user/add`,formdata);
  }
  public updateUser(formdata: FormData): Observable<User>{
    return  this.http.post<User>(`${this.host}/user/update`,formdata);
  }
  public resetPassword(email: string): Observable<CustomHttpResponse>{
    return this.http.get<CustomHttpResponse>(`${this.host}/user/resetpassword/${email}`)
  }
  public setPassword(email: string, oldPassword:string, newPassword: string): Observable<CustomHttpResponse>{
    return this.http.get<CustomHttpResponse>(`${this.host}/user/setpassword/${email}/${oldPassword}/${newPassword}`)
  }
  public deleteUser(id: number): Observable<CustomHttpResponse>{
    return this.http.delete<CustomHttpResponse>(`${this.host}/user/delete/${id}`)
  }
  public addUsersToLocalCache(users: User[]): void{
    localStorage.setItem('users',JSON.stringify(users));
  }
  public getUsersFromLocalCache(): any{
    if(localStorage.getItem('users')){
      return JSON.parse(localStorage.getItem('users') || '{}');
    }
    return null;
  }

  public createUserFormDate(loggedInUsername: any, user: User, profileImage: File): FormData {
      const formData = new FormData();
      formData.append('currentUsername', loggedInUsername);
      formData.append('firstName', user.firstName);
      formData.append('lastName', user.lastName);
      formData.append('username', user.username);
      formData.append('email', user.email);
      formData.append('role', user.role);
      formData.append('profileImage', profileImage);
      formData.append('isActive', JSON.stringify(user.active));
      formData.append('isNotLocked', JSON.stringify(user.isNotLocked));
      return formData;
   }


}
