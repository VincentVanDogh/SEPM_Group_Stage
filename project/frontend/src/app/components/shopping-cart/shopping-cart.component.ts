import {Component, OnInit} from '@angular/core';
import {Ticket, TicketUpdate} from '../../dtos/ticket';
import {TicketService} from '../../services/ticket.service';
import {ToastrService} from 'ngx-toastr';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {SharedService} from '../../services/shared.service';
import {MerchPurchaseService} from '../../services/merch-purchase.service';
import {MerchCartArticle} from '../../dtos/merchPurchase';
import {ShoppingCartService} from '../../services/shopping-cart.service';
import {Router} from '@angular/router';
import {MerchArticleQuantity} from '../../dtos/merchArticle';
import {TicketStatus} from '../../dtos/ticket-status';
import {TicketAcquisitionService} from '../../services/ticket.acquisition.service';
import {TicketAcquisition} from '../../dtos/ticket-acquisition';
import {AuthService} from '../../services/auth.service';
import {Title} from '@angular/platform-browser';

@Component({
  selector: 'app-shopping-cart',
  templateUrl: './shopping-cart.component.html',
  styleUrls: ['./shopping-cart.component.scss']
})

export class ShoppingCartComponent implements OnInit {
  tickets: Ticket[] = [];
  filteredTickets: Ticket[];
  ticketDelete: Ticket;
  reservation: TicketStatus;
  merchArticles: MerchCartArticle[] = [];
  points = 0;

  // CSS classes for picked Ticket option
  pickedAll = 'picked-all';
  pickedPurchases = 'not-picked';
  pickedReservations = 'not-picked';

  private timer;

  constructor(
    private titleService: Title,
    private service: TicketService,
    private cartService: ShoppingCartService,
    private merchPurchaseService: MerchPurchaseService,
    private ticketAcquisitionService: TicketAcquisitionService,
    private notification: ToastrService,
    private modalService: NgbModal,
    private sharedService: SharedService,
    private route: Router,
    private authService: AuthService
  ) {
    titleService.setTitle('Shopping Cart');
  }

