import {User} from "./user_model";
import {IWorkout} from "./workout_model";

export class IMembershipType{
  public id: number
  public name: string
  public description: string
  public price: number
  public type: string
  public membershipProductId: string
  public validityPeriodNumber: number
  public validityUnitOfTime: string

  constructor() {
    this.id = 0
    this.name = ''
    this.description = ''
    this.price = 0
    this.type = ''
    this.membershipProductId = ''
    this.validityPeriodNumber = 0
    this.validityUnitOfTime = ''
  }
}


export class IUserMembership {
  id: number;
  userId: User;
  membershipTypeId: IMembershipType;
  startDate: string;
  endDate: string;

  constructor() {
    this.id = 0;
    this.userId = new User();
    this.membershipTypeId =  new IMembershipType();
    this.startDate = '';
    this.endDate = '';
  }

}


