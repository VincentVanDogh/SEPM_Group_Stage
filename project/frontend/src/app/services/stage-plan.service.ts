import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Globals} from '../global/globals';
import {ActStagePlan, SeatStatusUpdate, StagePlan} from '../dtos/stage-plan';

@Injectable({
  providedIn: 'root'
})
export class StagePlanService {

  private stagePlanBaseUri: string = this.globals.backendUri + '/stage_plan';

  constructor(private httpClient: HttpClient, private globals: Globals) { }

  /**
   * Loads all stage plans from the backend
   */
  getStagePlans(): Observable<StagePlan[]> {
    return this.httpClient.get<StagePlan[]>(this.stagePlanBaseUri + '/generic');
  }

  /**
   * Loads specific stage plan from the backend
   *
   * @param id of stage plan to load
   */
  getStagePlanById(id: number): Observable<StagePlan> {
    return this.httpClient.get<StagePlan>(this.stagePlanBaseUri + '/generic/' + id);
  }

  /**
   * Loads all stage plan templates from the backend
   */
  getStagePlanTemplates(): Observable<StagePlan[]> {
    return this.httpClient.get<StagePlan[]>(this.stagePlanBaseUri + '/template');
  }

  /**
   * Loads specific stage plan template from the backend
   *
   * @param id of stage plan template to load
   */
  getStagePlanTemplateById(id: number): Observable<StagePlan> {
    return this.httpClient.get<StagePlan>(this.stagePlanBaseUri + '/template/' + id);
  }

  /**
   * Loads stage plan for specific act from the backend
   *
   * @param id of act for stage plan to load
   */
  getActStagePlanById(id: number): Observable<ActStagePlan> {
    return this.httpClient.get<ActStagePlan>(this.stagePlanBaseUri + '/act_specific/' + id);
  }

  /**
   * Loads stage plan for specific act from the backend with only the seats bought/reserved by the user marked
   *
   * @param id of act for stage plan to load
   */
  getActStagePlanByIdAndUser(id: number): Observable<ActStagePlan> {
    return this.httpClient.get<ActStagePlan>(this.stagePlanBaseUri + '/act_user_specific/' + id);
  }

  /**
   * Loads stage plan for specific act from the backend with only the seats bought/reserved by the user marked
   *
   * @param actStagePlan DTO of the ActStagePlan with updated Prices
   */
  updatePrices(actStagePlan: ActStagePlan): Observable<ActStagePlan> {
    return this.httpClient.put<ActStagePlan>(this.stagePlanBaseUri, actStagePlan);
  }

}
