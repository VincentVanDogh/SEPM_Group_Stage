import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Act} from '../../dtos/act';
import {Event} from '../../dtos/event';
import {EventService} from '../../services/event.service';
import {ToastrService} from 'ngx-toastr';
import {SeatDataForTicket, TicketUpdate} from '../../dtos/ticket';
import {ProfileService} from '../../services/profile.service';
import {Registration} from '../../dtos/registration';
import {ActStagePlan} from '../../dtos/stage-plan';
import {StagePlanService} from '../../services/stage-plan.service';
import {TicketStatus} from '../../dtos/ticket-status';
import {TicketService} from '../../services/ticket.service';
import {AuthService} from '../../services/auth.service';

export enum TicketGenerationMode {
  purchase,
  reservation
}

@Component({
  selector: 'app-ticket-generate',
  templateUrl: './ticket-generate.component.html',
  styleUrls: ['./ticket-generate.component.scss']
})
export class TicketGenerateComponent implements OnInit {

  mode: TicketGenerationMode = TicketGenerationMode.purchase;
  eventId: number;
  actId: number;
  allActs: Act[];
  act: Act;
  stageId: number;
  stageLoaded: boolean;
  totalTickets: number;
  seatData: SeatDataForTicket[];
  tickets: TicketUpdate[] = [];
  standingSectorDesignation: string[] = [];
  actStagePlan: ActStagePlan;
  user: Registration;
  event: Event;

  constructor(private route: ActivatedRoute,
              private eventService: EventService,
              private notification: ToastrService,
              private profileService: ProfileService,
              private stagePlanService: StagePlanService,
              private ticketService: TicketService,
              private authService: AuthService,
              private router: Router) {
    this.totalTickets = 0;
  }

  ngOnInit(): void {
    this.route.data.subscribe(data => {
      this.mode = data.mode;
    });
    const getId = this.route.snapshot.paramMap.get('id');
    if (getId) {
      this.eventId = Number.parseInt(getId, 10);
    }
    const getActId = this.route.snapshot.paramMap.get('actId');
    if (getActId) {
      this.actId = Number.parseInt(getActId, 10);
    }
    this.loadAct();
    this.getEvent();
    this.getUser();
    this.getActStagePlan();
  }

  loadAct() {
    this.eventService.getActsForEvent(this.eventId).subscribe({
      next: (acts: Act[]) => {
        this.allActs = acts;
        for (const val of this.allActs) {
          if (val.id === this.actId) {
            this.act = val;
            this.stageId = val.stageId;
            this.stageLoaded = true;
          }
        }
      },
      error: error => {
        this.notification.error('Could not load acts for event with id: ' + this.eventId);
      }
    });
  }

  calculateNumberOfTickets(): number {
    if (this.seatData) {
      this.totalTickets = 0;
      for (const val of this.seatData) {
        if (val) {
          this.totalTickets++;
        }
      }
    }
    return this.totalTickets;
  }

  generateTickets(): void {
    if (this.standingSectorDesignation.length === 0) {
      this.standingSectorDesignation = new Array(this.actStagePlan.sectorArray.length);
      this.generateStandingSectorDesignations();
    }
    if (this.seatData) {
      for (let i = 0; i < this.seatData.length; i++) {
        if (this.seatData[i]) {

          const ticket: TicketUpdate = {
            id: 0,
            buyerId: null,
            sectorMapId: 1,
            seatNo: 0,
            ticketFirstName: '',
            ticketLastName: '',
            reservation: TicketStatus.initialised,
            cancelled: false,
            actId: 0,
            creationDate: new Date()
          };

          ticket.seatNo = i;
          ticket.ticketFirstName = this.user.firstName;
          ticket.ticketLastName = this.user.lastName;
          ticket.actId = this.act.id;
          ticket.sectorMapId = this.getSectorIdOfSeat(i);
          if (this.seatData[i].rowNumber === 0) {
            ticket.seatNo = 0;
          }

          this.tickets.push(ticket);
        }
      }
    }
  }

  generateStandingSectorDesignations(): void {
    const identifiers = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'.split('');
    identifiers.reverse();
    for (let i = 0; i < this.standingSectorDesignation.length; i++) {
      if (this.actStagePlan.sectorArray[i] && this.actStagePlan.sectorArray[i].standing) {
        this.standingSectorDesignation[i] = identifiers.pop();
      }
    }
  }

  getSectorPositionInArray(sectorId: number): number {
    let atIndex = 0;
    this.actStagePlan.sectorArray.forEach((element, index) => {
      if (element && element.id){
        if (element.id === sectorId) {
          atIndex = index;
        }
      }
    });
    return atIndex;
  }

  getUser(): void {
    this.profileService.getUser().subscribe(data => {
      this.user = data;
    },);
  }

  getEvent(): void {
    this.eventService.getEventById(this.eventId).subscribe(data => {
      this.event = data;
    },);
  }

  getActStagePlan(): void {
    this.stagePlanService.getActStagePlanById(this.actId).subscribe(data => {
      this.actStagePlan = data;
    },);
  }

  getSectorIdOfSeat(seatNo: number): number {
    let sectorId = 0;
    this.actStagePlan.sectorArray.forEach((element, index) => {
      if (element && element.firstSeatNr){
        if (element.firstSeatNr <= seatNo) {
          sectorId = element.id;
        }
      }
    });
    return sectorId;
  }

  getPriceOfSector(sectorId: number): number {
    let price = 0;
    this.actStagePlan.sectorArray.forEach((element, index) => {
      if (element && element.id){
        if (element.id === sectorId) {
          price = element.price;
        }
      }
    });
    return price/100;
  }

  mapToTickets(fromSeatPlan: SeatDataForTicket[]): void {
    this.seatData = fromSeatPlan;
    this.tickets = [];
    this.generateTickets();
  }

  addToCart(): void {
    // eslint-disable-next-line @typescript-eslint/prefer-for-of
    for (let i = 0; i < this.tickets.length; i++) {
      if (this.mode === TicketGenerationMode.purchase) {
        this.tickets[i].reservation = TicketStatus.purchase;
      } else {
        this.tickets[i].reservation = TicketStatus.reserved;
      }
      this.ticketService.ticketCreate(this.tickets[i]).subscribe({
        next: data => {
          this.router.navigate(['shopping-cart']);
        },
        error: err => {
          console.error('Error creating tickets: ', err);
          this.notification.error(err.error.errors[0], 'Error creating tickets');
        }
      });
    }
  }

  getStart(start: Date): string {
    const unformattedDate = start?.toString();
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
    result += unformattedDate?.substring(8,10) + '.';
    result += unformattedDate?.substring(5,7) + '.';
    result += unformattedDate?.substring(0,4);
    result += ', ' + unformattedDate?.substring(11,16);

    return result;
  }

  /**
   * Returns true if the authenticated user is an admin
   */
  isAdmin(): boolean {
    return this.authService.getUserRole() === 'ADMIN';
  }


}
