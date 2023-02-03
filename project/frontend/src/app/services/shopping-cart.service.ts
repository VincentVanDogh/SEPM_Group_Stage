import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {ShoppingCart} from '../dtos/shoppingCart';
import {environment} from '../../environments/environment';
import {CreateInvoice} from '../dtos/invoice';

@Injectable({
  providedIn: 'root'
})
export class ShoppingCartService {

  constructor(
    private http: HttpClient
  ) { }

  public finalizePurchase(invoice: CreateInvoice): Observable<ShoppingCart> {
    return this.http.post<ShoppingCart>(environment.shoppingCart, invoice);
  }

  public getShoppingCart(): Observable<ShoppingCart> {
    return this.http.get<ShoppingCart>(environment.shoppingCart);
  }
}
