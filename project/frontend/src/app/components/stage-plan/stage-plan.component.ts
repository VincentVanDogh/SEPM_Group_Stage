import { Component, Input, OnInit } from '@angular/core';
import {DisplayMode} from '../../dtos/stage-plan';
import {InvoiceService} from '../../services/invoice.service';

@Component({
  selector: 'app-stage-plan',
  templateUrl: './stage-plan.component.html',
  styleUrls: ['./stage-plan.component.scss']
})
export class StagePlanComponent implements OnInit {

  @Input() mode: DisplayMode;
  @Input() id: number;
  @Input() actId: number;

  constructor(private invoiceService: InvoiceService) { }

  ngOnInit(): void {
    // this.downloadInvoice(1);
  }

  // // //downloadFile(fileJson: FileExpenseJson){
  // downloadInvoice(id: number){
  //   // console.log('download invoice with id ', id);
  //   this.invoiceService.getInvoiceById(id).subscribe(res => {
  //     window.open(window.URL.createObjectURL(res), '_blank');
  //   });
  //   // this.invoiceService.getInvoiceById(1).subscribe(data => {
  //   //   const blob = new Blob([data.blob], {type: 'application/pdf'});
  //   //   const url = window.URL.createObjectURL(blob);
  //   //   // const filename = fileJson.fileName;
  //   //   // if (navigator.msSaveOrOpenBlob) {
  //   //   //   navigator.msSaveBlob(blob, filename);
  //   //   // } else {
  //   //     const a = document.createElement('a');
  //   //     a.href = url;
  //   //     a.download = 'test';
  //   //     document.body.appendChild(a);
  //   //     a.click();
  //   //     document.body.removeChild(a);
  //   //   // }
  //   //   window.URL.revokeObjectURL(url);
  //   // });
  // }

}
