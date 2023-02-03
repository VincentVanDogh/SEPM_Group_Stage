import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Observable} from 'rxjs';
import {tap} from 'rxjs/operators';
import {Registration} from '../dtos/registration';
import {Password} from '../dtos/password';

@Injectable({
  providedIn: 'root'
})
export class ProfileService {
  private profileBaseUri: string = this.globals.backendUri + '/profile';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  /**
   * Edit an existing user. If it was successful, a valid JWT token will be stored.
   *
   * @param profile User data
   */
  editUser(profile: Registration): Observable<string> {
    return this.httpClient.put(this.profileBaseUri+'/edit', profile, {responseType: 'text'})
      .pipe(
        tap((authResponse: string) => this.setToken(authResponse))
      );
  }

  /**
   * Delete an existing user.
   */
  deleteUser(): Observable<void> {
    return this.httpClient.delete<void>(this.profileBaseUri);
  }

  /**
   * Get the current user information.
   */
  getUser(): Observable<Registration>{
    return this.httpClient.get<Registration>(this.profileBaseUri);
  }

  /**
   * Edit password of an existing user. If it was successful, a valid JWT token will be stored.
   *
   * @param password password data
   */
  editPassword(password: Password): Observable<string> {
    return this.httpClient.post(this.profileBaseUri, password, {responseType: 'text'})
      .pipe(
        tap((authResponse: string) => this.setToken(authResponse))
      );
  }

  /**
   * Returns the bonus points of the user that is currently logged in.
   */
  getBonusPoints(): Observable<number> {
    return this.httpClient.get<number>(`${this.profileBaseUri}/bonus_points`);
  }

  /**
   * Returns the bonus points in cart of the user that is currently logged in.
   */
  getBonusPointsInCart(): Observable<number> {
    return this.httpClient.get<number>(`${this.profileBaseUri}/bonus_points_cart`);
  }

  private setToken(authResponse: string) {
    localStorage.setItem('authToken', authResponse);
  }
}
