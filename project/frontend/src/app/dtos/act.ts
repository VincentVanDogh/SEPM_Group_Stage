import {Event} from './event';

export interface Act {
  id?: number;
  start: Date;
  stageId: number;
}

export interface ActDetails {
  id: number;
  start: Date;
  nrTicketsReserved: number;
  nrTicketsSold: number;
  stageId: number;
  event: Event;
}
