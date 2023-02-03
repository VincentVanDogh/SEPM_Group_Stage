import { Component, OnInit } from '@angular/core';
import {UntypedFormBuilder, UntypedFormGroup, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {RegistrationService} from '../../services/registration.service';
import {Registration} from '../../dtos/registration';
import {ToastrService} from 'ngx-toastr';
import {Title} from '@angular/platform-browser';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.scss']
})
export class RegistrationComponent implements OnInit {

  loginForm: UntypedFormGroup;
  // After first submission attempt, form validation will start
  submitted = false;
  // Error flag
  error = false;
  errorMessage = '';
  prevUrl = '/';

  constructor(
    private titleService: Title,
    private formBuilder: UntypedFormBuilder,
    private registrationService: RegistrationService,
    public router: Router,
    private notification: ToastrService
  ) {
    titleService.setTitle('Ticketline | Registration');
    this.loginForm = this.formBuilder.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      confirmation: ['', [Validators.required, Validators.minLength(8)]],
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]]
    });
  }

  /**
   * Form validation will start after the method is called, additionally an AuthRequest will be sent
   */
  registerUser() {
    this.submitted = true;
    if (this.loginForm.valid) {
      const registration: Registration = new Registration(
        this.loginForm.controls.username.value,
        this.loginForm.controls.password.value,
        this.loginForm.controls.confirmation.value,
        this.loginForm.controls.firstName.value,
        this.loginForm.controls.lastName.value );
      this.authenticateUser(registration);
    } else {
      console.error('Invalid input');
    }
  }

  /**
   * Send authentication data to the authService. If the authentication was successfully, the user will be forwarded to the message page
   *
   * @param registration authentication data from the user login form
   */
  authenticateUser(registration: Registration) {
    this.registrationService.registerUser(registration).subscribe({
      next: () => {
        this.notification.success('Successfully registered!');
        this.router.navigate(['/message']);
      },
      error: error => {
        console.error('Could not register due to: ', error);
        if (error.status === 401) {
          this.notification.error('Passwords do not match');
        }
        if (error.status === 422) {
          this.notification.error('Invalid email format');
        }
        if (error.status === 409) {
          this.notification.error('Email is already registered');
        }
      }
    });
  }

  ngOnInit() {
  }

}
