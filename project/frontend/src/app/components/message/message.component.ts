import {ChangeDetectorRef, Component, OnInit, TemplateRef} from '@angular/core';
import {MessageService} from '../../services/message.service';
import {Message} from '../../dtos/message';
import {NgbModal, NgbPaginationConfig} from '@ng-bootstrap/ng-bootstrap';
import {UntypedFormBuilder} from '@angular/forms';
import {AuthService} from '../../services/auth.service';
import {ToastrService} from 'ngx-toastr';
import {Router} from '@angular/router';
import {Title} from '@angular/platform-browser';

@Component({
  selector: 'app-message',
  templateUrl: './message.component.html',
  styleUrls: ['./message.component.scss']
})
export class MessageComponent implements OnInit {

  error = false;
  errorMessage = '';
  // After first submission attempt, form validation will start
  submitted = false;
  displayAll = false;

  currentMessage: Message;
  noOfMessageToLoad = 10;
  deleteId: number;

  private allMessages: Message[];
  private unreadMessages?: Message[] = [];
  constructor(
    private titleService: Title,
    private messageService: MessageService,
    private ngbPaginationConfig: NgbPaginationConfig,
    private formBuilder: UntypedFormBuilder,
    private cd: ChangeDetectorRef,
    public authService: AuthService,
    private modalService: NgbModal,
    private notification: ToastrService,
    private router: Router
  ) {
    titleService.setTitle('Ticketline | News');
  }

  ngOnInit() {
    this.loadMessage();
    if (!this.authService.isLoggedIn()) {
      this.displayAll = true;
    }
  }

  /**
   * Returns true if the authenticated user is an admin
   */
  isAdmin(): boolean {
    return this.authService.getUserRole() === 'ADMIN';
  }

  openAddModal(messageAddModal: TemplateRef<any>) {
    this.currentMessage = new Message();
    this.modalService.open(messageAddModal, {ariaLabelledBy: 'modal-basic-title'});
  }

  /**
   * Starts form validation and builds a message dto for sending a creation request if the form is valid.
   * If the procedure was successful, the form will be cleared.
   */
  addMessage(form) {
    this.submitted = true;


    if (form.valid) {
      this.currentMessage.publishedAt = new Date();
      this.createMessage(this.currentMessage);
      this.clearForm();
    }
  }

  getMessage(): Message[] {
    return this.displayAll ? this.allMessages : this.unreadMessages;
  }

  /**
   * Error flag will be deactivated, which clears the error message
   */
  vanishError() {
    this.error = false;
  }

  markNewsAsRead(id: number) {
    this.messageService.markAsRead(id).subscribe({
      next: () => {
        this.notification.success('News marked as read');
        this.loadMessage();
      },
      error: err => {
        console.error(err);
        this.notification.error('Error trying to mark news as read');
      }
    });
  }

  markNewsAsUnread(id: number) {
    this.messageService.markAsUnread(id).subscribe({
      next: () => {
        this.notification.success('News marked as unread');
        this.loadMessage();
      },
      error: err => {
        console.error(err);
        this.notification.error('Error trying to mark news as unread');
      }
    });
  }

  showAllNews(bool: boolean) {
    this.displayAll = bool;
  }

  isNotRead(message: Message): boolean {
    if (!this.authService.isLoggedIn()) {
      return true;
    }
    let ret = false;
    this.unreadMessages.forEach((element) => {
      if (element.id === message.id) {
        ret = true;
      }
    });
    return ret;
  }

  deleteMessage(id: number) {
    this.messageService.deleteMessage(id).subscribe({
      next: () => {
        this.notification.success(`News successfully deleted`);
        this.loadMessage();
      },
      error: error => {
        console.error('Error deleting news: ', error);
        this.notification.error('News could not be deleted');
      },
    });
  }

  showDetails(id: number) {
    if (this.authService.isLoggedIn()) {
      this.markNewsAsRead(id);
    }
    this.router.navigate(['message/detail', id]);
  }

  loadMoreMessages() {
    this.noOfMessageToLoad += 10;
  }

  goToTop() {
    scroll(0,0);
  }

  getStart(start: Date): string {
    const unformattedDate = start.toString();
    const date = new Date(start);
    let result = '';
    switch (date.getDay()) {
      case 0:
        result += 'Sunday, ';
        break;
      case 1:
        result += 'Monday, ';
        break;
      case 2:
        result += 'Tuesday, ';
        break;
      case 3:
        result += 'Wednesday, ';
        break;
      case 4:
        result += 'Thursday, ';
        break;
      case 5:
        result += 'Friday, ';
        break;
      case 6:
        result += 'Saturday, ';
        break;
    }
    result += unformattedDate.substring(8,10) + '.';
    result += unformattedDate.substring(5,7) + '.';
    result += unformattedDate.substring(0,4);
    result += ', ' + unformattedDate.substring(11,16);

    return result;
  }

  public openModal(messageDeleteWindow, id: number) {
    this.deleteId = id;
    this.modalService.open(messageDeleteWindow, {backdrop: 'static',size: 'lg'});
  }

  /**
   * Sends message creation request
   *
   * @param message the message which should be created
   */
  private createMessage(message: Message) {
    this.messageService.createMessage(message).subscribe({
        next: () => {
          this.loadMessage();
        },
        error: error => {
          console.error(error);
          this.notification.error('News could not be created');
        }
      }
    );
  }

  /**
   * Loads the specified page of message from the backend
   */
  private loadMessage() {
    if (this.authService.isLoggedIn()) {
      this.messageService.getUnreadMessage().subscribe({
        next: (message: Message[]) => {
          this.unreadMessages = message;
        },
        error: error => {
          console.error(error);
          this.notification.error('News page could not be loaded');
        }
      });
    }
    this.messageService.getMessage().subscribe({
      next: (message: Message[]) => {
        this.allMessages = message;
      },
      error: error => {
        console.error(error);
        this.notification.error('News page could not be loaded');
      }
    });
  }

  private clearForm() {
    this.currentMessage = new Message();
    this.submitted = false;
  }

}
