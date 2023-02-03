export class NewPassword {
  constructor(
    public newPassword: string,
    public confirmation: string,
    public token: string,
  ) {}
}
