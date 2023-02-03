import {SeatStatus} from './seat-status';
import {Orientation} from './orientation';

export class StagePlan {
  stageId: number;
  name: string;
  totalSeatsNr: number;
  sectorArray: SpecificSector[];
}

export class ActStagePlan {
  actId: number;
  stageId: number;
  name: string;
  totalSeatsNr: number;
  sectorArray: ActSpecificSector[];
}

export interface SpecificSector {
  orientation: Orientation;
  firstSeatNr?: number;
  numSeats: number;
  standing: boolean;
  numRows?: number;
  numColumns?: number;
}

export interface ActSpecificSector {
  id: number;
  orientation: Orientation;
  firstSeatNr?: number;
  numSeats: number;
  price: number;
  numReservedPlaces: number;
  numBoughtPlaces: number;
  standing: boolean;
  seatStatusMap?: SeatStatus[];
  numRows?: number;
  numColumns?: number;
}

export interface SeatStatusUpdate {
  newStatus: SeatStatus[];
  seatNr: number[];
  stageId: number;
  actId: number;
}

export enum DisplayMode {
  display = 'DISPLAY',
  ticket = 'TICKET',
  pricing = 'PRICING'
}
