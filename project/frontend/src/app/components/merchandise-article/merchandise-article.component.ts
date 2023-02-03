import { Component, OnInit } from '@angular/core';
import {MerchandiseService} from '../../services/merchandise.service';
import {ToastrService} from 'ngx-toastr';
import {MerchArticleQuantity} from '../../dtos/merchArticle';
import {ActivatedRoute, Router} from '@angular/router';
import {ProfileService} from '../../services/profile.service';
import {MerchCartArticle} from '../../dtos/merchPurchase';
import {MerchPurchaseService} from '../../services/merch-purchase.service';
import {AuthService} from '../../services/auth.service';
import {Title} from '@angular/platform-browser';

@Component({
  selector: 'app-merchandise-article',
  templateUrl: './merchandise-article.component.html',
  styleUrls: ['./merchandise-article.component.scss']
})
export class MerchandiseArticleComponent implements OnInit {
  merchArticle: MerchCartArticle = {
    articleNr: 0,
    name: '',
    price: 0,
    bonusPointPrice: 0,
    image: '',
    articleCount: 0,
    bonusUsed: false,
    artistOrEventName: ''
  };
  bonusUsedDefault = false;
  quantity = 1;
  bonusPointBalance = 0;
  quantityArr = [1,2,3,4,5,6,7,8,9,10];

  constructor(
    private titleService: Title,
    private service: MerchandiseService,
    private merchPurchaseService: MerchPurchaseService,
    private profileService: ProfileService,
    private notification: ToastrService,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    public authService: AuthService,
  ) { }

  ngOnInit(): void {
    this.reloadMerchArticle();
    this.profileService.getBonusPointsInCart().subscribe((points: number) => {
      this.bonusPointBalance = points;
    });
  }

  public checkInputRadio(): void {
    if (this.merchArticle.bonusPointPrice * this.quantity > this.bonusPointBalance) {
      this.merchArticle.bonusUsed = false;
    }
  }

  public computePrice(): string {
    return !this.merchArticle.bonusUsed ?
      `€ ${(this.quantity * this.merchArticle.price / 100).toFixed(2)}` :
      `${this.quantity * this.merchArticle.bonusPointPrice} Points`;
  }

  public getArticleName(): string {
    return `${this.merchArticle.artistOrEventName} ${this.merchArticle.name}`;
  }

  public addToCart(): void {
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login']);
      return;
    }
    const merchQuantity: MerchArticleQuantity = {
      articleNr: this.merchArticle.articleNr,
      quantity: this.quantity,
      bonusUsed: this.merchArticle.bonusUsed};
    this.service.addMerchArticleToCart(merchQuantity).subscribe({
        next: () => {
        this.notification.success(`Stored ${this.quantity} ${this.merchArticle.name}
          article${this.quantity > 1 ? 's' : ''} in your shopping cart`
        );
      },
      error: err => {
          console.error(err);
          this.notification.error(`Could not add merch article${this.quantity > 1 ? 's' : ''} to the shopping cart`, err);
      }
    });
  }

  public deleteMerch(merchId: number): void {
    this.merchPurchaseService.deleteMerchArticle(merchId).subscribe({
      next: () => {
        this.notification.success('Merch deleted from shopping cart');
        this.router.navigate(['/shopping-cart']);

      }
    });
  }

  /**
   * Returns true if the authenticated user is an admin
   */
  public isAdmin(): boolean {
    return this.authService.getUserRole() === 'ADMIN';
  }

  private reloadMerchArticle(): void {
    const id: number = parseInt(this.activatedRoute.snapshot.paramMap.get('id'), 10);
    this.service.getMerchArticle(id).subscribe({
      next: data => {
        this.merchArticle = data;
        this.titleService.setTitle(`Merch | ${data.artistOrEventName} ${data.name}`);
        if (data.articleCount !== 0) {
          this.quantity = data.articleCount;
        }
        this.bonusUsedDefault = data.bonusUsed;
      },
      error: err => {
        console.error(err);
        if (err.status === 403) {
          this.notification.error('Access not permitted: ', err);
        }
        this.notification.error('Error fetching merchandise articles: ', err);
      }
    });
  }

}
