import {Component, Input, OnInit} from '@angular/core';
import {StagePlanService} from '../../../services/stage-plan.service';
import {ActivatedRoute} from '@angular/router';
// import {ToastrService} from 'ngx-toastr';
import {SpecificSector, StagePlan} from '../../../dtos/stage-plan';
import {Orientation} from '../../../dtos/orientation';

@Component({
  selector: 'app-stage-plan-single-generic',
  templateUrl: './stage-plan-single-generic.component.html',
  styleUrls: ['./stage-plan-single-generic.component.scss']
})
export class StagePlanSingleGenericComponent implements OnInit {

  @Input() id: number;

  stagePlan: StagePlan;
  seatChosen: boolean[];
  rowNumber: number;
  standingTickets: number;

  constructor(private service: StagePlanService,
              private route: ActivatedRoute,
              // private notification: ToastrService,
  ) {
    this.seatChosen = null;
    this.rowNumber = 0;
    this.standingTickets = 0;
  }

  ngOnInit(): void {
    this.fetchStagePlan();
  }

  fetchStagePlan(): void {
    this.service.getStagePlanById(this.id)
      .subscribe({
        next: stagePlan => {
          this.stagePlan = stagePlan;
          this.seatChosen = new Array(stagePlan.totalSeatsNr);
        },
        error: error => {
          console.error('Error fetching stage plan: ', error);
        }
      });
  }

  resetRowNumber(): void {
    this.rowNumber = 0;
  }

  incRowNumber(): number {
    return ++this.rowNumber;
  }

  getSectorDesignation(sectorIdentifier: number): string {
    const identifiers = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'.split('');
    return identifiers[sectorIdentifier*(-1)-1];
  }

  getSectorOrientation(sector: SpecificSector): string {
    return sector.orientation.toString();
  }

  isSectorOrientationNorth(sector: SpecificSector): boolean {
    return sector.orientation === Orientation.north;
  }

  isSectorOrientationEast(sector: SpecificSector): boolean {
    return sector.orientation === Orientation.east;
  }

  isSectorOrientationSouth(sector: SpecificSector): boolean {
    return sector.orientation === Orientation.south;
  }

  isSectorOrientationWest(sector: SpecificSector): boolean {
    return sector.orientation === Orientation.west;
  }

}
