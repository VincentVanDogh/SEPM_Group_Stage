import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Observable} from 'rxjs';
import {Stage} from '../dtos/stage';

@Injectable({
  providedIn: 'root'
})
export class StageService {

  private stageBaseUri: string = this.globals.backendUri + '/stages';

  constructor(
    private http: HttpClient,
    private globals: Globals
  ) {
  }

  /**
   * Gets all stages with the corresponding location ID
   *
   * @param locationId id of the Location the stage is at
   * @return an observable list of matching stages
   */
  getByLocationId(locationId: number): Observable<Stage[]> {
    return this.http.get<Stage[]>(this.stageBaseUri + '?locationId=' + locationId);
  }
}
