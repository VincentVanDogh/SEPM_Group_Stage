export class CreateUser {
  constructor(
    public email: string,
    public password: string,
    public confirmation: string,
    public firstName: string,
    public lastName: string,
    public admin: boolean
  ) {}
}
