import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {FormControl, UntypedFormBuilder, Validators} from '@angular/forms';
import {ToastrService} from 'ngx-toastr';
import {ArtistService} from '../../services/artist.service';
import {Artist} from '../../dtos/artist';
import {AuthService} from '../../services/auth.service';

@Component({
  selector: 'app-create-artist',
  templateUrl: './create-artist.component.html',
  styleUrls: ['./create-artist.component.scss']
})
export class CreateArtistComponent implements OnInit {

  artistForm = this.formBuilder.group({
    firstName: ['', Validators.maxLength(255)],
    lastName: ['', Validators.maxLength(255)],
    bandName: ['', Validators.maxLength(255)],
    stageName: ['', Validators.compose([Validators.required, Validators.maxLength(255)])],
  });

  errorMessage = '';

  constructor(
    private artistService: ArtistService,
    private router: Router,
    private route: ActivatedRoute,
    private formBuilder: UntypedFormBuilder,
    private notification: ToastrService,
    private authService: AuthService
  ) {
  }

  get firstName() {
    return this.artistForm.get('firstName') as FormControl;
  }

  get lastName() {
    return this.artistForm.get('lastName') as FormControl;
  }

  get bandName() {
    return this.artistForm.get('bandName') as FormControl;
  }

  get stageName() {
    return this.artistForm.get('stageName') as FormControl;
  }

  ngOnInit(): void {
    if (!this.isAdmin()) {
      this.router.navigate(['']);
    }
  }

  public onSubmit(): void {
    const observable = this.artistService.createArtist(this.artistForm.value as Artist);
    observable.subscribe({
      next: data => {
        this.notification.success('Artist was successfully created');
        this.router.navigate(['/events']);
      },
      error: error => {
        console.error('Error creating artist: ' + error);

        if (error.status === 400) {
          const msg: string = error.error;

          if (msg.includes('first') && msg.includes('255')) {
            this.notification.error('Venue name must not be longer than 255 characters');
          }

          if (msg.includes('last') && msg.includes('255')) {
            this.notification.error('Street name must not be longer than 255 characters');
          }

          if (msg.includes('band') && msg.includes('255')) {
            this.notification.error('City name must not be longer than 255 characters');
          }

          if (msg.includes('stage') && msg.includes('255')) {
            this.notification.error('Country name must not be longer than 255 characters');
          }

          if (msg.includes('stage') && msg.includes('255')) {
            this.notification.error('Stage name must not be longer than 255 characters');
          }

          if (msg.includes('postal')) {
            this.notification.error('Postal code must be a number between 100 and 999999999');
          }
        } else {
          this.notification.error('Error creating location');
        }
      }
    });
  }

  /**
   * Returns true if the authenticated user is an admin
   */
  isAdmin(): boolean {
    return this.authService.getUserRole() === 'ADMIN';
  }
}
