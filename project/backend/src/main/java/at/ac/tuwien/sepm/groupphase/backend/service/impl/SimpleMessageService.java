package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.MessageRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SimpleMessageService implements MessageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public SimpleMessageService(MessageRepository messageRepository,
                                UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public List<Message> findAll() {
        LOGGER.debug("Find all messages");
        return messageRepository.findAllByOrderByPublishedAtDesc();
    }

    @Override
    public Message findOne(Long id) {
        LOGGER.debug("Find message with id {}", id);
        Optional<Message> message = messageRepository.findById(id);
        if (message.isPresent()) {
            return message.get();
        } else {
            throw new NotFoundException(String.format("Could not find message with id %s", id));
        }
    }

    @Override
    public Message publishMessage(Message message) {
        LOGGER.debug("Publish new message {}", message);
        message.setPublishedAt(LocalDateTime.now());
        if (message.getImage() == null) {
            message.setImage("");
        }
        return messageRepository.save(message);
    }

    @Override
    @Transactional
    public List<Message> findNotRead() {
        LOGGER.debug("Find all messages a user has not read");

        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = 0L;
        if (username != null) {
            userId = userRepository.findApplicationUserByEmail(username).getId();
        }
        return messageRepository.findNotReadByUserOrderByPublishedAtDesc(userId);
    }

    @Override
    @Transactional
    public String markAsRead(Long messageId) {
        LOGGER.debug("Mark a message as read by a user");

        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (username != null) {
            Message message = messageRepository.findById(messageId).orElseThrow(() -> new IllegalArgumentException("Invalid message id"));
            ApplicationUser user = userRepository.findApplicationUserByEmail(username);
            message.getReadBy().add(user);
            messageRepository.save(message);
        } else {
            throw new IllegalArgumentException("Invalid user");
        }
        return "Success";
    }

    @Override
    @Transactional
    public String markAsUnread(Long messageId) {
        LOGGER.debug("Mark a message as read by a user");

        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (username != null) {
            Message message = messageRepository.findById(messageId).orElseThrow(() -> new IllegalArgumentException("Invalid message id"));
            ApplicationUser user = userRepository.findApplicationUserByEmail(username);
            message.getReadBy().remove(user);
            messageRepository.save(message);
        } else {
            throw new IllegalArgumentException("Invalid user");
        }
        return "Success";
    }

    @Override
    public void delete(Long messageId) {
        LOGGER.debug("Delete message with id: " + messageId);
        if (messageRepository.existsById(messageId)) {
            messageRepository.deleteById(messageId);
        } else {
            throw new IllegalArgumentException("No message with id: " + messageId);
        }
    }

}
