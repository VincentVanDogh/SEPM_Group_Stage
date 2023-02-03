import { Component, OnInit } from '@angular/core';
import {Ticket, TicketUpdate} from '../../../dtos/ticket';
import {TicketAcquisitionService} from '../../../services/ticket.acquisition.service';
import {ToastrService} from 'ngx-toastr';
import {TicketAcquisition} from '../../../dtos/ticket-acquisition';
import {ActDetails} from '../../../dtos/act';
import {TicketService} from '../../../services/ticket.service';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {InvoiceService} from '../../../services/invoice.service';

@Component({
  selector: 'app-purchases',
  templateUrl: './purchases.component.html',
  styleUrls: ['./purchases.component.scss']
})
export class PurchasesComponent implements OnInit {
  ticketAcquisitions: TicketAcquisition[];
  acquisitionDelete: TicketAcquisition;
  ticketDelete: Ticket;

  constructor(
    private service: TicketAcquisitionService,
    private ticketService: TicketService,
    private invoiceService: InvoiceService,
    private notification: ToastrService,
    private modalService: NgbModal,
  ) { }

  ngOnInit(): void {
    this.loadTicketAcquisitions();
  }

  public acquisitionTotalPrice(acquisition: TicketAcquisition): number {
    return 0;
  }

  public actStartAsLocaleDate(act: ActDetails): string {
    const startDate: Date = new Date(act.start);
    const day: number = startDate.getDay() + 1;
    const month: string = startDate.toLocaleDateString('default', {month: 'long'});
    const time: string = startDate.getHours().toString().padStart(2,'0') + ':' + startDate.getMinutes().toString().padStart(2,'0');
    return `${day}. ${month} ${time}`;
  }

  public openDeleteTicketAcquisition(targetModal, acquisition: TicketAcquisition): void {
    this.acquisitionDelete = acquisition;
    this.modalService.open(targetModal, {
      backdrop: 'static',
      size: 'lg'
    });
  }

  public cancelTicketAcquisition(acquisition: TicketAcquisition) {
    this.service.deleteTicketAcquisition(acquisition.id).subscribe({
      next: () => {
        this.notification.success(`Ticket with the id: ${acquisition.id} has been successfully cancelled`);
        this.loadTicketAcquisitions();
      },
      error: err => {
        console.error(err);
        this.notification.error('Error cancelling ticket', err);
      }
    });
    this.modalService.dismissAll();
  }

  public openDeleteTicket(targetModal, ticket: Ticket): void {
    this.ticketDelete = ticket;
    this.modalService.open(targetModal, {
      backdrop: 'static',
      size: 'lg'
    });
  }

  public cancelTicket(ticket: Ticket): void {
    this.ticketService.ticketUpdate(this.ticketToTicketUpdateMapper(ticket)).subscribe({
      next: () => {
        this.notification.success(
          `Your ${ticket.reservation} to ${ticket.act.event.name} on ${ticket.act.start} has been successfully
          cancelled`
        );
        this.loadTicketAcquisitions();
      },
      error: err => {
        console.error(err);
        this.notification.error('Ticket cancellation failed', err);
      }
    });
    this.modalService.dismissAll();
  }

  public countTicketTypes(acquisition: TicketAcquisition, reserved: string) {
    let sum = 0;
    acquisition.tickets.forEach(entry => {
      if (entry.reservation.toString() === reserved) {
        sum += 1;
      }
    });
    return sum;
  }

  private loadTicketAcquisitions(): void {
    this.service.getTicketAcquisitions().subscribe({
      next: data => {
        this.ticketAcquisitions = data;
        this.ticketAcquisitions.forEach(value => {
          value.expanded = false;
        });
      },
      error: err => {
        console.error('Error fetching ticket acquisitions: ', err);
        if (err.status === 0){
          this.notification.error('Is the backend up?');
        } else {
          this.notification.error('Error fetching ticket acquisitions');
        }
      }
    });
  }

  private ticketToTicketUpdateMapper(ticket: Ticket): TicketUpdate {
    return {
      id: ticket.id,
      buyerId: ticket.buyerId,
      actId: ticket.act.id,
      cancelled: ticket.cancelled,
      reservation: ticket.reservation,
      seatNo: ticket.seatNo,
      sectorMapId: ticket.sectorMapId,
      ticketFirstName: ticket.ticketFirstName,
      ticketLastName: ticket.ticketLastName,
      creationDate: ticket.creationDate
    };
  }
}
