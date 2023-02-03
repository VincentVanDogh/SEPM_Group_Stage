import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {HomeComponent} from './components/home/home.component';
import {LoginComponent} from './components/login/login.component';
import {AuthGuard} from './guards/auth.guard';
import {MessageComponent} from './components/message/message.component';
import {CreateEventComponent} from './components/create-event/create-event.component';
import {EventComponent} from './components/event/event.component';
import {CreateLocationComponent} from './components/create-location/create-location.component';
import {RegistrationComponent} from './components/registration/registration.component';
import {ShoppingCartComponent} from './components/shopping-cart/shopping-cart.component';
import {CreateUserComponent} from './components/create-user/create-user.component';
import {UsersComponent} from './components/users/users.component';
import {ProfileComponent} from './components/profile/profile.component';
import {EditProfileComponent} from './components/edit-profile/edit-profile.component';
import {EventDetailsComponent} from './components/event-details/event-details.component';
import {TicketGenerateComponent, TicketGenerationMode} from './components/ticket-generate/ticket-generate.component';
import {CreateArtistComponent} from './components/create-artist/create-artist.component';
import {MerchandiseComponent} from './components/merchandise/merchandise.component';
import {MerchandiseArticleComponent} from './components/merchandise-article/merchandise-article.component';
import {ResetPasswordComponent} from './components/reset-password/reset-password.component';
import {SetNewPasswordComponent} from './components/set-new-password/set-new-password.component';
import {MessageDetailsComponent} from './components/message-details/message-details.component';
import {ShoppingCartResolver} from './services/shopping-cart.resolver';

const routes: Routes = [
  {path: '', component: HomeComponent},
  {path: 'login', component: LoginComponent},
  {path: 'merchandise', children: [
      {path: '', component: MerchandiseComponent},
      {path: ':id', component: MerchandiseArticleComponent}
    ]},
  {path: 'message', children: [
      {path: '', component: MessageComponent},
      {path: 'detail/:id', component: MessageDetailsComponent}
    ]},
  {path: 'events', children: [
      {path: '', component: EventComponent},
      {path: 'create', component: CreateEventComponent},
      {path: 'detail/:id', children: [
          {path: '', component: EventDetailsComponent},
          {path: 'tickets/:actId', canActivate: [AuthGuard], component: TicketGenerateComponent,
            data: {mode: TicketGenerationMode.purchase}},
          {path: 'tickets/:actId/buy', canActivate: [AuthGuard], component: TicketGenerateComponent,
            data: {mode: TicketGenerationMode.purchase}},
          {path: 'tickets/:actId/reserve', canActivate: [AuthGuard], component: TicketGenerateComponent,
            data: {mode: TicketGenerationMode.reservation}}
          ]},
    ]},
  {path: 'locations/create', component: CreateLocationComponent},
  {path: 'artists/create', component: CreateArtistComponent},
  {path: 'registration', component: RegistrationComponent},
  {path: 'shopping-cart', canActivate: [AuthGuard], component: ShoppingCartComponent, resolve: {cart: ShoppingCartResolver}},
  {path: 'profile', component: ProfileComponent, canActivate: [AuthGuard]},
  {path: 'edit', component: EditProfileComponent},
  {path: 'users/create', canActivate: [AuthGuard], component: CreateUserComponent},
  {path: 'users', canActivate: [AuthGuard], component: UsersComponent},
  {path: 'reset-password', component: ResetPasswordComponent},
  {path: 'set-new-password/:token', component: SetNewPasswordComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {useHash: true, scrollPositionRestoration: 'enabled'})],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
