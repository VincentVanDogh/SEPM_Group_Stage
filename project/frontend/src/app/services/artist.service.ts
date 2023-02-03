import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Artist} from '../dtos/artist';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ArtistService {

  private locationBaseUri: string = this.globals.backendUri + '/artists';

  constructor(
    private http: HttpClient,
    private globals: Globals
  ) {
  }

  createArtist(artist: Artist): Observable<Artist> {
    return this.http.post<Artist>(
      this.locationBaseUri,
      artist
    );
  }

  findAll(): Observable<Artist[]> {
    return this.http.get<Artist[]>(
      this.locationBaseUri
    );
  }

  findArtistsByName(name: string): Observable<Artist[]> {
    //let params = new HttpParams();
    //params = params.set('searchParam', name);
    return this.http.get<Artist[]>(
      this.locationBaseUri + '/search?searchParams=' + name
    );
  }
}
