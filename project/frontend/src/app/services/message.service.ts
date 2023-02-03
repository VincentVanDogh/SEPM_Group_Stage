import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Message} from '../dtos/message';
import {Observable} from 'rxjs';
import {Globals} from '../global/globals';

@Injectable({
  providedIn: 'root'
})
export class MessageService {

  private messageBaseUri: string = this.globals.backendUri + '/messages';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  /**
   * Loads all messages from the backend
   */
  getMessage(): Observable<Message[]> {
    return this.httpClient.get<Message[]>(this.messageBaseUri + '/all');
  }

  /**
   * Loads all messages the user has not read from the backend
   */
  getUnreadMessage(): Observable<Message[]> {
    return this.httpClient.get<Message[]>(this.messageBaseUri + '/unread');
  }

  /**
   * Loads specific message from the backend
   *
   * @param id of message to load
   */
  getMessageById(id: number): Observable<Message> {
    return this.httpClient.get<Message>(this.messageBaseUri + '/all/' + id);
  }

  /**
   * Persists message to the backend
   *
   * @param message to persist
   */
  createMessage(message: Message): Observable<Message> {
    return this.httpClient.post<Message>(this.messageBaseUri, message);
  }

  /**
   * Marks message with given ID as read for current user.
   *
   * @param id of message
   */
  markAsRead(id: number): Observable<void> {
    return this.httpClient.post<void>(this.messageBaseUri + '/' + id, null);
  }

  /**
   * Marks message with given ID as not read for current user.
   *
   * @param id of message
   */
  markAsUnread(id: number): Observable<void> {
    return this.httpClient.put<void>(this.messageBaseUri + '/' + id, null);
  }

  /**
   * Deletes the message with the given id
   *
   * @param id of message
   */
  deleteMessage(id: number): Observable<void> {
    return this.httpClient.delete<void>(this.messageBaseUri + '/' + id);
  }
}
