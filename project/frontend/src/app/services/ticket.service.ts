import {HttpClient} from '@angular/common/http';
import {TicketUpdate} from '../dtos/ticket';
import {environment} from '../../environments/environment';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {Globals} from '../global/globals';

@Injectable({
  providedIn: 'root'
})
export class TicketService {
  private authBaseUri: string = this.globals.backendUri + '/ticket';

  constructor(
    private httpClient: HttpClient, private globals: Globals
  ) {}

  /**
   * Deletes a ticket that has not been purchased yet from the system.
   *
   * @param id of the ticket expected to be deleted
   */
  public deleteTicket(id: number): Observable<void> {
    return this.httpClient.delete<void>(environment.ticketUrl + id);
  }

  /**
   * Deletes all tickets (with the optional status) from the shopping cart.
   *
   * @param status status of the tickets to be deleted
   */
  public deleteAllTickets(status: string | null): Observable<void> {
    return this.httpClient.delete<void>(`${environment.ticketUrl}${status === null ? '' : `?status=${status}`}`);
  }

  /**
   * Update a ticket that has not been purchased yet in the system.
   *
   * @param ticket
   * @return an observable of the updated ticket that was not purchased yet
   */
  public ticketUpdate(ticket: TicketUpdate): Observable<TicketUpdate> {
    return this.httpClient.put<TicketUpdate>(environment.ticketUrl, ticket);
  }

  /**
   * Create a ticket that has not been purchased yet in the system.
   *
   * @param ticket ticket expected to be creates
   */
  public ticketCreate(ticket: TicketUpdate) {
    return this.httpClient.post<TicketUpdate>(this.authBaseUri, ticket);
  }

  /**
   * Sets the status of the ticket from reserved to purchased.
   *
   * @param id id of the ticket
   */
  public buyReservedTicket(id: number) {
    return this.httpClient.patch<null>(environment.ticketUrl + 'buy_reservation/' + id, null);
  }
}
