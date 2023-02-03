import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class InvoiceService {

  private invoiceBaseUri: string = this.globals.backendUri + '/invoice';

  constructor(private httpClient: HttpClient, private globals: Globals) { }

  /**
   * Loads specific invoice/cancellation from the backend
   *
   * @param id of invoice/cancellation to load
   */
  getById(id: number): Observable<any> {
    return this.httpClient.get(this.invoiceBaseUri + '/unspecified/' + id, {responseType: 'blob'});
  }

  /**
   * Loads specific invoice from the backend
   *
   * @param referenceNr of invoice to load
   */
  getInvoiceById(referenceNr: number): Observable<any> {
    return this.httpClient.get(this.invoiceBaseUri + '/regular/' + referenceNr, {responseType: 'blob'});
  }

  /**
   * Loads specific cancellation from the backend
   *
   * @param referenceNr of cancellation to load
   */
  getCancellationById(referenceNr: number): Observable<any> {
    return this.httpClient.get(this.invoiceBaseUri + '/cancellation/' + referenceNr, {responseType: 'blob'});
  }
}
