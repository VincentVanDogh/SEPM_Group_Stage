import {Component, OnInit} from '@angular/core';
import {Reservation, ReservationPage, TicketAcquisitionWithPrices} from '../../../dtos/ticket-acquisition';
import {Ticket} from '../../../dtos/ticket';
import {TicketService} from '../../../services/ticket.service';
import {InvoiceService} from '../../../services/invoice.service';
import {ToastrService} from 'ngx-toastr';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {ReservationService} from '../../../services/reservation.service';
import {ActivatedRoute, Router} from '@angular/router';
import {TicketAcquisitionService} from '../../../services/ticket.acquisition.service';
import {TicketStatus} from '../../../dtos/ticket-status';
import {Observable} from 'rxjs';

@Component({
  selector: 'app-reservations',
  templateUrl: './reservations.component.html',
  styleUrls: ['./reservations.component.scss']
})
export class ReservationsComponent implements OnInit {
  reservations: Reservation[];
  acquisitionDelete: Reservation;
  ticketDelete: Ticket;
  currentPageId = 1;
  pageNumbers = [];
  displayAll = false;

  constructor(
    private service: ReservationService,
    private ticketService: TicketService,
    private ticketAcquisitionService: TicketAcquisitionService,
    private invoiceService: InvoiceService,
    private notification: ToastrService,
    private modalService: NgbModal,
    private route: Router,
    private activatedRoute: ActivatedRoute
  ) { }

  ngOnInit(): void {
    this.loadReservations();
  }

  getStatus(ticket: Ticket): string {
    if (ticket.cancelled) {
      return ticket.reservation === TicketStatus.reserved ? 'RESERVATION CANCELLED' : 'PURCHASE CANCELLED';
    }
    return ticket.reservation;
  }

  public getPrice(ticket: Ticket): string {
    return (ticket.price / 100).toFixed(2);
  }

  public acquisitionTotalPrice(reservation: Reservation): string {
    let totalPrice = 0;
    reservation.tickets.forEach(value => {
      totalPrice = totalPrice + value.price;
    });
    return (totalPrice / 100).toFixed(2);
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

  public openDeleteTicketAcquisition(targetModal, acquisition: Reservation): void {
    this.acquisitionDelete = acquisition;
    this.modalService.open(targetModal, {
      backdrop: 'static',
      size: 'lg'
    });
  }

  public cancelTicketAcquisition(acquisition: Reservation) {
    const cancellation: TicketAcquisitionWithPrices = {
      id: acquisition.reservationNo,
      buyerId: acquisition.customerId,
      tickets: acquisition.tickets,
      cancelled: false
    };
    this.ticketAcquisitionService.cancelTicketAcquisition(cancellation).subscribe({
      next: () => {
        this.notification.success(`Ticket acquisition with the ID number ${acquisition.reservationNo} has been successfully cancelled`);
        this.loadReservations();
      },
      error: err => {
        console.error(err);
        this.notification.error(err.message, 'Error cancelling ticket acquisition');
      }
    });
    this.modalService.dismissAll();
  }

  public openDeleteTicket(targetModal, ticket: Ticket, reservation: Reservation): void {
    this.acquisitionDelete = reservation;
    this.ticketDelete = ticket;
    this.modalService.open(targetModal, {
      backdrop: 'static',
      size: 'lg'
    });
  }

  public cancelTicket(ticket: Ticket, acquisition: Reservation): void {
    const cancellation: TicketAcquisitionWithPrices = {
      id: acquisition.reservationNo,
      buyerId: acquisition.customerId,
      tickets: [ticket],
      cancelled: false
    };
    this.ticketAcquisitionService.cancelTicketAcquisition(cancellation).subscribe({
    // Calling delete ticket on ticket that has a TA results in cancellation of ticket
      next: () => {
        this.notification.success(
          `Your ticket ${ticket.reservation === TicketStatus.reserved ? 'reservation' : 'purchase'} to
          ${ticket.act.event.name} on ${this.getStart(ticket.act.start)} has been successfully cancelled`
        );
        this.loadReservations();
      },
      error: err => {
        console.error(err);
        this.notification.error(err.error.messages[0], 'Ticket cancellation failed');
      }
    });
    this.modalService.dismissAll();
  }

  public buyReservedTicket(ticket: Ticket) {
    this.ticketService.buyReservedTicket(ticket.id).subscribe({
      next: () => {
        this.route.navigate(['/shopping-cart']);
      },
      error: err => {
        console.error(err);
        this.notification.error(err.error.messages[0], 'Error buying reserved ticket');
      }
    });
    this.modalService.dismissAll();
  }

  public buyReservedTickets(acquisitionId: number) {
    this.ticketAcquisitionService.buyReservedTickets(acquisitionId).subscribe({
      next: () => {
        this.route.navigate(['/shopping-cart']);
      },
      error: err => {
        console.error(err);
        this.notification.error(err.error.messages[0], 'Error buying reserved ticket(s)');
      }
    });
    this.modalService.dismissAll();
  }

  public showPage(id: number): void {
    this.currentPageId = id;
    this.loadReservations();
  }

  public showNextPage(): void {
    this.currentPageId++;
    this.loadReservations();
  }

  public showPrevPage(): void {
    this.currentPageId--;
    this.loadReservations();
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

  toggleDisplayModes() {
    this.displayAll = !this.displayAll;
    this.loadReservations();
  }

  hasActHappend(ticket: Ticket): boolean {
    const date = new Date();
    const start = new Date(ticket.act.start);
    return start < date;

  }

  public cancelReservationAvailable(reservation: Reservation): boolean {
    //are there any tickets not cancelled left?
    let showCancel = false;
    reservation.tickets.forEach(entry => { //should this get calculated in backend instead?
      showCancel = showCancel || (!entry.cancelled && !this.hasActHappend(entry));
    });
    return showCancel;
  }

  private loadReservations(): void {
    let reservationObservable: Observable<ReservationPage>;
    if (this.displayAll) {
      reservationObservable = this.service.getAllReservations(this.currentPageId);
    } else {
      reservationObservable = this.service.getReservations(this.currentPageId);
    }
    reservationObservable.subscribe({
      next: data => {
        this.reservations = data.reservations;
        this.pageNumbers = Array(data.numberOfPages).fill(0);
        this.reservations.forEach(value => {
          value.expanded = false;
        });
      },
      error: err => {
        console.error('Error fetching reservations: ', err);
        if (err.status === 0){
          this.notification.error('Is the backend up?');
        } else {
          this.notification.error('Error fetching reservations');
        }
      }
    });
  }
}
