import {MerchPurchase} from './merchPurchase';
import {TicketAcquisitionDetails} from './ticket-acquisition';
import {CreateInvoice} from './invoice';

export interface ShoppingCart {
  merchPurchase: MerchPurchase;
  ticketAcquisition: TicketAcquisitionDetails;
  size: number;
  bonusPoints: number;
  invoiceDto?: CreateInvoice;
}
