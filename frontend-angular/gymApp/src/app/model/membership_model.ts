export class IMembershipType{
  public id: number
  public name: string
  public description: string
  public price: number
  public type: string
  public validityPeriodNumber: number
  public validityUnitOfTime: string

  constructor() {
    this.id = 0
    this.name = ''
    this.description = ''
    this.price = 0
    this.type = ''
    this.validityPeriodNumber = 0
    this.validityUnitOfTime = ''
  }
}
