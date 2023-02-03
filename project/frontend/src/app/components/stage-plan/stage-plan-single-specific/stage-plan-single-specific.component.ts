import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {ActStagePlan, SpecificSector} from '../../../dtos/stage-plan';
import {StagePlanService} from '../../../services/stage-plan.service';
import {ActivatedRoute} from '@angular/router';
import {Orientation} from '../../../dtos/orientation';
import {SeatStatus} from '../../../dtos/seat-status';
import {SeatDataForTicket} from '../../../dtos/ticket';
import {AuthService} from '../../../services/auth.service';
import {Title} from '@angular/platform-browser';

@Component({
  selector: 'app-stage-plan-single-specific',
  templateUrl: './stage-plan-single-specific.component.html',
  styleUrls: ['./stage-plan-single-specific.component.scss']
})

export class StagePlanSingleSpecificComponent implements OnInit {

  @Input() id: number;
  @Input() actId: number;
  @Input() seatData: SeatDataForTicket[];
  @Output() seatDataChange = new EventEmitter<SeatDataForTicket[]>(true);

  stagePlan: ActStagePlan;
  rowNumber: number;
  rowNumbersNorth: number[] = [];
  rowNumbersSouth: number[] = [];
  rowNumbersEast: number[] = [];
  rowNumbersWest: number[] = [];
  standingTickets: number[] = [];
  standingSectorDesignation: string[] = [];
  seatIsFree = SeatStatus.free;
  seatChosen: boolean[];

  constructor(
    private titleService: Title,
    private service: StagePlanService,
    private route: ActivatedRoute,
    private authService: AuthService
  ) {
    titleService.setTitle('Stage Plan');
    this.seatChosen = null;
    this.rowNumber = 0;
    this.seatDataChange = new EventEmitter<SeatDataForTicket[]>(true);
  }

  ngOnInit(): void {
    this.fetchStagePlan();
  }

  fetchStagePlan(): void {
    this.service.getActStagePlanById(this.actId)
      .subscribe({
        next: stagePlan => {
          this.stagePlan = stagePlan;
          this.seatChosen = new Array(stagePlan.totalSeatsNr);
          this.seatData = new Array(stagePlan.totalSeatsNr);
          this.standingTickets = new Array(stagePlan.sectorArray.length);
          this.standingSectorDesignation = new Array(stagePlan.sectorArray.length);
          this.generateStandingSectorDesignations();
        },
        error: error => {
          console.error('Error fetching stage plan: ', error);
        }
      });
  }

  resetRowNumber(): void {
    this.rowNumber = 0;
  }

  incRowNumber(sector: SpecificSector, rowInSector: number): number {
    if (this.isSectorOrientationNorth(sector) && this.rowNumber >= this.rowNumbersNorth.length) {
      this.rowNumbersNorth.push((sector.firstSeatNr + sector.numColumns*rowInSector));
    }
    if (this.isSectorOrientationSouth(sector) && this.rowNumber >= this.rowNumbersSouth.length) {
      this.rowNumbersSouth.push((sector.firstSeatNr + sector.numColumns*rowInSector));
    }
    if (this.isSectorOrientationEast(sector) && this.rowNumber >= this.rowNumbersEast.length) {
      this.rowNumbersEast.push((sector.firstSeatNr + sector.numColumns*rowInSector));
    }
    if (this.isSectorOrientationWest(sector) && this.rowNumber >= this.rowNumbersWest.length) {
      this.rowNumbersWest.push((sector.firstSeatNr + sector.numColumns*rowInSector));
    }
    return ++this.rowNumber;
  }

  getRowNumber(seatId: number, orientation: Orientation): number {
    let rowNumber = 0;
    if (orientation === Orientation.north) {
      this.rowNumbersNorth.forEach((element, index) => {
        if (element){
          if (element <= seatId) {
            rowNumber = index;
          }
        }
      });
    }
    if (orientation === Orientation.south) {
      this.rowNumbersSouth.forEach((element, index) => {
        if (element){
          if (element <= seatId) {
            rowNumber = index;
          }
        }
      });
    }
    if (orientation === Orientation.west) {
      this.rowNumbersWest.forEach((element, index) => {
        if (element){
          if (element <= seatId) {
            rowNumber = index;
          }
        }
      });
    }
    if (orientation === Orientation.east) {
      this.rowNumbersEast.forEach((element, index) => {
        if (element){
          if (element <= seatId) {
            rowNumber = index;
          }
        }
      });
    }
    return rowNumber;
  }

  generateStandingSectorDesignations(): void {
    const identifiers = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'.split('');
    identifiers.reverse();
    for (let i = 0; i < this.standingSectorDesignation.length; i++) {
      if (this.stagePlan.sectorArray[i] && this.stagePlan.sectorArray[i].standing) {
        this.standingSectorDesignation[i] = identifiers.pop();
      }
    }
  }

  getSectorOrientation(sector: SpecificSector): Orientation {
    return sector.orientation;
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

  pushToTickets(seatId: number, seatNumberInRow: number, orientation: Orientation, clear: boolean): boolean {
    if (clear) {
      this.seatData[seatId] = null;
    } else {
      this.seatData[seatId] = {
        rowNumber: 1 + this.getRowNumber(seatId, orientation),
        seatNumberInRow
      };
    }

    this.seatDataChange.emit(this.seatData);
    return true;
  }

  pushToTicketsStanding(firstSeatNr: number, numberOfSeats: number, numberOfFreeSeats: number): boolean {

    if(numberOfSeats > numberOfFreeSeats) {
      numberOfSeats = numberOfFreeSeats;
    }

    for (let i = 0; i < numberOfFreeSeats; i++) {
      this.seatData[firstSeatNr+i] = null;
    }

    if (numberOfSeats !== 0) {
      for (let i = 0; i < numberOfSeats; i++) {
        this.seatData[firstSeatNr+i] = {
          rowNumber: 0,
          seatNumberInRow: 0
        };
      }
    }

    this.seatDataChange.emit(this.seatData);
    return true;
  }

  /**
   * Returns true if the authenticated user is an admin
   */
  isAdmin(): boolean {
    return this.authService.getUserRole() === 'ADMIN';
  }

  public priceAsString(price: number): string {
    return `€ ${(price / 100).toFixed(2)}`;
  }

}
