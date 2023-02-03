import {Component, OnInit} from '@angular/core';
import {TicketAcquisitionService} from '../../../services/ticket.acquisition.service';
import {TicketService} from '../../../services/ticket.service';
import {InvoiceService} from '../../../services/invoice.service';
import {ToastrService} from 'ngx-toastr';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {TicketAcquisitionWithPrices} from '../../../dtos/ticket-acquisition';
import {Ticket} from '../../../dtos/ticket';
import {MerchCartArticle} from '../../../dtos/merchPurchase';
import {MerchPurchaseService} from '../../../services/merch-purchase.service';
import {CancellationForOrder, Order} from '../../../dtos/order';
import {OrderService} from '../../../services/order.service';
import {TicketStatus} from '../../../dtos/ticket-status';
import {Router} from '@angular/router';

@Component({
  selector: 'app-orders',
  templateUrl: './orders.component.html',
  styleUrls: ['./orders.component.scss']
})
export class OrdersComponent implements OnInit {
  orders: Order[];
  acquisitionDelete: TicketAcquisitionWithPrices;
  ticketDelete: Ticket;
  currentPageId = 1;
  pageNumbers = [];

  constructor(private ticketAcqService: TicketAcquisitionService,
              private merchService: MerchPurchaseService,
              private ticketService: TicketService,
              private orderService: OrderService,
              private invoiceService: InvoiceService,
              private notification: ToastrService,
              private modalService: NgbModal,
              private route: Router
              ) { }


  ngOnInit(): void {
    this.loadOrders();
  }

  downloadInvoice(id: number){
    this.invoiceService.getInvoiceById(id).subscribe(res => {
      window.open(window.URL.createObjectURL(res), '_blank');
    });
  }

  downloadCancellation(id: number){
    this.invoiceService.getCancellationById(id).subscribe(res => {
      window.open(window.URL.createObjectURL(res), '_blank');
    });
  }

  public getTicketPrice(ticket: Ticket): string {
    return (ticket.price / 100).toFixed(2);
  }

  public getArticlePrice(article: MerchCartArticle): string {
    return article.bonusUsed ? `${article.bonusPointPrice} Points` : (article.articleCount * article.price / 100).toFixed(2);
  }

  public orderTotalPrice(order: Order): string {
    let totalPrice = 0;
    if (order.ticketAcquisition !== null) {
      order.ticketAcquisition.tickets.forEach(value => {
        if (value.reservation === TicketStatus.purchase) {
          totalPrice = totalPrice + value.price;
        }
      });
    }
    if (order.merchPurchase !== null) {
      order.merchPurchase.articles.forEach(value => {
        totalPrice = totalPrice + value.price * value.articleCount;
      });
    }
    return (totalPrice / 100).toFixed(2);
  }

  public cancellationTotalPrice(cancellationForOrder: CancellationForOrder): string {
    let totalPrice = 0;
    if (cancellationForOrder.ticketAcquisition !== null) {
      cancellationForOrder.ticketAcquisition.tickets.forEach(value => {
        if (value.reservation === TicketStatus.purchase) {
          totalPrice = totalPrice + value.price;
        }
      });
    }
    return (-1 * totalPrice / 100).toFixed(2);
  }

  public orderReservedValue(order: Order): string {
    let totalPrice = 0;
    if (order.ticketAcquisition !== null) {
      order.ticketAcquisition.tickets.forEach(value => {
        if (value.reservation === TicketStatus.reserved) {
          totalPrice = totalPrice + value.price;
        }
      });
    }
    return (totalPrice / 100).toFixed(2);
  }

  getStart(start: Date, long: boolean): string {
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
    if (long) {
      result += ', ' + unformattedDate.substring(11,16);
    }
    return result;
  }

  public openDeleteTicketAcquisition(targetModal, acquisition: TicketAcquisitionWithPrices): void {
    this.acquisitionDelete = acquisition;
    this.modalService.open(targetModal, {
      backdrop: 'static',
      size: 'lg'
    });
  }

  getInvoiceReferenceNo(order: Order): string {
    return 'I' + order.invoiceId;
  }

  getCancellationReferenceNo(cancellationForOrder: CancellationForOrder): string {
    return 'C' + cancellationForOrder.cancellationId;
  }

  getStatus(ticket: Ticket): string {
    if (ticket.cancelled) {
      return ticket.reservation === TicketStatus.reserved ? 'RESERVATION CANCELLED' : 'PURCHASE CANCELLED';
    }
    return ticket.reservation;
  }

  getBackgroundColorString(ticket: Ticket): string {
    return ticket.cancelled ? 'light-red-bg' : ticket.reservation === TicketStatus.reserved ? 'light-blue-bg' : 'light-green-bg';
  }

