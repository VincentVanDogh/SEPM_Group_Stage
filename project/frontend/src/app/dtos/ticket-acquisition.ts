import {Ticket, TicketUpdate} from './ticket';

export interface TicketAcquisition {
  id?: number;
  buyerId?: number;
  tickets: TicketUpdate[];
  cancelled?: boolean;
  expanded?: boolean;
}

export interface TicketAcquisitionWithPrices {
  id?: number;
  buyerId?: number;
  tickets: Ticket[];
  cancelled?: boolean;
  expanded?: boolean;
}

export interface Reservation {
  reservationNo: number;
  customerId?: number;
  tickets: Ticket[];
  expanded?: boolean;
}

export interface ReservationPage {
  numberOfPages: number;
  reservations: Reservation[];
}

export interface TicketAcquisitionDetails {
  id?: number;
  buyerId?: number;
  tickets: Ticket[];
  cancelled?: boolean;
  expanded?: boolean;
}
