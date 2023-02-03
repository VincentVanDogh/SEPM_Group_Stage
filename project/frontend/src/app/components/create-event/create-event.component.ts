import {Component, OnInit} from '@angular/core';
import {EventType} from '../../dtos/eventType';
import {ActivatedRoute, Router} from '@angular/router';
import {FormArray, FormControl, UntypedFormBuilder, Validators} from '@angular/forms';
import {EventService} from '../../services/event.service';
import {EventCreate} from '../../dtos/event';
import {Stage} from '../../dtos/stage';
import {StageService} from '../../services/stage.service';
import {LocationService} from '../../services/location.service';
import {ToastrService} from 'ngx-toastr';
import {Artist} from '../../dtos/artist';
import {ArtistService} from '../../services/artist.service';
import {catchError, debounceTime, distinctUntilChanged, EMPTY, Observable, switchMap} from 'rxjs';
import {AuthService} from '../../services/auth.service';

@Component({
  selector: 'app-create-event',
  templateUrl: './create-event.component.html',
  styleUrls: ['./create-event.component.scss']
})
export class CreateEventComponent implements OnInit {

  eventForm = this.formBuilder.group({
      name: ['', Validators.required],
      description: [''],
      type: [EventType.concert, Validators.required],
      duration: [60, Validators.compose([Validators.required, Validators.min(0), Validators.pattern('^[0-9]*$')])],
      location: ['', Validators.required],
      acts: this.formBuilder.array([
        this.formBuilder.group({
          start: ['', Validators.compose([Validators.required])],
          stageId: ['', Validators.required]
        }),
      ]),
      artistArray: this.formBuilder.array([
        this.formBuilder.control('', Validators.required),
      ])
    }
  );

  eventType = EventType;
  searchValue: string;
  stages: Stage[] = [];
  artists: Artist[] = [];
  actPrices: number[][] = [new Array(49).fill(0)];
  actShowPrice: boolean[] = [false];

  constructor(
    private eventService: EventService,
    private stageService: StageService,
    private locationService: LocationService,
    private artistService: ArtistService,
    private router: Router,
    private route: ActivatedRoute,
    private formBuilder: UntypedFormBuilder,
    private notification: ToastrService,
    private authService: AuthService
  ) {
  }

  get acts() {
    return this.eventForm.get('acts') as FormArray;
  }

  get artistArray() {
    return this.eventForm.get('artistArray') as FormArray;
  }

  get name() {
    return this.eventForm.get('name') as FormControl;
  }

  get description() {
    return this.eventForm.get('description') as FormControl;
  }

  get type() {
    return this.eventForm.get('type') as FormControl;
  }

  get duration() {
    return this.eventForm.get('duration') as FormControl;
  }

  get location() {
    return this.eventForm.get('location') as FormControl;
  }

  addAct() {
    this.acts.push(this.formBuilder.group({
        start: ['', Validators.required],
        stageId: ['', Validators.required],
      })
    );
    this.actPrices.push(new Array(49).fill(0));
    this.actShowPrice.push(false);
  }

  addArtist() {
    this.artistArray.push(this.formBuilder.control(0, Validators.required),
    );
  }

  removeAct(index: number) {
    this.acts.removeAt(index);
    this.actPrices.splice(index, 1);
    this.actShowPrice.splice(index, 1);
  }

  removeArtist(index: number) {
    this.artistArray.removeAt(index);
  }

  getStageIdForAct(index: number) {
    return this.acts.value[index].stageId;
  }

  setPrices(newPrices: number[], index: number) {
    this.actPrices[index] = newPrices;
    this.actShowPrice[index] = false;
    this.notification.success('Prices for act ' + (index + 1) + ' successfully saved!');
  }

  ngOnInit(): void {
    if (!this.isAdmin()) {
      this.router.navigate(['']);
    }
  }

