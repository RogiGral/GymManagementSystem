export class User {
  public id: number;
  public userId: string;
  public firstName: string;
  public lastName: string;
  public username: string;
  public email: string;
  public lastLoginDate: any;
  public lastLoginDateDisplay: any;
  public joinDate: any;
  public profileImageUrl: string;
  public active: boolean;
  public isNotLocked: boolean;
  public role: string;
  public authorities: [];

  constructor() {
    this.userId = '';
    this.firstName = '';
    this.lastName = '';
    this.username = '';
    this.email = '';
    this.lastLoginDate = null;
    this.lastLoginDateDisplay = null;
    this.joinDate = null;
    this.profileImageUrl = '';
    this.active = false;
    this.isNotLocked = false;
    this.role = '';
    this.authorities = [];
  }

}

export class UserResetPassword {
  public email: string;

  constructor() {
    this.email = '';
  }
}



