import {Injectable} from '@angular/core';
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {AuthService} from '../services/auth.service';
import {Observable} from 'rxjs';
import {Globals} from '../global/globals';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(private authService: AuthService, private globals: Globals) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const authUri = this.globals.backendUri + '/authentication';
    const regUri = this.globals.backendUri + '/registration';
    const createUri = this.globals.backendUri + '/users/create';
    const usersUri = this.globals.backendUri + '/users';
    const eventUri = this.globals.backendUri + '/events';
    const initResetUri = this.globals.backendUri + '/reset/initiate';
    const resetUri = this.globals.backendUri + '/reset';
    const messageUri = this.globals.backendUri + '/messages/all';
    const actUri = this.globals.backendUri + '/acts';
    const merchUri = this.globals.backendUri + '/merch_article';

    // Do not intercept authentication requests
    if (req.url === authUri) {
      return next.handle(req);
    }

    if (req.url === regUri) {
      return next.handle(req);
    }

    if (req.url === createUri) {
      return next.handle(req);
    }

    if (req.url === initResetUri || req.url === resetUri) {
      return next.handle(req);
    }

    if (req.url.includes(eventUri +'/search') || req.url === eventUri +'/top10') {
      return next.handle(req);
    }
    if (req.url.includes(merchUri +'/all')) {
      return next.handle(req);
    }
    if (req.url.includes(messageUri)) {
      return next.handle(req);
    }
    if (req.url.includes(actUri)) {
      return next.handle(req);
    }
    const authReq = req.clone({
      headers: req.headers.set('Authorization', 'Bearer ' + this.authService.getToken())
    });

    return next.handle(authReq);
  }
}
