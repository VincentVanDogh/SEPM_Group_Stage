import { Injectable } from '@angular/core';
import {
  Router, Resolve,
  ActivatedRouteSnapshot
} from '@angular/router';
import {Observable} from 'rxjs';
import {ShoppingCart} from '../dtos/shoppingCart';
import {ShoppingCartService} from './shopping-cart.service';

@Injectable({
  providedIn: 'root'
})
export class ShoppingCartResolver implements Resolve<ShoppingCart> {
  constructor(
    private cart: ShoppingCartService
  ) {
  }

  resolve(route: ActivatedRouteSnapshot): Observable<ShoppingCart> {
    return this.cart.getShoppingCart();
  }
}