  ngOnInit(): void {
    if (!this.authService.isLoggedIn()) {
      this.route.navigate(['login']);
    }
    this.loadShoppingCart();
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

  public setPickedPurchases(style: string) {
    switch (style) {
      case 'picked-all':
        this.pickedAll = style;
        this.pickedPurchases = 'not-picked';
        this.pickedReservations = 'not-picked';
        this.reservation = null;
        this.filteredTickets = this.tickets;
        break;
      case 'picked-purchases':
        this.pickedAll = 'not-picked';
        this.pickedPurchases = style;
        this.pickedReservations = 'not-picked';
        this.reservation = TicketStatus.purchase;
        this.fillTicketsShown();
        break;
      case 'picked-reservations':
        this.pickedAll = 'not-picked';
        this.pickedPurchases = 'not-picked';
        this.pickedReservations = style;
        this.reservation = TicketStatus.reserved;
        this.fillTicketsShown();
        break;
    }
  }

  ticketUpdate(ticket: Ticket): void {
    const ticketUpdate = this.mapTicketToTicketUpdate(ticket);
    if (ticketUpdate.reservation === TicketStatus.reserved) {
      ticketUpdate.reservation = TicketStatus.purchase;
    } else {
      ticketUpdate.reservation = TicketStatus.reserved;
    }
    this.service.ticketUpdate(ticketUpdate).subscribe({
      next: () => {
        this.loadShoppingCart();
      },
      error: err => {
        console.error(err);
        err.error.errors.forEach(e => {
          this.notification.error('Error updating ticket: ' + e);
        });
      }
    });
  }

  mapTicketToTicketUpdate(ticket: Ticket): TicketUpdate {
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

  mapTicketsToTicketUpdates(tickets: Ticket[]): TicketUpdate[] {
    const result: TicketUpdate[] = [];
    // eslint-disable-next-line @typescript-eslint/prefer-for-of
    for (let i = 0; i < tickets.length; i++) {
      result.push(this.mapTicketToTicketUpdate(tickets[i]));
    }
    return result;
  }

  openDelete(targetModal, ticket: Ticket): void {
    this.ticketDelete = ticket;
    this.modalService.open(targetModal, {
      backdrop: 'static',
      size: 'lg'
    });
  }

  public deleteTicket(): void {
    this.service.deleteTicket(this.ticketDelete.id).subscribe({
      next: () => {
        this.notification.success(
          `Ticket ${this.ticketDelete.reservation === TicketStatus.reserved ? 'reservation' : 'purchase'}
          to ${this.ticketDelete.act.event.name}
          on ${this.getStart(this.ticketDelete.act.start)} has been successfully removed from the shopping cart`
        );
        this.loadShoppingCart();
      },
      error: err => {
        console.error(err);
        this.notification.error(
          `Ticket ${this.ticketDelete.reservation === TicketStatus.reserved ? 'reservation' : 'purchase'}
          to ${this.ticketDelete.act.event.name}
          on ${this.getStart(this.ticketDelete.act.start)} could not be removed: ` + err.error.error
        );
      }
    });
    this.modalService.dismissAll();
  }

  public deleteAllTickets(status: string): void {
    this.service.deleteAllTickets(status).subscribe({
      next: () => {
        this.notification.success(
          `All tickets ${status === null ? '' : `with the status ${status}`}
          have been successfully removed from the shopping cart`
        );
        this.loadShoppingCart();
      },
      error: err => {
        console.error(err);
        this.notification.error(
          `All tickets ${status === null ? '' : `with the status ${status}`}
          failed to be removed from the shopping cart`
        );
      }
    });
  }

  public evalTicketType(reservation: TicketStatus): string {
    return reservation === TicketStatus.reserved ? 'RESERVATION' : 'PURCHASE';
  }

  public deleteMerch(merchId: number): void {
    this.merchPurchaseService.deleteMerchArticle(merchId).subscribe({
      next: () => {
        this.notification.success('Merch deleted from shopping cart');
        this.loadShoppingCart();
      },
      error: err => {
        console.error(err);
        this.notification.error('Merch could not be deleted from cart');
      }
    });
  }

  public deleteAllMerch(): void {
    this.merchPurchaseService.deleteAllMerchArticlesInCart().subscribe({
      next: () => {
        this.notification.success('Every merch deleted from the shopping cart');
        this.loadShoppingCart();
      },
      error: err => {
        console.error(err);
        this.notification.error('Merch could not be deleted from cart');
      }
    });
  }

  public onReserve(): void {
    const toSend: TicketAcquisition = {
      tickets: this.mapTicketsToTicketUpdates(this.tickets),
      cancelled: false
    };
    this.ticketAcquisitionService.purchaseTickets(toSend).subscribe({
      next: () => {
        this.notification.success('Your order has been processed successfully');
        this.sharedService.setCartStatus(0, null);
        this.route.navigate(['/profile']);
      },
      error: err => {
        console.error(err);
        this.notification.error(err.error.errors[0], 'Your order could not be processed');
      }
    });
  }

  allTicketsReservations(): boolean {
    let hasPurchase = false;
    for (const ticket of this.tickets) {
      hasPurchase = ticket.reservation === TicketStatus.purchase;
      if (hasPurchase) {
        return !hasPurchase;
      }
    }
    return !hasPurchase && this.merchArticles.length === 0;
  }

  public ticketPurchaseTotalPrice(): number {
    let totalPrice = 0;
    this.tickets.forEach(ticket => {
      if (ticket.reservation === TicketStatus.purchase) {
        totalPrice += ticket.price;
      }
    });
    return totalPrice;
  }

  public merchPointsTotalPrice(): number {
    let totalPoints = 0;
    this.merchArticles.forEach(article => {
      if (article.bonusUsed) {
        totalPoints += article.bonusPointPrice * article.articleCount;
      }
    });
    return totalPoints;
  }

  public merchTotalPrice(): number {
    let totalPrice = 0;
    this.merchArticles.forEach(merch => {
      if (!merch.bonusUsed) {
        totalPrice += merch.price * merch.articleCount;
      }
    });
    return totalPrice;
  }

  public cartTotalPrice(): number {
    return this.ticketPurchaseTotalPrice() + this.merchTotalPrice();
  }

  public priceAsString(price: number): string {
    return `€ ${(price / 100).toFixed(2)}`;
  }

  public updateMerch(article: MerchCartArticle) {
    const quantityDto: MerchArticleQuantity = {
      articleNr: article.articleNr,
      bonusUsed: article.bonusUsed,
      quantity: article.articleCount
    };
    this.merchPurchaseService.updateMerchArticle(quantityDto).subscribe({
      next: () => {
        this.loadShoppingCart();
      },
      error: err => {
        console.error(err);
        this.notification.error(err, 'Failed to update merch article in the shopping cart');
      }
    });
  }

  public pointPurchaseAvailable(article: MerchCartArticle): boolean {
    if (!article.bonusUsed) {
      return article.bonusPointPrice !== null && article.bonusPointPrice <= this.points;
    }
    return article.bonusPointPrice !== null && article.bonusPointPrice <= this.points + article.bonusPointPrice * article.articleCount;
  }

  public getArticleName(article: MerchCartArticle): string {
    return `${article.artistOrEventName} ${article.name}`;
  }

  public getTicketStatusAmount(status: string) {
    let ticketStatusAmount = 0;
    this.tickets.forEach(ticket => {
      if (ticket.reservation.toString() === status) {
        ticketStatusAmount++;
      }
    });
    return ticketStatusAmount;
  }

  private setAllowedPointAmount(): void {
    this.merchArticles.forEach(article => {
      article.allowedPointAmount = [1];
      while (this.points + article.bonusPointPrice * article.articleCount
      >= article.bonusPointPrice * (article.allowedPointAmount.length + 1) && article.allowedPointAmount.length < 10) {
        article.allowedPointAmount.push(article.allowedPointAmount.length + 1);
      }
    });
  }

  private loadShoppingCart(): void {
    this.cartService.getShoppingCart().subscribe({
      next: cartItems => {
        this.tickets = cartItems.ticketAcquisition.tickets;
        this.ticketTimeRemaining();
        this.fillTicketsShown();
        this.ticketCountdown();

        if (cartItems.merchPurchase !== null && cartItems.merchPurchase.articles !== null &&
          cartItems.merchPurchase.articles.length !== 0) {
          this.merchArticles = cartItems.merchPurchase.articles;
        } else {
          this.merchArticles = [];
        }
        this.points = cartItems.bonusPoints;
        this.sharedService.setCartStatus(cartItems.size, cartItems.bonusPoints);
        this.setAllowedPointAmount();
      },
      error: err => {
        console.error('Error fetching cart: ', err);
        if (err.status === 0){
          this.notification.error('Is the backend up?');
        } else {
          this.notification.error('Error fetching the shopping cart');
        }
      }
    });
  }

  private fillTicketsShown(): void {
    this.filteredTickets = [];
    this.tickets.forEach(entry => {
      if (this.reservation == null) {
        this.filteredTickets.push(entry);
      }
      if (entry.reservation === this.reservation) {
        this.filteredTickets.push(entry);
      }
    });
  }

  private ticketCountdown(): void {
    if (this.timer) {
      clearInterval(this.timer);
    }
    if (this.tickets.length !== 0) {
      this.timer = setInterval(() => {
        if (this.authService.isLoggedIn()) {
          this.ticketTimeRemaining();
        } else {
          clearInterval(this.timer);
        }
      }, 1000);
    }
  }

  private ticketTimeRemaining(): void {
    let ticketAmount = 0;
    this.tickets.forEach(ticket => {
      if (ticket.remainingTime) {
        if (ticket.remainingTime.minutes <= 0 && ticket.remainingTime.seconds <= 0) {
          this.loadShoppingCart();
        } else if (ticket.remainingTime.seconds <= 0) {
          ticket.remainingTime.minutes = ticket.remainingTime.minutes - 1;
          ticket.remainingTime.seconds = 59;
        } else {
          ticket.remainingTime.seconds = ticket.remainingTime.seconds - 1;
          if (ticket.remainingTime.minutes === 1 && ticket.remainingTime.seconds === 0 && ticket.notified === undefined) {
            ticketAmount++;
            ticket.notified = true;
          }
        }
      } else {
        this.setTicketRemainingTime(ticket);
      }
    });
    if (ticketAmount > 0) {
      this.notification.warning(`${ticketAmount} ${ticketAmount > 1 ? 'tickets' : 'ticket'} about to expire in one minute`);
    }
  }

  private setTicketRemainingTime(ticket: Ticket): void {
    const timeDifference = this.getTicketTimeDifference(ticket);
    ticket.remainingTime = {
      minutes: Math.floor(timeDifference/60),
      seconds: timeDifference % 60
    };
  }

  private getTicketTimeDifference(ticket: Ticket): number {
    const time = new Date();
    const ticketTime = new Date(ticket.creationDate);
    const timeSeconds = time.getSeconds() + time.getMinutes() * 60;
    const ticketSeconds = ticketTime.getSeconds() + ticketTime.getMinutes() * 60;
    return timeSeconds < ticketSeconds ? ticketSeconds + 10 * 60 - 60 * 60 + timeSeconds : ticketSeconds + 10 * 60 - timeSeconds;
  }
}
