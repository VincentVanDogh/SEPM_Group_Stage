import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {ActStagePlan, SpecificSector, StagePlan} from '../../../dtos/stage-plan';
import {StagePlanService} from '../../../services/stage-plan.service';
import {ActivatedRoute} from '@angular/router';
import {Orientation} from '../../../dtos/orientation';
import {AuthService} from '../../../services/auth.service';

@Component({
  selector: 'app-stage-plan-single-price-config',
  templateUrl: './stage-plan-single-price-config.component.html',
  styleUrls: ['./stage-plan-single-price-config.component.scss']
})

export class StagePlanSinglePriceConfigComponent implements OnInit {

  @Input() id: number;
  @Input() prices: number[];
  @Output() pricesChange = new EventEmitter<number[]>(true);

  stagePlan: StagePlan;

  constructor(private service: StagePlanService,
              private route: ActivatedRoute,
              private authService: AuthService,
              // private notification: ToastrService,
  ) {
  }

  ngOnInit(): void {
    this.fetchStagePlan();
  }

  fetchStagePlan(): void {
    this.service.getStagePlanById(this.id)
      .subscribe({
        next: stagePlan => {
          this.stagePlan = stagePlan;
        },
        error: error => {
          console.error('Error fetching stage plan: ', error);
        }
      });
  }

  savePrices(): void {
    this.pricesChange.emit(this.prices);
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

  /**
   * Returns true if the authenticated user is an admin
   */
  isAdmin(): boolean {
    return this.authService.getUserRole() === 'ADMIN';
  }
}
