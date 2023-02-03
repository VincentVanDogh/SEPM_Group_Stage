import { Component, OnInit } from '@angular/core';
import {UntypedFormBuilder, UntypedFormGroup, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {UserService} from '../../services/user.service';
import {ToastrService} from 'ngx-toastr';
import {NewPassword} from '../../dtos/new-password';

@Component({
  selector: 'app-set-new-password',
  templateUrl: './set-new-password.component.html',
  styleUrls: ['./set-new-password.component.scss']
})
export class SetNewPasswordComponent implements OnInit {
  passwordForm: UntypedFormGroup;
  submitted = false;
  token: string;

  constructor(
    private route: ActivatedRoute,
    private formBuilder: UntypedFormBuilder,
    private userService: UserService,
    private router: Router,
    private notification: ToastrService) {
    this.token = this.route.snapshot.paramMap.get('token');
    this.passwordForm = this.formBuilder.group({
      password: ['', [Validators.required]],
      confirmation: ['', [Validators.required]],
    });
  }

  ngOnInit(): void {
  }

  /**
   * Form validation will start after the method is called
   */
  setNewPassword() {
    this.submitted = true;
    if (this.passwordForm.valid) {
      const newPassword: NewPassword = new NewPassword(
        this.passwordForm.controls.password.value,
        this.passwordForm.controls.confirmation.value,
        this.token
      );
      this.userService.setNewPassword(newPassword).subscribe(response => {
        this.notification.success('Your password has successfully been set.');
        this.router.navigate(['/login']);
      }, error => {
        console.error(error);
        if (error.status === 422) {
          this.notification.error('Link is no longer valid. Please request a new one.');
        }
        if (error.status === 404) {
          this.notification.error('Link is not valid. Please request a new one.');
        }
        if (error.status === 403) {
          this.notification.error('Passwords do not match.');
        }
      });
    } else {
      console.error('Invalid input');
    }
  }

}
