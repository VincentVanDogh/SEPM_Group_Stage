import {EventType} from './eventType';

export class SearchEvent {
  constructor(
    public search: string,
    public dateFrom: string,
    public dateTo: string,
    public category: EventType,
    public location: string
  ) {}
}

export class SearchAct {
  constructor(
    public minPrice: number,
    public maxPrice: number,
    public dateFrom: string,
    public dateTo: string,
  ) {}
}
