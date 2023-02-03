import { Injectable } from '@angular/core';
import {Observable} from 'rxjs';
import {MerchArticle, MerchArticleSearch, MerchPage} from '../dtos/merchArticle';
import {environment} from '../../environments/environment';
import {HttpClient, HttpParams} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class MerchArticleService {

  constructor(
    private http: HttpClient
  ) { }

  /**
   * Returns a page of merchandise articles matching the search query
   *
   * @param searchRequest values for the search query
   * @param pageId the page that should be returned
   */
  public getAllMerchandise(searchRequest: MerchArticleSearch, pageId: number): Observable<MerchPage> {
    let params = new HttpParams();
    if (searchRequest.term !== undefined  && searchRequest.term !== null) {
      params = params.set('term', searchRequest.term);
    }
    if (searchRequest.minPrice !== undefined  && searchRequest.minPrice !== null) {
      params = params.set('minPrice', searchRequest.minPrice);
    }
    if (searchRequest.maxPrice !== undefined && searchRequest.maxPrice !== null) {
      params = params.set('maxPrice', searchRequest.maxPrice);
    }
    if (searchRequest.minPointPrice !== undefined && searchRequest.minPointPrice !== null) {
      params = params.set('minPointPrice', searchRequest.minPointPrice);
    }
    if (searchRequest.maxPointPrice !== undefined  && searchRequest.maxPointPrice !== null) {
      params = params.set('maxPointPrice', searchRequest.maxPointPrice);
    }
    if (searchRequest.pointPurchaseAvailable !== undefined && searchRequest.pointPurchaseAvailable !== null) {
      params = params.set('pointPurchaseAvailable', searchRequest.pointPurchaseAvailable);
    }
    if (searchRequest.sortBy !== undefined && searchRequest.sortBy.toString() !== null) {
      params = params.set('sortBy', searchRequest.sortBy.toString());
    }
    return this.http.get<MerchPage>(
      environment.merchArticleUrl + '/all/page/' + pageId,
      { params }
    );
  }
}
