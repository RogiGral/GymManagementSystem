import { Injectable } from '@angular/core';
import {NotifierService} from "angular-notifier";

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  private readonly notifier: NotifierService;

  constructor(private notifierService: NotifierService) {

    this.notifier = notifierService;
  }

  public notify(type: string, message: string){
    this.notifier.notify(type,message);
  }
}
