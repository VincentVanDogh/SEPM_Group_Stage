import {Address} from './address';

export interface CreateInvoice {
  //referenceNr?: number;
  invoiceType: InvoiceType;
  address: Address;
  recipientName: string;
}

export enum InvoiceType {
  regular = 'REGULAR',
  cancellation = 'CANCELLATION'
}
