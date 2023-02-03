import { Component, OnInit } from '@angular/core';
import {ProfileService} from '../../services/profile.service';
import {Registration} from '../../dtos/registration';
import {ToastrService} from 'ngx-toastr';
import {ActivatedRoute, Router} from '@angular/router';
import {AuthService} from '../../services/auth.service';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {Title} from '@angular/platform-browser';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit {

  profile: Registration = {
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    confirmation: ''
  };

  bonusPointBalance = 0;

  constructor(
    private titleService: Title,
    private service: ProfileService,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
    private modalService: NgbModal,
  ) {
    titleService.setTitle('Profile');
  }

  ngOnInit(): void {
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['login']);
    } else {
      this.service.getUser().subscribe(data => {
        this.profile.firstName = data.firstName;
        this.profile.lastName = data.lastName;
        this.profile.email = data.email;
      },);
      this.service.getBonusPoints().subscribe(data => {
        this.bonusPointBalance = data;
      });
    }
  }

  delete(): void {
      this.service.deleteUser().subscribe({
        next: () => {
          this.notification.success(`Account ${this.profile.email} successfully deleted`);
          this.router.navigate(['/registration']);
        },
        error: error => {
          console.error('Error deleting account: ', error);
          this.notification.error(error.error.error, error.error.message);
        },
      });
      this.authService.logoutUser();
  }


  getFirstName(): string {
    return this.profile.firstName;
  }

  getLastName(): string {
    return this.profile.lastName;
  }

  getEmail(): string {
    return this.profile.email;
  }

  public openModal(profileDeleteWindow) {
    this.modalService.open(profileDeleteWindow, {backdrop: 'static',size: 'lg'});
  }

}
