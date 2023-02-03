import { Component, OnInit } from '@angular/core';
import {UntypedFormBuilder, UntypedFormGroup, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {CreateUser} from '../../dtos/create-user';
import {UserService} from '../../services/user.service';
import {AuthService} from '../../services/auth.service';
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-create-user',
  templateUrl: './create-user.component.html',
  styleUrls: ['./create-user.component.scss']
})
export class CreateUserComponent implements OnInit {
  userForm: UntypedFormGroup;
  // After first submission attempt, form validation will start
  submitted = false;
  // Error flag
  error = false;
  errorMessage = '';

  constructor(
    private formBuilder: UntypedFormBuilder,
    private userService: UserService,
    private authService: AuthService,
    private notification: ToastrService,
    private router: Router) {
    this.userForm = this.formBuilder.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      confirmation: ['', [Validators.required, Validators.minLength(8)]],
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]]
    });
  }


  ngOnInit(): void {
    if (!this.isAdmin()) {
      this.router.navigate(['']);
    }
  }


  /**
   * Form validation will start after the method is called, additionally an AuthRequest will be sent
   */
  createUser() {
    const checkbox = document.getElementById(
      'admin',
    ) as HTMLInputElement | null;
    this.submitted = true;
    if (this.userForm.valid) {
      const user: CreateUser = new CreateUser(
        this.userForm.controls.username.value,
        this.userForm.controls.password.value,
        this.userForm.controls.confirmation.value,
        this.userForm.controls.firstName.value,
        this.userForm.controls.lastName.value,
        checkbox?.checked);
      this.userService.createUser(user).subscribe({
        next: () => {
          this.notification.success('Successfully created user: ' + user.email);
          this.router.navigate(['/users']);
        },
        error: error => {
          console.error('Could not create user due to: ' + error);
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
    } else {
      console.error('Invalid input');
    }
  }

  /**
   * Returns true if the authenticated user is an admin
   */
  isAdmin(): boolean {
    return this.authService.getUserRole() === 'ADMIN';
  }
}