  /**
   * search for stages by location
   */
  public findStagesByLocationId(locationId: number): void {
    this.resetStages();
    this.stageService.getByLocationId(locationId).subscribe({
      next: data => {
        this.stages = data;
      },
      error: error => {
        console.error('Error finding stages by location Id', error);
        this.notification.error('Failed to find stages by location Id');
      }
    });
  }

  resetStages(): void {
    for (const act of this.acts.controls) {
      act.get('stageId').setValue('');
    }
  }

  /**
   * search for location typeahead
   */
  searchLocation = (text$: Observable<string>) => text$.pipe(
    debounceTime(200),
    distinctUntilChanged(),
    switchMap((name) => this.locationService.findLocationsByName(name)),
    catchError((err, caught) => EMPTY)
  );

  /**
   * Format result for location typehead
   */
  formatResultLocation(value: any) {
    return value.venueName;
  }

  /**
   * Format input for location typeahead
   */
  formatInputLocation(value: any) {
    if (value.venueName) {
      return value.venueName;
    }
    return value;
  }

  /**
   * search for artist typeahead
   */
  searchArtist = (text$: Observable<string>) => text$.pipe(
    debounceTime(200),
    distinctUntilChanged(),
    switchMap((name) => this.artistService.findArtistsByName(name)),
    catchError((err, caught) => EMPTY)
  );

  /**
   * Format for artist typehead
   */
  formatArtist(value: any) {
    let displayName = '';
    let firstVal = true;

    if (value.firstName) {
      if (firstVal) {
        displayName = displayName + value.firstName;
        firstVal = false;
      } else {
        displayName = displayName + ' ' + value.firstName;
      }
    }
    if (value.lastName) {
      if (firstVal) {
        displayName = displayName + value.lastName;
        firstVal = false;
      } else {
        displayName = displayName + ' ' + value.lastName;
      }
    }
    if (value.bandName) {
      if (firstVal) {
        displayName = displayName + value.bandName;
        firstVal = false;
      } else {
        displayName = displayName + ' ' + value.bandName;
      }
    }
    if (value.stageName) {
      if (firstVal) {
        displayName = displayName + value.stageName;
        firstVal = false;
      } else {
        displayName = displayName + ' ' + value.stageName;
      }
    }
    return displayName;
  }

  public formToEventCreate(): EventCreate {
    const artistIds = this.artistArray.value as Artist[];
    this.actPrices.forEach((element) => {
      element.forEach((innerElement, index) => {
        if (innerElement && innerElement !== 0) {
          element[index] = innerElement * 100;
        }
      });
    });
    return {
      name: this.name.value,
      description: this.description.value,
      type: this.type.value,
      duration: this.duration.value,
      locationId: this.location.value.id,
      acts: this.acts.value,
      artistIds: artistIds.map(a => a.id),
      pricesPerAct: this.actPrices
    };
  }

  public submitEvent(): void {
    const observable = this.eventService.createEvent(this.formToEventCreate());
    observable.subscribe({
      next: data => {
        this.notification.success('Event was successfully created');
        this.router.navigate(['/events']);
      },
      error: error => {
        console.error('Error creating event: ', error);
        if (error.status === 400) {
          const msg: string = error.error;

          if (msg.includes('future date')) {
            this.notification.error('Chosen date must be in the future');
          }

          if (msg.includes('255 characters')) {
            this.notification.error('Name must not be longer than 255 characters');
          }

          if (msg.includes('4095 characters')) {
            this.notification.error('Name must not be longer than 255 characters');
          }

          if (msg.includes('location')) {
            this.notification.error('choose a valid location');
          }

        } else if (error.status === 404) {
          const msg: string = error.error;

          if (msg.includes('artist')) {
            this.notification.error('choose a valid artist');
          }
          if (msg.includes('location')) {
            this.notification.error('choose a valid location');
          }
          if (msg.includes('stage')) {
            this.notification.error('choose a valid stage');
          }

        } else {
          this.notification.error('Error creating event');
        }
      }
    });
  }

  /**
   * Returns true if the authenticated user is an admin
   */
  isAdmin(): boolean {
    return this.authService.getUserRole() === 'ADMIN';
  }
}
