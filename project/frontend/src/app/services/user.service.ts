import { Injectable } from '@angular/core';
import {CreateUser} from '../dtos/create-user';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Observable} from 'rxjs';
import {User, UserPage} from '../dtos/user';
import {Emails} from '../dtos/emails';
import {NewPassword} from '../dtos/new-password';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private userBaseUri: string = this.globals.backendUri + '/users';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }
  /**
   * Create a new user
   *
   * @param user User data
   */
  createUser(user: CreateUser): Observable<string> {
    return this.httpClient.post(this.userBaseUri, user, {responseType: 'text'});
  }

  /**
   * Toggle locked status of user
   *
   * @param emails email of user who's locked status is to be changed
   */
  changeLockedStatus(emails: Emails): Observable<void> {
    return this.httpClient.put<void>(this.userBaseUri, emails);
  }

  /**
   * Returns a list of all users
   *
   *@param page the page that should be returned
   */
  getAll(page: number): Observable<UserPage> {
    return this.httpClient.get<UserPage>(this.userBaseUri + '/' + page);
  }


  /**
   * Returns a Dto of the total number of pages and a page of users matching the search criteria
   *
   * @param searchParams The criteria to search for
   * @param page the page that should be returned
   */
  search(searchParams: User, page: number): Observable<UserPage> {
    let params = new HttpParams();
    params = params.set('email', searchParams.email);
    params = params.set('firstName', searchParams.firstName);
    params = params.set('lastName', searchParams.lastName);
    params = params.set('lockedOut', searchParams.lockedOut);
    return this.httpClient.get<UserPage>(this.userBaseUri + '/search/' + page, { params });
  }

  /**
   * Returns a Dto of the total number of pages and a page of users matching the search criteria
   *
   * @param searchParams The criteria to search for
   * @param page the page that should be returned
   */
  searchWithoutLockedOut(searchParams: User, page: number): Observable<UserPage> {
    let params = new HttpParams();
    params = params.set('email', searchParams.email);
    params = params.set('firstName', searchParams.firstName);
    params = params.set('lastName', searchParams.lastName);
    return this.httpClient.get<UserPage>(this.userBaseUri + '/search/' + page, { params });
  }

  /**
   * Creates and stores reset token in database and sends email to the user to reset his password
   *
   * @param email The email of the user that wants to reset the password
   */
  initiateResetPassword(email: string): Observable<string> {
    return this.httpClient.post(this.globals.backendUri + '/reset/initiate', { email }, {responseType: 'text'});
  }

  /**
   * Sets the new password if the token and confirmation are valid.
   *
   * @param newPassword The new password which will be set, the confirmation of it and the token
   */
  setNewPassword(newPassword: NewPassword): Observable<string> {
    return this.httpClient.post(this.globals.backendUri + '/reset',  newPassword, {responseType: 'text'})
      .pipe();
  }
}
