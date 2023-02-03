import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Message} from '../../dtos/message';
import {StagePlanService} from '../../services/stage-plan.service';
import {ToastrService} from 'ngx-toastr';
import {MessageService} from '../../services/message.service';
import {Title} from '@angular/platform-browser';


@Component({
  selector: 'app-message-details',
  templateUrl: './message-details.component.html',
  styleUrls: ['./message-details.component.scss']
})
export class MessageDetailsComponent implements OnInit {
  message: Message;
  id: number;

  constructor(
    private titleService: Title,
    private route: ActivatedRoute,
    private router: Router,
    private messageService: MessageService,
    private stagePlanService: StagePlanService,
    private notification: ToastrService
  ) { }

  ngOnInit(): void {
    const getId = this.route.snapshot.paramMap.get('id');
    if (getId) {
      this.id = Number.parseInt(getId, 10);
      this.loadMessage();
    }
  }

  loadMessage(): void {
    this.messageService.getMessageById(this.id).subscribe({
      next: (messageData: Message) => {
        this.message = messageData;
        this.titleService.setTitle(`News | ${messageData.title}`);
      },
      error: error => {
        console.error(error);
        this.notification.error('Could not load message with id: ' + this.id);
      }
    });
  }
}
