import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Event} from '../../dtos/event';
import {EventService} from '../../services/event.service';
import {ToastrService} from 'ngx-toastr';
import {Act} from '../../dtos/act';
import {AuthService} from '../../services/auth.service';
import {StagePlanService} from '../../services/stage-plan.service';
import {UntypedFormBuilder, UntypedFormGroup} from '@angular/forms';
import {SearchAct} from '../../dtos/search-event';
import {Title} from '@angular/platform-browser';

@Component({
  selector: 'app-event-details',
  templateUrl: './event-details.component.html',
  styleUrls: ['./event-details.component.scss']
})
export class EventDetailsComponent implements OnInit {

  event: Event;
  acts: Act[];
  id: number;
  searchForm: UntypedFormGroup;
  defaultFrom = 0;
  error = false;
  errorMessage = '';


  constructor(
    private titleService: Title,
    private route: ActivatedRoute,
    public router: Router,
    private eventService: EventService,
    private stagePlanService: StagePlanService,
    private notification: ToastrService,
    private authService: AuthService,
    private formBuilder: UntypedFormBuilder,
  ) {
    this.searchForm = this.formBuilder.group({
      minPrice: [0],
      maxPrice: [1000],
      dateFrom: [this.route.snapshot.queryParamMap.get('dateFrom')],
      dateTo:[this.route.snapshot.queryParamMap.get('dateTo')]
    });
  }

  ngOnInit(): void {
    const getId = this.route.snapshot.paramMap.get('id');
    if (getId) {
      this.id = Number.parseInt(getId, 10);
      this.loadEvent();
      this.searchActs();
    }
  }

  loadEvent(): void {
    this.eventService.getEventById(this.id).subscribe({
      next: (eventData: Event) => {
        this.titleService.setTitle(`${eventData.name} | Acts`);
        this.event = eventData;
      },
      error: error => {
        console.error(error);
        this.notification.error('Could not load event with id: ' + this.id);
      }
    });
  }

  searchActs(): void {
    const searchRequest: SearchAct = new SearchAct(
      this.searchForm.controls.minPrice.value * 100,
      this.searchForm.controls.maxPrice.value * 100,
      this.searchForm.controls.dateFrom.value,
      this.searchForm.controls.dateTo.value
    );
    this.eventService.searchAct(searchRequest, this.id).subscribe({
      next: (acts: Act[]) => {
        this.acts = acts.sort((a, b) => {
        const timestampA = new Date(a.start).getTime();
        const timestampB = new Date(b.start).getTime();

          if (timestampA < timestampB) {
            return -1;
          } else if (timestampA > timestampB) {
            return 1;
          } else {
            return 0;
          }
        });
      },
      error: error => {
        console.error(error);
        this.notification.error('Could not load acts for event with id: ' + this.id);
      }
    });
  }

  /**
   * Returns true if the authenticated user is an admin
   */
  isAdmin(): boolean {
    return this.authService.getUserRole() === 'ADMIN';
  }

  /**
   * Returns true if the authenticated user is an admin
   */
  isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }


  getStart(start: Date): string {
    const unformattedDate = start.toString();
    const date = new Date(start);
    let result = '';
    switch (date.getDay()) {
      case 0:
        result += 'Sunday, ';
        break;
      case 1:
        result += 'Monday, ';
        break;
      case 2:
        result += 'Tuesday, ';
        break;
      case 3:
        result += 'Wednesday, ';
        break;
      case 4:
        result += 'Thursday, ';
        break;
      case 5:
        result += 'Friday, ';
        break;
      case 6:
        result += 'Saturday, ';
        break;
    }
    result += unformattedDate.substring(8,10) + '.';
    result += unformattedDate.substring(5,7) + '.';
    result += unformattedDate.substring(0,4);
    result += ', ' + unformattedDate.substring(11,16);

    return result;
  }

  actIsOver(start: Date): boolean {

    return new Date(start) < new Date();
  }
}
