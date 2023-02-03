import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {tap} from 'rxjs/operators';
// @ts-ignore
import jwt_decode from 'jwt-decode';
import {Globals} from '../global/globals';
import {Registration} from '../dtos/registration';

@Injectable({
  providedIn: 'root'
})
export class RegistrationService {
  private authBaseUri: string = this.globals.backendUri + '/registration';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  /**
   * Create a new user. If it was successful, a valid JWT token will be stored
   *
   * @param registration User data
   */
  registerUser(registration: Registration): Observable<string> {
    return this.httpClient.post(this.authBaseUri, registration, {responseType: 'text'})
      .pipe(
        tap((authResponse: string) => this.setToken(authResponse))
      );
  }


  private setToken(authResponse: string) {
    localStorage.setItem('authToken', authResponse);
  }
}
