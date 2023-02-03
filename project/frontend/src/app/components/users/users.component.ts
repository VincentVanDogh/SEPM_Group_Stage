import { Component, OnInit } from '@angular/core';
import {User} from '../../dtos/user';
import {UserService} from '../../services/user.service';
import {ToastrService} from 'ngx-toastr';
import {UntypedFormBuilder, UntypedFormGroup} from '@angular/forms';
import {AuthService} from '../../services/auth.service';
import {ProfileService} from '../../services/profile.service';
import {Emails} from '../../dtos/emails';
import {Router} from '@angular/router';

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.scss']
})
export class UsersComponent implements OnInit {
  search = false;
  users: User[] = [];
  error = false;
  errorMessage = '';
  searchForm: UntypedFormGroup;
  emails: Emails = {
    email: 'test',
    currentEmail: 'test',
  };
  currentPageId = 1;
  pageNumbers = [];

  constructor(
    private userService: UserService,
    private authService: AuthService,
    private profileService: ProfileService,
    private notification: ToastrService,
    private formBuilder: UntypedFormBuilder,
    private router: Router
  ) {
    this.searchForm = this.formBuilder.group({
      email: [''],
      firstName: [''],
      lastName: [''],
      lockedOut:[]
    });
  }

  ngOnInit(): void {
    if (!this.isAdmin()) {
      this.router.navigate(['']);
    } else {
      this.profileService.getUser().subscribe(data => {
        this.emails.currentEmail = data.email;
      },);
      this.reloadUsers();
    }
  }

  reloadUsers() {
    this.userService.getAll(this.currentPageId)
      .subscribe({
        next: data => {
          this.users = data.users;
          this.pageNumbers = Array(data.numberOfPages).fill(0);
          if (this.users.length === 0 && this.currentPageId !== 1 && data.numberOfPages >= 1) {
            this.emptyPage();
          }
          if (this.users.length === 0 && data.numberOfPages < 1) {
            this.notification.error('No Users found.');
          }
        },
        error: error => {
          console.error('Error fetching users: ', error);
          const errorMessage = error.status === 0
            ? 'Is the backend up?'
            : error.message.message;
          this.notification.error(errorMessage, 'Could Not Fetch Users');
        }
      });
  }

  changeLockedStatus(email: string, lock: boolean) {
    this.emails.email = email;
    return this.userService.changeLockedStatus(this.emails).subscribe({
      next: () => {
        if (lock) {
          this.notification.success( 'Successfully locked user: ' + email);
        } else {
          this.notification.success( 'Successfully unlocked user: ' + email);
        }
        this.reloadUsers();
      },
      error: error => {
        console.error('Could not toggle locked status: ' + error);
        this.notification.error(error.error.message);
        this.error = true;
      }
    });
  }

  searchUsers() {
    if (this.searchForm.valid) {
      let lockedOut = null;
      if (this.searchForm.controls.lockedOut.value > 0) {
        lockedOut = true;
      }
      if (this.searchForm.controls.lockedOut.value < 0) {
        lockedOut = false;
      }
      const user: User = new User(
        this.searchForm.controls.email.value,
        this.searchForm.controls.firstName.value,
        this.searchForm.controls.lastName.value,
        lockedOut,);
      if (lockedOut == null) {
        this.userService.searchWithoutLockedOut(user, this.currentPageId)
          .subscribe({
            next: data => {
              this.users = data.users;
              this.pageNumbers = Array(data.numberOfPages).fill(0);
              if (this.users.length === 0 && this.currentPageId !== 1 && data.numberOfPages >= 1) {
                this.emptyPage();
              }
            },
            error: error => {
              console.error('Error fetching users: ', error);
              const errorMessage = error.status === 0
                ? 'Is the backend up?'
                : error.message.message;
              this.notification.error(errorMessage, 'Could Not Fetch Users');
            }
          });
      } else {
        this.userService.search(user, this.currentPageId)
          .subscribe({
            next: data => {
              this.users = data.users;
              this.pageNumbers = Array(data.numberOfPages).fill(0);
              if (this.users.length === 0 && this.currentPageId !== 1 && data.numberOfPages >= 1) {
                this.emptyPage();
              }
              if (this.users.length === 0 && data.numberOfPages < 1) {
                this.notification.error('No matching search results found.');
              }
            },
            error: error => {
              console.error('Error fetching users: ', error);
              const errorMessage = error.status === 0
                ? 'Is the backend up?'
                : error.message.message;
              this.notification.error(errorMessage, 'Could Not Fetch Users');
            }
          });
        }
    }
  }

  /**
   * Returns true if the authenticated user is an admin
   */
  isAdmin(): boolean {
    return this.authService.getUserRole() === 'ADMIN';
  }

  lockUser(email: string) {
    this.changeLockedStatus(email, true);
  }

  unlockUser(email: string) {
    this.changeLockedStatus(email, false);
  }

  resetPassword(email: string) {
    this.userService.initiateResetPassword(email).subscribe(() => {
      this.notification.success('An email with a link to reset the password hase been sent to: '
        + email);
    }, error => {
      this.notification.error(error.error.message);
    });
  }

  showPage(id: number): void {
    this.currentPageId = id;
    this.searchUsers();
    scroll(0,0);
    this.router.navigate(['users'], {queryParams: {page: this.currentPageId}});
  }

  showNextPage(): void {
    this.currentPageId++;
    this.searchUsers();
    scroll(0,0);
    this.router.navigate(['users'], {queryParams: {page: this.currentPageId}});
  }

  showPrevPage(): void {
    this.currentPageId--;
    this.searchUsers();
    scroll(0,0);
    this.router.navigate(['users'], {queryParams: {page: this.currentPageId}});
  }

  emptyPage(){
    this.currentPageId = 1;
    this.searchUsers();
    this.notification.info('New search parameters have been applied. Directing to page 1 of search results');
    this.router.navigate(['users'], {queryParams: {page: this.currentPageId}});
  }

  showNumberPagination(i: number): boolean {

    if (this.pageNumbers.length > 0 && this.pageNumbers.length < 6) {
      return this.currentPageId !== i;
    } else {
      if (Math.abs(this.currentPageId - i) > 0 && Math.abs(this.currentPageId - i) < 3) {
        return true;
      }
      if ((this.currentPageId > 3 && i === 1) || (this.currentPageId === 5 && i === 2)) {
        return true;
      }
      if (this.currentPageId < this.pageNumbers.length - 2 && i === this.pageNumbers.length) {
        return true;
      }
      if (this.currentPageId === this.pageNumbers.length - 4 && i === this.pageNumbers.length - 1) {
        return true;
      }
    }
    return false;
  }

  showDotsPagination(i: number): boolean {

    if (this.pageNumbers.length > 5) {
      if (Math.abs(this.currentPageId - i) === 3 && i > 2 && i < this.pageNumbers.length - 1) {
        return true;
      }
    }
    return false;
  }
}
