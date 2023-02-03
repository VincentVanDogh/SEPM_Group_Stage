package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Message;

import java.util.List;

public interface MessageService {

    /**
     * Find all message entries ordered by published at date (descending).
     *
     * @return ordered list of all message entries
     */
    List<Message> findAll();


    /**
     * Find a single message entry by id.
     *
     * @param id the id of the message entry
     * @return the message entry
     */
    Message findOne(Long id);

    /**
     * Publish a single message entry.
     *
     * @param message to publish
     * @return published message entry
     */
    Message publishMessage(Message message);

    /**
     * Find unread message entries ordered by published at date (descending).
     *
     * @return ordered list of all message entries that have not been read by the user
     */
    List<Message> findNotRead();

    /**
     * Mark a single message as read.
     *
     * @param messageId ID of the message to be marked
     * @return String with success message
     */
    String markAsRead(Long messageId);

    /**
     * Mark a single message as unread.
     *
     * @param messageId ID of the message to be unmarked
     * @return String with success message
     */
    String markAsUnread(Long messageId);

    /**
     * Delete the message given the messageId.
     *
     * @param messageId ID of the message to be deleted
     */
    void delete(Long messageId);
}
