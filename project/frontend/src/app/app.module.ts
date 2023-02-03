import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {HeaderComponent} from './components/header/header.component';
import {FooterComponent} from './components/footer/footer.component';
import {HomeComponent} from './components/home/home.component';
import {LoginComponent} from './components/login/login.component';
import {MessageComponent} from './components/message/message.component';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {httpInterceptorProviders} from './interceptors';
import {StagePlanComponent} from './components/stage-plan/stage-plan.component';
import { StagePlanSingleGenericComponent } from './components/stage-plan/stage-plan-single-generic/stage-plan-single-generic.component';
import { StagePlanSingleSpecificComponent } from './components/stage-plan/stage-plan-single-specific/stage-plan-single-specific.component';
import { CreateEventComponent } from './components/create-event/create-event.component';
import { EventComponent } from './components/event/event.component';
import { CreateLocationComponent } from './components/create-location/create-location.component';
import {ToastrModule} from 'ngx-toastr';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import { ShoppingCartComponent } from './components/shopping-cart/shopping-cart.component';
import { CheckoutComponent } from './components/shopping-cart/checkout/checkout.component';
import { PurchasesComponent } from './components/profile/purchases/purchases.component';
import {NgxMaskModule} from 'ngx-mask';
import {RegistrationComponent} from './components/registration/registration.component';
import { CreateUserComponent } from './components/create-user/create-user.component';
import { UsersComponent } from './components/users/users.component';
import { ProfileComponent } from './components/profile/profile.component';
import { EditProfileComponent } from './components/edit-profile/edit-profile.component';
import { EventDetailsComponent } from './components/event-details/event-details.component';
import { CreateArtistComponent } from './components/create-artist/create-artist.component';
import { StagePlanSinglePriceConfigComponent } from
    './components/stage-plan/stage-plan-single-price-config/stage-plan-single-price-config.component';
import { TicketGenerateComponent } from './components/ticket-generate/ticket-generate.component';
import { TopTenEventsComponent } from './components/top-ten-events/top-ten-events.component';
import { MerchandiseComponent } from './components/merchandise/merchandise.component';
import { MerchandiseArticleComponent } from './components/merchandise-article/merchandise-article.component';
import { MerchPurchasesComponent } from './components/profile/merch-purchases/merch-purchases.component';
import { ResetPasswordComponent } from './components/reset-password/reset-password.component';
import { SetNewPasswordComponent } from './components/set-new-password/set-new-password.component';
import { OrdersComponent } from './components/profile/orders/orders.component';
import { MessageDetailsComponent } from './components/message-details/message-details.component';
import { ReservationsComponent } from './components/profile/reservations/reservations.component';
import {ShoppingCartResolver} from './services/shopping-cart.resolver';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    FooterComponent,
    HomeComponent,
    LoginComponent,
    MessageComponent,
    StagePlanComponent,
    StagePlanSingleGenericComponent,
    StagePlanSingleSpecificComponent,
    CreateEventComponent,
    EventComponent,
    CreateLocationComponent,
    RegistrationComponent,
    ShoppingCartComponent,
    CheckoutComponent,
    PurchasesComponent,
    ProfileComponent,
    EditProfileComponent,
    CreateUserComponent,
    UsersComponent,
    EventDetailsComponent,
    CreateArtistComponent,
    StagePlanSinglePriceConfigComponent,
    TicketGenerateComponent,
    MerchandiseComponent,
    MerchandiseArticleComponent,
    MerchPurchasesComponent,
    ResetPasswordComponent,
    SetNewPasswordComponent,
    OrdersComponent,
    TopTenEventsComponent,
    ReservationsComponent,
    MessageDetailsComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    HttpClientModule,
    NgbModule,
    FormsModule,
    ToastrModule.forRoot(),
    // Needed for Toastr
    BrowserAnimationsModule,
    NgxMaskModule.forRoot(),
  ],
  providers: [httpInterceptorProviders, ShoppingCartResolver],
  bootstrap: [AppComponent]
})
export class AppModule {
}
