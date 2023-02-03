import { Component, OnInit } from '@angular/core';
import {UntypedFormBuilder, UntypedFormGroup, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {ProfileService} from '../../services/profile.service';
import {Registration} from '../../dtos/registration';
import {Password} from '../../dtos/password';
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-edit-profile',
  templateUrl: './edit-profile.component.html',
  styleUrls: ['./edit-profile.component.scss']
})
export class EditProfileComponent implements OnInit {


  profileForm: UntypedFormGroup;
  // After first submission attempt, form validation will start
  submitted = false;
  submittedPassword = false;
  // Error flag
  error = false;
  errorMessage = '';

  profile: Registration = {
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    confirmation: ''
  };
  passwordForm: UntypedFormGroup;

  constructor(
    private formBuilder: UntypedFormBuilder,
    private profileService: ProfileService,
    private router: Router,
    private service: ProfileService,
    private notification: ToastrService
  ) {
    this.profileForm = this.formBuilder.group({
      username: [''],
      firstName: [''],
      lastName: [''],
      passwordProfile: ['', [Validators.required, Validators.minLength(8)]],
    });
    this.passwordForm = this.formBuilder.group({
      newPassword: ['', [Validators.required, Validators.minLength(8)]],
      confirmation: ['', [Validators.required, Validators.minLength(8)]],
      currPassword: ['', [Validators.required, Validators.minLength(8)]],
    });
  }

  /**
   * Form validation will start after the method is called, additionally the request to edit the profile according to
   * the data in the form will be called.
   */
  editUser() {
    this.submitted = true;
    let firstName;
    let email;
    let lastName;
    if (this.profileForm.valid) {
      this.ngOnInit();
      if (!this.profileForm.controls.username.value) {
        email = this.getEmail();
      } else {
        email = this.profileForm.controls.username.value;
      }
      if (!this.profileForm.controls.firstName.value) {
        firstName = this.getFirstName();
      } else {
        firstName = this.profileForm.controls.firstName.value;
      }
      if (!this.profileForm.controls.lastName.value) {
        lastName = this.getLastName();
      } else {
        lastName = this.profileForm.controls.lastName.value;
      }

      const profile: Registration = new Registration(
        email,
        this.profileForm.controls.passwordProfile.value,
        null,
        firstName,
        lastName);
      this.profileService.editUser(profile).subscribe({
        next: () => {
          this.notification.success('Saved all changes');
          this.router.navigate(['/profile']);
        },
        error: error => {
          console.error('Could not edit due to: ' + error);
          this.notification.error(error.error.message);
        }
      });
    } else {
      console.error('Invalid input');
    }
  }

  ngOnInit(): void {
    this.service.getUser().subscribe(data => {
      this.profile.firstName = data.firstName;
      this.profile.lastName = data.lastName;
      this.profile.email = data.email;
    },);
  }

  delete(): void {
    this.service.deleteUser();
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

  editPassword(): void {
    this.submittedPassword = true;
    if (this.passwordForm.valid) {
      const password: Password = new Password(
        this.passwordForm.controls.currPassword.value,
        this.passwordForm.controls.newPassword.value,
        this.passwordForm.controls.confirmation.value,
      );
      this.profileService.editPassword(password).subscribe({
        next: () => {
          this.notification.success('Password changed');
          this.router.navigate(['/profile']);
        },
        error: error => {
          console.error('Could not edit due to: ' + error);
          this.notification.error(error.error.message);
        }
      });
    } else {
      console.error('Invalid input');
    }
  }
}
