import {ActDetails} from './act';
import {TicketStatus} from './ticket-status';

export interface Ticket {
  id: number;
  buyerId: number;
  sectorMapId: number;
  seatNo: number;
  ticketFirstName: string;
  ticketLastName: string;
  reservation: TicketStatus;
  cancelled: boolean;
  act: ActDetails;
  creationDate: Date;
  remainingTime?: RemainingTime;
  notified?: boolean;
  price: number;
  rowNumber: number;
  seatNoInRow: number;
  sectorDesignation: string;
}

export interface TicketUpdate {
  id: number;
  buyerId?: number;
  actId: number;
  sectorMapId: number;
  seatNo: number;
  ticketFirstName: string;
  ticketLastName: string;
  reservation: TicketStatus;
  cancelled: boolean;
  creationDate: Date;
}

export interface Card {
  method: string;
  cardNumber?: number;
  expirationDate?: Date;
  cvc?: number;
}

export interface SeatDataForTicket {
  rowNumber: number;
  seatNumberInRow: number;
}

export interface RemainingTime {
  minutes: number;
  seconds: number;
}
