import { Component, OnInit } from '@angular/core';
import {UntypedFormBuilder, UntypedFormGroup, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';
import {UserService} from '../../services/user.service';

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.scss']
})
export class ResetPasswordComponent implements OnInit {

  resetForm: UntypedFormGroup;
  submitted = false;
  error = false;
  errorMessage = '';

  constructor(
    private formBuilder: UntypedFormBuilder,
    private userService: UserService,
    private router: Router,
    private notification: ToastrService
  ) {
    this.resetForm = this.formBuilder.group({
      username: ['', [Validators.required]],
    });
  }

  /**
   * Form validation will start after the method is called
   */
  resetPassword() {
    this.submitted = true;
    if (this.resetForm.valid) {
      this.userService.initiateResetPassword(this.resetForm.controls.username.value).subscribe(response => {
        this.notification.success('An email with a link to reset your password hase been sent to: '
          + this.resetForm.controls.username.value);
      }, error => {
        console.error(error);
        if (error.status === 404) {
          this.notification.error('No account with this email');
        }
      });
    } else {
      console.error('Invalid input');
    }
  }

  ngOnInit(): void {
  }

}
