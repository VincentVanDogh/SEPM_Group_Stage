import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Observable} from 'rxjs';
import {CreateLocation, Location} from '../dtos/location';

@Injectable({
  providedIn: 'root'
})
export class LocationService {

  private locationBaseUri: string = this.globals.backendUri + '/locations';

  constructor(
    private http: HttpClient,
    private globals: Globals
  ) {
  }

  /**
   * Persists location to the backend
   *
   * @param location to persist
   */
  createLocation(location: CreateLocation): Observable<CreateLocation> {
    return this.http.post<CreateLocation>(
      this.locationBaseUri,
      location
    );
  }

  /**
   * Returns all locations
   */
  getLocations(): Observable<Location[]> {
    return this.http.get<Location[]>(this.locationBaseUri);
  }

  /**
   * Returns all locations matching the name given
   *
   * @param name the name for the search
   */
  findLocationsByName(name: string): Observable<Location[]> {
    let params = new HttpParams();
    params = params.set('venueName', name);
    return this.http.get<Location[]>(
      this.locationBaseUri + '/search',
      { params }
    );
  }
}