  public cancelTicketAcquisition(acquisition: TicketAcquisitionWithPrices) {
    this.ticketAcqService.cancelTicketAcquisition(acquisition).subscribe({
      next: () => {
        this.notification.success(`Ticket acquisition with the ID number ${acquisition.id} has been successfully cancelled`);
        this.loadOrders();
      },
      error: err => {
        console.error(err);
        this.notification.error(err.error.messages[0], 'Error cancelling ticket acquisition');
      }
    });
    this.modalService.dismissAll();
  }

  public openDeleteTicket(targetModal, ticket: Ticket, acquisition: TicketAcquisitionWithPrices): void {
    this.ticketDelete = ticket;
    this.acquisitionDelete = acquisition;
    this.modalService.open(targetModal, {
      backdrop: 'static',
      size: 'lg'
    });
  }

  public cancelTicket(ticket: Ticket, acquisition: TicketAcquisitionWithPrices): void {
    const cancellation: TicketAcquisitionWithPrices = {
      id: acquisition.id,
      buyerId: acquisition.buyerId,
      tickets: [ticket],
      cancelled: acquisition.cancelled
    };
    this.ticketAcqService.cancelTicketAcquisition(cancellation).subscribe({
    // Calling delete ticket on ticket that has a TA results in cancellation of ticket
      next: () => {
        this.notification.success(
          `Your ticket ${ticket.reservation === TicketStatus.reserved ? 'reservation' : 'purchase'} to
          ${ticket.act.event.name} on ${this.getStart(ticket.act.start, true)} has been successfully cancelled`
        );
        this.loadOrders();
      },
      error: err => {
        console.error(err);
        this.notification.error(err.error.messages[0], 'Ticket cancellation failed');
      }
    });
    this.modalService.dismissAll();
  }

  public buyTicketAcquisitionReservations(id: number) {
    this.ticketAcqService.buyReservedTickets(id).subscribe({
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

  public ticketIsReservation(ticket: Ticket) {
    return ticket.reservation === TicketStatus.reserved;
  }

  hasActHappend(ticket: Ticket): boolean {
    const date = new Date();
    const start = new Date(ticket.act.start);
    return start < date;

  }

  public cancelOrderAvailable(order: Order): boolean {
    let showCancel = order.ticketAcquisition
      && !order.ticketAcquisition.cancelled;
    if (!showCancel) { //we already know not to offer the cancellation option
      return showCancel;
    }
    //are there any tickets not cancelled left?
    showCancel = false;
    order.ticketAcquisition.tickets.forEach(entry => { //should this get calculated in backend instead?
      showCancel = showCancel || (!entry.cancelled && !this.hasActHappend(entry));
    });
    return showCancel;
  }

  public countReservations(order: Order) {
    let sum = 0;
    if (order.ticketAcquisition != null) {
      order.ticketAcquisition.tickets.forEach(entry => {
        if (entry.reservation.toString() === 'RESERVED') {
          sum += 1;
        }
      });
    }
    return sum;
  }

  public countPurchases(order: Order) {
    let sum = 0;
    if (order.ticketAcquisition !== null) {
      order.ticketAcquisition.tickets.forEach(entry => {
        if (entry.reservation.toString() === 'PURCHASED') {
          sum += 1;
        }
      });
    }
    if (order.merchPurchase !== null) {
      order.merchPurchase.articles.forEach(entry => {
        sum += entry.articleCount;
      });
    }
    return sum;
  }

  public countCancelledItems(cancellation: CancellationForOrder) {
    let sum = 0;
    if (cancellation.ticketAcquisition !== null) {
      cancellation.ticketAcquisition.tickets.forEach(entry => {
        if (entry.reservation.toString() === 'PURCHASED') {
          sum += 1;
        }
      });
    }
    return -sum;
  }

  public getName(article: MerchCartArticle): string {
    return `${article.artistOrEventName} ${article.name}`;
  }

  public showPage(id: number): void {
    this.currentPageId = id;
    this.loadOrders();
  }

  public showNextPage(): void {
    this.currentPageId++;
    this.loadOrders();
  }

  public showPrevPage(): void {
    this.currentPageId--;
    this.loadOrders();
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

  private loadOrders(): void {
    this.orderService.getOrders(this.currentPageId).subscribe({
      next: data => {
        this.orders = data.orders;
        this.pageNumbers = Array(data.numberOfPages).fill(0);
        this.orders.forEach(value => {
          value.expanded = false;
        });
      },
      error: err => {
        console.error(err);
        this.notification.error('Error fetching orders', err);
      }
    });
  }
}
