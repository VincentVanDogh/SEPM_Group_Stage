import {EventType} from './eventType';
import {Act} from './act';
import {LocationDetails} from './location';
import {Artist} from './artist';

export class EventCreate {
  id?: number;
  name: string;
  description?: string;
  type: EventType;
  duration: number;
  locationId: number;
  acts: Act[];
  artistIds: number[];
  pricesPerAct: number[][];
}

export interface Event {
  id: number;
  name: string;
  description?: string;
  type: EventType;
  duration: number;
  location: LocationDetails;
  totalNrOfTicketsSold?: number;
  featuredArtists: Artist[];
}

export interface EventPage {
  numberOfPages: number;
  events: Event[];
}
