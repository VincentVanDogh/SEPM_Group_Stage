import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Observable} from 'rxjs';
import {ReservationPage} from '../dtos/ticket-acquisition';

@Injectable({
  providedIn: 'root'
})
export class ReservationService {

  private reservationBaseUri: string = this.globals.backendUri + '/reservation';

  constructor(private httpClient: HttpClient, private globals: Globals) { }

  /**
   * Returns all reservations of the logged-in user stored in the system
   */
  public getAllReservations(page: number): Observable<ReservationPage> {
    return this.httpClient.get<ReservationPage>(this.reservationBaseUri + '/all/' + page);
  }

  /**
   * Returns all reservations of the logged-in user stored in the system containing tickets for future acts
   */
  public getReservations(page: number): Observable<ReservationPage> {
    return this.httpClient.get<ReservationPage>(this.reservationBaseUri + '/current/' + page);
  }

  // public purchaseTickets(acquisition: TicketAcquisition): Observable<TicketAcquisition> {
  //   return this.http.post<TicketAcquisition>(environment.ticketOrderUrl, acquisition);
  // }
  //
  // // Change to a PUT request when cancelling acquisitions?
  // public deleteTicketAcquisition(id: number): Observable<void> {
  //   return this.http.delete<void>(environment.ticketOrderUrl + id);
  // }

}
