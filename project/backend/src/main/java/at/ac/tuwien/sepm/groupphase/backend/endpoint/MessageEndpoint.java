package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DetailedMessageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MessageInquiryDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SimpleMessageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.MessageMapper;
import at.ac.tuwien.sepm.groupphase.backend.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;

import javax.annotation.security.PermitAll;
import javax.validation.Valid;
import java.lang.invoke.MethodHandles;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/messages")
public class MessageEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final MessageService messageService;
    private final MessageMapper messageMapper;

    @Autowired
    public MessageEndpoint(MessageService messageService, MessageMapper messageMapper) {
        this.messageService = messageService;
        this.messageMapper = messageMapper;
    }

    @PermitAll
    @GetMapping("/all")
    @Operation(summary = "Get list of messages without details", security = @SecurityRequirement(name = "apiKey"))
    @Transactional
    public List<SimpleMessageDto> findAll() {
        LOGGER.info("GET /api/v1/messages/all");
        LOGGER.debug("findAll()");
        return messageMapper.messageToSimpleMessageDto(messageService.findAll());
    }

    @Secured("ROLE_USER")
    @GetMapping("/unread")
    @Operation(summary = "Get list of unread messages without details", security = @SecurityRequirement(name = "apiKey"))
    @Transactional
    public List<SimpleMessageDto> findNotRead() {
        LOGGER.info("GET /api/v1/messages/unread");
        LOGGER.debug("findNotRead()");
        return messageMapper.messageToSimpleMessageDto(messageService.findNotRead());
    }

    @PermitAll
    @GetMapping("/all/{id}")
    @Operation(summary = "Get detailed information about a specific message", security = @SecurityRequirement(name = "apiKey"))
    public DetailedMessageDto find(@PathVariable Long id) {
        LOGGER.info("GET /api/v1/messages/{}", id);
        LOGGER.debug("find({})", id);
        return messageMapper.messageToDetailedMessageDto(messageService.findOne(id));
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Operation(summary = "Publish a new message", security = @SecurityRequirement(name = "apiKey"))
    public DetailedMessageDto create(@Valid @RequestBody MessageInquiryDto messageDto) {
        LOGGER.info("POST /api/v1/messages");
        LOGGER.debug("create({})", messageDto);
        return messageMapper.messageToDetailedMessageDto(
            messageService.publishMessage(messageMapper.messageInquiryDtoToMessage(messageDto)));
    }

    @Secured("ROLE_USER")
    @PostMapping("/{id}")
    @Operation(summary = "Mark a message as read", security = @SecurityRequirement(name = "apiKey"))
    @Transactional
    public void markAsRead(@PathVariable Long id) {
        LOGGER.info("POST /api/v1/messages/{}", id);
        LOGGER.debug("markAsRead({})", id);
        messageService.markAsRead(id);
    }

    @Secured("ROLE_USER")
    @PutMapping("/{id}")
    @Operation(summary = "Mark a message as unread", security = @SecurityRequirement(name = "apiKey"))
    @Transactional
    public void markAsUnread(@PathVariable Long id) {
        LOGGER.info("PUT /api/v1/messages/{}", id);
        LOGGER.debug("markAsUnread({})", id);
        messageService.markAsUnread(id);
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a message", security = @SecurityRequirement(name = "apiKey"))
    public void delete(@PathVariable Long id) {
        LOGGER.info("DELETE /api/v1/messages/{}", id);
        LOGGER.debug("delete({})", id);
        messageService.delete(id);
    }
}
