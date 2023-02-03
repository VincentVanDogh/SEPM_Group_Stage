import {Injectable} from '@angular/core';
import {AuthRequest} from '../dtos/auth-request';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {tap} from 'rxjs/operators';
// @ts-ignore
import jwt_decode from 'jwt-decode';
import {Globals} from '../global/globals';
import {SharedService} from './shared.service';
import {ShoppingCartService} from './shopping-cart.service';
import {ToastrService} from 'ngx-toastr';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private authBaseUri: string = this.globals.backendUri + '/authentication';

  constructor(
    private httpClient: HttpClient,
    private globals: Globals,
    public service: ShoppingCartService,
    private sharedService: SharedService,
    private notification: ToastrService,
  ) {
  }

  /**
   * Login in the user. If it was successful, a valid JWT token will be stored
   *
   * @param authRequest User data
   */
  loginUser(authRequest: AuthRequest): Observable<string> {
    return this.httpClient.post(this.authBaseUri, authRequest, {responseType: 'text'})
      .pipe(
        tap((authResponse: string) => {
          this.setToken(authResponse);
          this.loadShoppingCart();
        })
      );
  }


  /**
   * Check if a valid JWT token is saved in the localStorage
   */
  isLoggedIn() {
    return !!this.getToken() && (this.getTokenExpirationDate(this.getToken()).valueOf() > new Date().valueOf());
  }

  /**
   * Removes the stored JWT token from the localStorage and empties shopping cart
   */
  logoutUser() {
    console.warn('Logout');
    this.sharedService.setCartStatus(0, 0);
    localStorage.removeItem('authToken');
  }

  /**
   * Returns the JWT token from the localStorage
   */
  getToken() {
    return localStorage.getItem('authToken');
  }

  /**
   * Returns the user role based on the current token
   */
  getUserRole() {
    if (this.getToken() != null) {
      const decoded: any = jwt_decode(this.getToken());
      const authInfo: string[] = decoded.rol;
      if (authInfo.includes('ROLE_ADMIN')) {
        return 'ADMIN';
      } else if (authInfo.includes('ROLE_USER')) {
        return 'USER';
      }
    }
    return 'UNDEFINED';
  }

  /**
   * Sets the JWT token in the localStorage
   *
   * @param authResponse the JWT token
   */
  private setToken(authResponse: string) {
    localStorage.setItem('authToken', authResponse);
  }

  /**
   * Returns the expiration date for the JWT token
   *
   * @param token the JWT token
   */
  private getTokenExpirationDate(token: string): Date {

    const decoded: any = jwt_decode(token);
    if (decoded.exp === undefined) {
      return null;
    }

    const date = new Date(0);
    date.setUTCSeconds(decoded.exp);
    return date;
  }

  /**
   * Loads the shopping cart for the user
   */
  private loadShoppingCart(): void {
    this.service.getShoppingCart().subscribe({
      next: cartItems => {
        this.sharedService.setCartStatus(cartItems.size, cartItems.bonusPoints);
      },
      error: err => {
        console.log('Error fetching cart: ', err);
        if (err.status === 0){
          this.notification.error('Is the backend up?');
        } else {
          this.notification.error('Error fetching the shopping cart');
        }
      }
    });
  }

}
