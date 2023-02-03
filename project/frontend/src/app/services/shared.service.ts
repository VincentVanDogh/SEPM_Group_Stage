import { Injectable } from '@angular/core';
import {Observable, Subject} from 'rxjs';
import {CartStatus} from '../dtos/cart-status';

@Injectable({
  providedIn: 'root'
})
export class SharedService {
  private cartStatus = new Subject<any>();

  constructor() { }

  setCartStatus(cartSize: number, points: number): void {
    if (points === null) {
      points = 0;
    }
    const cartStatus: CartStatus = {
      points,
      items: cartSize
    };
    this.cartStatus.next(cartStatus);
  }

  getCartStatus(): Observable<CartStatus> {
    return this.cartStatus.asObservable();
  }
}
