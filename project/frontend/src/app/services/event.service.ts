import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {EventCreate, Event, EventPage} from '../dtos/event';
import {Observable} from 'rxjs';
import {Globals} from '../global/globals';
import {Act} from '../dtos/act';
import {SearchAct, SearchEvent} from '../dtos/search-event';
import {EventType} from '../dtos/eventType';

@Injectable({
  providedIn: 'root'
})
export class EventService {

  private eventBaseUri: string = this.globals.backendUri + '/events';
  private actBaseUri: string = this.globals.backendUri + '/acts';

  constructor(
    private http: HttpClient,
    private globals: Globals
  ) { }

  /**
   * Persists event to the backend
   *
   * @param event to persist
   */
  createEvent(event: EventCreate): Observable<EventCreate> {
    return this.http.post<EventCreate>(
      this.eventBaseUri,
      event
    );
  }

  /**
   * Loads a page of events from the backend
   *
   * @param pageId number of the page that should be returned
   */
  getEvent(pageId: number): Observable<EventPage> {
    return this.http.get<EventPage>(
      this.eventBaseUri + '/search/all/' + pageId
    );
  }

  /**
   * Get a specific event by its ID
   *
   * @param id the id of the event
   */
  getEventById(id: number): Observable<Event> {
    return this.http.get<Event>(this.eventBaseUri + '/search/' + id);
  }

  /**
   * Get all acts for an event by the event id
   *
   * @param id the id of the event
   */
  getActsForEvent(id: number): Observable<Act[]> {
    return this.http.get<Act[]>(this.actBaseUri + '/' + id);
  }

  /**
   * Loads a page of events from the backend matching the search request.
   *
   * @param searchRequest search parameters for the query
   * @param pageId number of the page that should be returned
   */
  searchEvent(searchRequest: SearchEvent, pageId: number): Observable<EventPage> {
    let params = new HttpParams();
    params = params.set('search', searchRequest.search);
    params = params.set('dateFrom', searchRequest.dateFrom);
    params = params.set('dateTo', searchRequest.dateTo);
    params = params.set('category', searchRequest.category);
    params = params.set('location', searchRequest.location);
    return this.http.get<EventPage>(
      this.eventBaseUri + '/search/page/' + pageId,
      { params }
    );
  }

  /**
   * Loads a page of events from the backend matching the search request given no category.
   *
   * @param searchRequest search parameters for the query
   * @param pageId number of the page that should be returned
   */
  searchEventWithoutCategory(searchRequest: SearchEvent, pageId: number): Observable<EventPage> {
    let params = new HttpParams();
    params = params.set('search', searchRequest.search);
    params = params.set('dateFrom', searchRequest.dateFrom);
    params = params.set('dateTo', searchRequest.dateTo);
    params = params.set('location', searchRequest.location);
    return this.http.get<EventPage>(
      this.eventBaseUri + '/search/page/' + pageId,
      { params }
    );
  }

  /**
   * Loads all acts for an event from the backend matching the search request.
   *
   * @param searchRequest search parameters for the query
   * @param id the id of the event
   */
  searchAct(searchRequest: SearchAct, id: number): Observable<Act[]> {
    let params = new HttpParams();
    params = params.set('minPrice', searchRequest.minPrice);
    params = params.set('maxPrice', searchRequest.maxPrice);
    params = params.set('dateFrom', searchRequest.dateFrom);
    params = params.set('dateTo', searchRequest.dateTo);
    return this.http.get<Act[]>(
      this.actBaseUri + '/search/' + id,
      { params }
    );
  }

  /**
   * Loads top ten events by number of tickets sold and category based on the month and year.
   *
   * @param category the category of the events
   * @param month the month that should be returned
   * @param year the year that should be returned
   */
  getTop10Events(category: EventType, month: number, year: number): Observable<Event[]> {
    let params = new HttpParams();
    params = params.set('category', category);
    if (month < 10) {
      params = params.set('yearMonth', year + '-0' + month);
    } else {
      params = params.set('yearMonth', year + '-' + month);
    }
    return this.http.get<Event[]>(
      this.eventBaseUri + '/top10',
      { params }
    );
  }

}
