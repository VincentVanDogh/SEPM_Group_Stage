import { Component, OnInit } from '@angular/core';
import {MerchPurchaseService} from '../../../services/merch-purchase.service';
import {MerchCartArticle, MerchPurchase} from '../../../dtos/merchPurchase';
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-merch-purchases',
  templateUrl: './merch-purchases.component.html',
  styleUrls: ['./merch-purchases.component.scss']
})
export class MerchPurchasesComponent implements OnInit {
  merchArticles: MerchCartArticle[] = [];

  constructor(
    private service: MerchPurchaseService,
    private notification: ToastrService
  ) { }

  ngOnInit(): void {
    this.loadPurchases();
  }

  public getPrice(article: MerchCartArticle): string {
    return article.bonusUsed ? `${article.bonusPointPrice} Points` : (article.articleCount * article.price / 100).toFixed(2);
  }

  public getName(article: MerchCartArticle): string {
    return `${article.artistOrEventName} ${article.name}`;
  }

  private loadPurchases(): void {
    this.service.getCompletedPurchases().subscribe({
      next: data => {
        data.forEach(merch => {
          merch.articles.forEach(articles => {
            this.merchArticles.push(articles);
          });
        });
      },
      error: err => {
        console.error(err);
        this.notification.error('Error fetching purchases', err);
      }
    });
  }

}
