import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Observable} from 'rxjs';
import {Order, OrderPage} from '../dtos/order';

@Injectable({
  providedIn: 'root'
})
export class OrderService {

  private orderBaseUri: string = this.globals.backendUri + '/order';

  constructor(private httpClient: HttpClient, private globals: Globals) { }

  /**
   * Loads all orders for current user from the backend
   */
  getOrders(page: number): Observable<OrderPage> {
    return this.httpClient.get<OrderPage>(this.orderBaseUri + '/' + page);
  }
}
