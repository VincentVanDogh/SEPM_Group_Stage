package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import java.util.List;

/**
 * Data Transfer Object (DTO) used to transfer the information about a conflict error regarding data sent to the server
 * back to the client.
 *
 * @param message Generic message informing about the existence of errors
 * @param errors A list of errors that have arisen in the data sent to the server
 */
public record ConflictErrorRestDto(
    String message,
    List<String> errors
) {
}
