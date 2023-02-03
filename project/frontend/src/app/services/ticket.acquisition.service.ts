import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {environment} from '../../environments/environment';
import {TicketAcquisition, TicketAcquisitionWithPrices} from '../dtos/ticket-acquisition';

@Injectable({
  providedIn: 'root'
})
export class TicketAcquisitionService {

  constructor(
    private http: HttpClient
  ) { }

  /**
   * Returns all ticket acquisitions of the logged-in user stored in the system
   */
  public getTicketAcquisitions(): Observable<TicketAcquisition[]> {
    return this.http.get<TicketAcquisition[]>(environment.ticketOrderOverviewUrl);
  }

  /**
   * Sends a purchase request for tickets to the backend
   *
   * @param acquisition List of tickets that should be purchased
   */
  public purchaseTickets(acquisition: TicketAcquisition): Observable<TicketAcquisition> {
    return this.http.post<TicketAcquisition>(environment.ticketOrderUrl, acquisition);
  }

  /**
   * Sends a delete request to the backend
   *
   * @param id the id of the ticket that should be deleted
   */
  public deleteTicketAcquisition(id: number): Observable<void> {
    return this.http.delete<void>(environment.ticketOrderUrl + id);
  }

  /**
   * Sends a cancellation request to the backend
   *
   * @param cancellation
   * @return the ticket acquisition representing the cancellation
   * */
  public cancelTicketAcquisition(cancellation: TicketAcquisitionWithPrices): Observable<TicketAcquisition> {
    return this.http.post<TicketAcquisition>(environment.ticketOrderUrl + 'cancel', cancellation);
  }

  /**
   * Sends a purchase request for reserved tickets to the backend
   *
   * @param id id of the ticket that should be bought
   */
  public buyReservedTickets(id: number): Observable<void> {
    return this.http.patch<void>(environment.ticketOrderUrl + 'buy_reservation/' + id, null);
  }
}
