import {Component, HostListener, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {ToastrService} from 'ngx-toastr';
import {SharedService} from '../../services/shared.service';
import {Subscription} from 'rxjs';
import {ShoppingCartService} from '../../services/shopping-cart.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {
  cartTicketSize = 0;
  cartBonusPoints = 0;
  phoneMode = false;
  phoneNavExpanded = false;
  private subscription: Subscription;

  constructor(
    public authService: AuthService,
    public service: ShoppingCartService,
    private notification: ToastrService,
    private sharedService: SharedService,
    public router: Router
  ) {
    this.subscription = this.sharedService.getCartStatus().subscribe({
      next: data => {
        this.cartTicketSize = data.items;
        this.cartBonusPoints = data.points;
      }
    });
  }

  @HostListener('window:resize', ['$event'])
  onResize(event): void {
    if (event.target.innerWidth < 750) {
      this.phoneMode = true;
    } else {
      this.phoneMode = false;
      this.phoneNavExpanded = false;
    }
  }

  ngOnInit() {
    this.phoneMode = window.innerWidth < 750;
    if (this.authService.isLoggedIn()) {
      this.loadShoppingCart();
    }
  }

  /**
   * Returns true if the authenticated user is an admin
   */
  isAdmin(): boolean {
    return this.authService.getUserRole() === 'ADMIN';
  }

  private loadShoppingCart(): void {
    this.service.getShoppingCart().subscribe({
      next: cartItems => {
        this.cartTicketSize = cartItems.size;
        this.cartBonusPoints = cartItems.bonusPoints;
        this.sharedService.setCartStatus(cartItems.size, cartItems.bonusPoints);
      },
      error: err => {
        console.error('Error fetching tickets', err);
        if (err.status === 0){
          this.notification.error('Is the backend up?');
        } else {
          this.notification.error('Error fetching the shopping cart');
        }
      }
    });
  }
}
