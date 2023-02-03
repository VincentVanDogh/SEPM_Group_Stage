import {Stage} from './stage';
import {Address} from './address';

export class CreateLocation {
  id?: number;
  venueName: string;
  street: string;
  city: string;
  country: string;
  postalCode: number;
  stages: Stage[];
}

export class Location {
  id: number;
  venueName: string;
  addressId: number;
}

export class LocationDetails {
  id: number;
  venueName: string;
  address: Address;
}
