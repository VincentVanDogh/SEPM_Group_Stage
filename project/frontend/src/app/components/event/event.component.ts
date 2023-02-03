import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {Event, EventPage} from '../../dtos/event';
import {UntypedFormBuilder, UntypedFormGroup} from '@angular/forms';
import {EventService} from '../../services/event.service';
import {NgbPaginationConfig} from '@ng-bootstrap/ng-bootstrap';
import {AuthService} from '../../services/auth.service';
import {ToastrService} from 'ngx-toastr';
import {ActivatedRoute, Router} from '@angular/router';
import {SearchEvent} from '../../dtos/search-event';
import {EventType} from '../../dtos/eventType';
import {Title} from '@angular/platform-browser';
import {Artist} from '../../dtos/artist';


@Component({
  selector: 'app-event',
  templateUrl: './event.component.html',
  styleUrls: ['./event.component.scss']
})
export class EventComponent implements OnInit {


  error = false;
  errorMessage = '';
  // After first submission attempt, form validation will start
  submitted = false;

  eventType = EventType;
  searchForm: UntypedFormGroup;
  selectedCategory = 'All Categories';
  currentPageId = 1;
  pageNumbers = [];
  private events: Event[];


  constructor(
    private titleService: Title,
    private eventService: EventService,
    private ngbPaginationConfig: NgbPaginationConfig,
    private formBuilder: UntypedFormBuilder,
    private cd: ChangeDetectorRef,
    public authService: AuthService,
    private notification: ToastrService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    titleService.setTitle('Ticketline | Events');
    this.searchForm = this.formBuilder.group({
      search: [''],
      category: [],
      dateFrom: [new Date().toISOString().substring(0,10)],
      dateTo:[''],
      location:['']
    });
  }

  ngOnInit() {
    const pageId = this.route.snapshot.queryParamMap.get('page');
    if (pageId) {
      this.currentPageId = Number.parseInt(pageId, 10);
    }
    this.searchEvents();
  }

  startSearch(): void {
    this.currentPageId = 1;
    this.searchEvents();
    this.router.navigate(['events'], {queryParams: {page: 1}});
  }

  /**
   * Returns all events matching the search form for one page
   */
  searchEvents(): void {
    let category = this.searchForm.controls.category.value;
    if (category !== 'All Categories') {
      category = category as EventType;
    } else {
      category = null;
    }
    const searchRequest: SearchEvent = new SearchEvent(
      this.searchForm.controls.search.value,
      this.searchForm.controls.dateFrom.value,
      this.searchForm.controls.dateTo.value,
      category,
      this.searchForm.controls.location.value);
    if (category == null) {
      this.eventService.searchEventWithoutCategory(searchRequest, this.currentPageId).subscribe({
        next: (eventPage: EventPage) => {
          this.events = eventPage.events;
          this.pageNumbers = Array(eventPage.numberOfPages).fill(0);
          if (this.events.length === 0 && this.currentPageId !== 1 && eventPage.numberOfPages >= 1) {
            this.emptyPage();
          }
        },
        error: error => {
          console.error(error);
          this.notification.error(error.error.errors);
        }
      });
    } else {
      this.eventService.searchEvent(searchRequest, this.currentPageId).subscribe({
        next: (eventPage: EventPage) => {
          this.events = eventPage.events;
          this.pageNumbers = Array(eventPage.numberOfPages).fill(0);
          if (this.events.length === 0 && this.currentPageId !== 1 && eventPage.numberOfPages >= 1) {
            this.emptyPage();
          }
          if (this.events.length === 0 && eventPage.numberOfPages < 1) {
            this.notification.error('No matching search results found.');
          }
        },
        error: error => {
          console.error(error);
          this.notification.error(error.error.errors);
        }
      });
    }
  }

  reloadEvents(): void {
    this.currentPageId = 1;
    this.searchForm.reset({
      search: '',
      dateFrom: new Date().toISOString().substring(0,10),
      dateTo: '',
      location: ''
    });
    this.searchEvents();
    this.router.navigate(['events'], {queryParams: {page: 1}});
  }

  getEvents(): Event[] {
    return this.events;
  }

  formatArtistsForEvent(artists: Artist[]) {

    if (artists && artists.length > 0) {
      let artistString = 'Hosting: ';

      artists.forEach((artist, index) => {
        if (artist) {
          if (artist.stageName) {
            artistString += artist.stageName;
          } else if (artist.bandName) {
            artistString += artist.bandName;
          } else {
            if (artist.firstName) {
              artistString += artist.firstName;
            }
            if (artist.lastName) {
              artistString += artist.lastName;
            }
          }
          if (artists.length > 1 && index < artists.length-1) {
            artistString += ', ';
          }
        }
      });

      return artistString;
    } else {
      return '';
    }
  }

  showActs(id: number): void {
    this.router.navigate(['events/detail', id], {queryParams: {dateFrom: this.searchForm.controls.dateFrom.value,
        dateTo:this.searchForm.controls.dateTo.value}});
  }

  showPage(id: number): void {
    this.currentPageId = id;
    this.searchEvents();
    scroll(0,0);
    this.router.navigate(['events'], {queryParams: {page: this.currentPageId}});
  }

  showNextPage(): void {
    this.currentPageId++;
    this.searchEvents();
    scroll(0,0);
    this.router.navigate(['events'], {queryParams: {page: this.currentPageId}});
  }

  showPrevPage(): void {
    this.currentPageId--;
    this.searchEvents();
    scroll(0,0);
    this.router.navigate(['events'], {queryParams: {page: this.currentPageId}});
  }

  emptyPage(){
    this.currentPageId = 1;
    this.searchEvents();
    this.notification.info('New search parameters have been applied. Directing to page 1 of search results');
    this.router.navigate(['events'], {queryParams: {page: this.currentPageId}});
  }

  showNumberPagination(i: number): boolean {

    if (this.pageNumbers.length > 0 && this.pageNumbers.length < 6) {
      return this.currentPageId !== i;
    } else {
      if (Math.abs(this.currentPageId - i) > 0 && Math.abs(this.currentPageId - i) < 3) {
        return true;
      }
      if ((this.currentPageId > 3 && i === 1) || (this.currentPageId === 5 && i === 2)) {
        return true;
      }
      if (this.currentPageId < this.pageNumbers.length - 2 && i === this.pageNumbers.length) {
        return true;
      }
      if (this.currentPageId === this.pageNumbers.length - 4 && i === this.pageNumbers.length - 1) {
        return true;
      }
    }
    return false;
  }

  showDotsPagination(i: number): boolean {

    if (this.pageNumbers.length > 5) {
      if (Math.abs(this.currentPageId - i) === 3 && i > 2 && i < this.pageNumbers.length - 1) {
        return true;
      }
    }
    return false;
  }


  /**
   * Returns true if the authenticated user is an admin
   */
  isAdmin(): boolean {
    return this.authService.getUserRole() === 'ADMIN';
  }

  /**
   * Loads all events from the backend for one page
   */
  private loadAllEvents() {
    this.eventService.getEvent(this.currentPageId).subscribe({
      next: (eventPage: EventPage) => {
        this.events = eventPage.events;
        this.pageNumbers = Array(eventPage.numberOfPages).fill(0);
      },
      error: error => {
        console.error(error);
        this.notification.error('Could not load events');
      }
    });
  }
}
