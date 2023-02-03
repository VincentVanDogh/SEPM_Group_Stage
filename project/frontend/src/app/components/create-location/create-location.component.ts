import {Component, OnInit} from '@angular/core';
import {FormArray, UntypedFormBuilder, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {LocationService} from '../../services/location.service';
import {CreateLocation} from '../../dtos/location';
import {ToastrService} from 'ngx-toastr';
import {StagePlan} from '../../dtos/stage-plan';
import {StagePlanService} from '../../services/stage-plan.service';
import {AuthService} from '../../services/auth.service';

@Component({
  selector: 'app-create-location',
  templateUrl: './create-location.component.html',
  styleUrls: ['./create-location.component.scss']
})
export class CreateLocationComponent implements OnInit {

  locationForm = this.formBuilder.group({
    venueName: ['', Validators.required],
    street: ['', Validators.required],
    city: ['', Validators.required],
    country: ['', Validators.required],
    postalCode: ['', Validators.compose([Validators.min(1000), Validators.max(9999)])],
    stages: this.formBuilder.array([
      this.formBuilder.group({
        stageTemplateId: ['', Validators.required],
        name: ['', Validators.required],
      })
    ])
  });
  stagePlans: StagePlan[] = [];

  errorMessage = '';

  constructor(
    private locationService: LocationService,
    private stagePlanService: StagePlanService,
    private router: Router,
    private route: ActivatedRoute,
    private formBuilder: UntypedFormBuilder,
    private notification: ToastrService,
    private authService: AuthService
  ) {
  }

  get venueName() {
    return this.locationForm.get('venueName') as FormArray;
  }

  get stages() {
    return this.locationForm.get('stages') as FormArray;
  }

  get street() {
    return this.locationForm.get('street');
  }

  get city() {
    return this.locationForm.get('city');
  }

  get country() {
    return this.locationForm.get('country');
  }

  get postalCode() {
    return this.locationForm.get('postalCode');
  }

  addStage() {
    this.stages.push(this.formBuilder.group({
        stageTemplateId: ['', Validators.required],
        name: ['', Validators.required],
      })
    );
  }

  removeStage(index: number) {
    this.stages.removeAt(index);
  }

  ngOnInit(): void {
    if (!this.isAdmin()) {
      this.router.navigate(['']);
    } else {
      this.findStagePlans();
    }
  }

  findStagePlans(): void {
    this.stagePlanService.getStagePlanTemplates().subscribe({
      next: data => {
        this.stagePlans = data;
      },
      error: error => {
        console.error('Error finding stage-plans', error);
        this.notification.error('Could not fetch stage-plans');
      }
    });
  }

  public onSubmit(): void {
    const observable = this.locationService.createLocation(this.locationForm.value as CreateLocation);
    observable.subscribe({
      next: () => {
        this.router.navigate(['/events']);
        this.notification.success('Location successfully created');
      },
      error: error => {
        console.error('Error creating location: ' + error);
        if (error.status === 400) {
          const msg: string = error.error;

          if (msg.includes('venue') && msg.includes('255')) {
            this.notification.error('Venue name must not be longer than 255 characters');
          }

          if (msg.includes('street') && msg.includes('255')) {
            this.notification.error('Street name must not be longer than 255 characters');
          }

          if (msg.includes('city') && msg.includes('255')) {
            this.notification.error('City name must not be longer than 255 characters');
          }

          if (msg.includes('country') && msg.includes('255')) {
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
