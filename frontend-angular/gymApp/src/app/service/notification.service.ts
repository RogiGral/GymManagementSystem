import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  constructor() { }

  public notify(type: string, message: string){
    console.log(type + ": " + message)
  }
}
