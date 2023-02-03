export class User {
  constructor(
    public email: string,
    public firstName: string,
    public lastName: string,
    public lockedOut: boolean
  ) {}
}

export interface UserPage {
  numberOfPages: number;
  users: User[];
}

