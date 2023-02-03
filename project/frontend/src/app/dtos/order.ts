import {TicketAcquisitionWithPrices} from './ticket-acquisition';
import {MerchPurchase} from './merchPurchase';
import {InvoiceType} from './invoice';

export interface Order {
  invoiceId: number;
  ticketAcquisition?: TicketAcquisitionWithPrices;
  merchPurchase?: MerchPurchase;
  orderDate: Date;
  invoiceType: InvoiceType;
  cancellations: CancellationForOrder[];
  expanded?: boolean; //For display on profile page only
}

export interface OrderPage {
  numberOfPages: number;
  orders: Order[];
}

export interface CancellationForOrder {
  cancellationId: number;
  ticketAcquisition?: TicketAcquisitionWithPrices;
  cancellationDate: Date;
  expanded?: boolean; //For display on profile page only
}


