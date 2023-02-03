import {Component, Input, OnInit} from '@angular/core';
import {Card, TicketUpdate} from '../../../dtos/ticket';
import {TicketAcquisitionService} from '../../../services/ticket.acquisition.service';
import {ToastrService} from 'ngx-toastr';
import {Router} from '@angular/router';
import {SharedService} from '../../../services/shared.service';
import {ShoppingCartService} from '../../../services/shopping-cart.service';
import {CreateInvoice, InvoiceType} from '../../../dtos/invoice';

@Component({
  selector: 'app-checkout',
  templateUrl: './checkout.component.html',
  styleUrls: ['./checkout.component.scss']
})
export class CheckoutComponent implements OnInit {
  @Input() tickets: TicketUpdate[] = [];

  card: Card = {
    method: 'PAYPAL'
  };

  billingInformation = {
    address: {
      street: '',
      city: '',
      postalCode: null,
      country: ''
    },
    firstName: '',
    lastName: ''
  };

  constructor(
    private service: TicketAcquisitionService,
    private shoppingCartService: ShoppingCartService,
    private notification: ToastrService,
    private route: Router,
    private sharedService: SharedService
  ) { }

  ngOnInit(): void {
  }

  onSubmit(): void {
    const createInvoice: CreateInvoice = {
      invoiceType: InvoiceType.regular,
      address: {
        id: null,
        street: this.billingInformation.address.street,
        city: this.billingInformation.address.city,
        postalCode: this.billingInformation.address.postalCode,
        country: this.billingInformation.address.country
      },
      recipientName: this.billingInformation.firstName + ' ' + this.billingInformation.lastName
    };
    this.shoppingCartService.finalizePurchase(createInvoice).subscribe({
      next: data => {
        this.notification.success('Your order has been processed successfully');
        this.sharedService.setCartStatus(0, data.bonusPoints);
        this.route.navigate(['/profile']);
      },
      error: err => {
        console.error(err);
        this.notification.error('Your order could not be processed', err);
      }
    });
  }
}
