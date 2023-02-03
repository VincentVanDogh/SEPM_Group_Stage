import { Component, OnInit } from '@angular/core';
import {MerchArticleService} from '../../services/merch-article.service';
import {MerchArticle, MerchArticleSearch, MerchPage} from '../../dtos/merchArticle';
import {ToastrService} from 'ngx-toastr';
import {ActivatedRoute, Router} from '@angular/router';
import {Title} from '@angular/platform-browser';

@Component({
  selector: 'app-merchandise',
  templateUrl: './merchandise.component.html',
  styleUrls: ['./merchandise.component.scss']
})
export class MerchandiseComponent implements OnInit {
  merchArticles: MerchArticle[] = [];
  merchArticlesSearch: MerchArticleSearch = {
    pointPurchaseAvailable: false
  };
  hidden = false;
  currentPageId = 1;
  pageNumbers = [];

  constructor(
    private titleService: Title,
    private service: MerchArticleService,
    private notification: ToastrService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    titleService.setTitle('Ticketline | Merch');
  }

  ngOnInit(): void {
    const pageId = this.route.snapshot.queryParamMap.get('page');
    if (pageId) {
      this.currentPageId = Number.parseInt(pageId, 10);
    }
    this.reloadMerchandise();
  }

  showPage(id: number): void {
    this.currentPageId = id;
    this.reloadMerchandise();
    scroll(0,0);
    this.router.navigate(['merchandise'], {queryParams: {page: this.currentPageId}});
  }

  showNextPage(): void {
    this.currentPageId++;
    this.reloadMerchandise();
    scroll(0,0);
    this.router.navigate(['merchandise'], {queryParams: {page: this.currentPageId}});
  }

  showPrevPage(): void {
    this.currentPageId--;
    this.reloadMerchandise();
    scroll(0,0);
    this.router.navigate(['merchandise'], {queryParams: {page: this.currentPageId}});
  }

  emptyPage(){
    this.currentPageId = 1;
    this.reloadMerchandise();
    this.notification.info('New search parameters have been applied. Directing to page 1 of search results');
    this.router.navigate(['merchandise'], {queryParams: {page: this.currentPageId}});
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

  startSearch(): void {
    this.currentPageId = 1;
    this.reloadMerchandise();
    this.router.navigate(['merchandise'], {queryParams: {page: 1}});
  }

  public reloadMerchandise(): void {
    this.service.getAllMerchandise(this.merchArticlesSearch, this.currentPageId).subscribe({
      next: (merchPage: MerchPage) => {
        this.merchArticles = merchPage.merchArticles;
        this.pageNumbers = Array(merchPage.numberOfPages).fill(0);
        if (this.merchArticles.length === 0 && this.currentPageId !== 1 && merchPage.numberOfPages >= 1) {
          this.emptyPage();
        }
      },
      error: err => {
        console.error(err);
        this.notification.error('Error fetching merchandise articles: ', err);
      }
    });
  }

  public clearFilters() {
    this.merchArticlesSearch = {
      pointPurchaseAvailable: false
    };
  }

  public changeFilterVisibility() {
    this.hidden = !this.hidden;
  }
}
