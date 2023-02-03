import { Injectable } from '@angular/core';
import {Observable} from 'rxjs';
import {MerchPurchase} from '../dtos/merchPurchase';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {MerchArticleQuantity} from '../dtos/merchArticle';

@Injectable({
  providedIn: 'root'
})
export class MerchPurchaseService {

  constructor(
    private http: HttpClient
  ) { }

  /**
   * Retrieves all merch articles purchased by the user.
   */
  public getCompletedPurchases(): Observable<MerchPurchase[]> {
    return this.http.get<MerchPurchase[]>(environment.merchComplete);
  }

  /**
   * Deletes a merch article from the shopping cart.
   *
   * @param merchId id of the to be deleted merch article
   */
  public deleteMerchArticle(merchId: number): Observable<void> {
    return this.http.delete<void>(`${environment.merchPurchaseUrl}/${merchId}`);
  }

  /**
   * Updates the quantity and/or payment option for a merch article.
   *
   * @param quantityDto dto containing purchase options
   */
  public updateMerchArticle(quantityDto: MerchArticleQuantity): Observable<MerchPurchase> {
    return this.http.put<MerchPurchase>(`${environment.merchPurchaseUrl}`, quantityDto);
  }

  /**
   * Deletes all merch articles stored in a users shopping cart.
   */
  deleteAllMerchArticlesInCart(): Observable<void> {
    return this.http.delete<void>(environment.merchPurchaseUrl);
  }
}
