package at.ac.tuwien.sepm.groupphase.backend.service.validation;


import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Service
public class CredentialValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public void validateForRegistration(ApplicationUser applicationUser) throws ValidationException {
        LOGGER.debug("Validate credentials for Registration {}", applicationUser);
        List<String> validationErrors = new ArrayList<>();

        if (applicationUser.getEmail() != null) {
            if (applicationUser.getEmail().contains(" ")) {
                validationErrors.add(" Email contains blank spaces");
            }
            if (applicationUser.getEmail().isBlank()) {
                validationErrors.add(" Email is blank");
            }
            if (!applicationUser.getEmail().contains("@")) {
                validationErrors.add(" Invalid Email format");
            } else if (!((applicationUser.getEmail().substring(applicationUser.getEmail().indexOf('@') + 1)).contains("."))) {
                validationErrors.add(" Invalid Email format");
            }
        } else {
            validationErrors.add(" Email may not be null");
        }

        if (applicationUser.getFirstName() == null) {
            validationErrors.add(" No first name given");
        } else if (applicationUser.getFirstName().isBlank()) {
            validationErrors.add(" First name is given but blank");
        } else if (applicationUser.getFirstName().length() > 255) {
            validationErrors.add(" First name too long: longer than 255 characters");
        }

        if (applicationUser.getLastName() == null) {
            validationErrors.add(" No last name given");
        } else if (applicationUser.getLastName().isBlank()) {
            validationErrors.add(" Last name is given but blank");
        } else if (applicationUser.getLastName().length() > 255) {
            validationErrors.add(" Last name too long: longer than 255 characters");
        }
        if (!validationErrors.isEmpty()) {
            throw new ValidationException("Registration failed", validationErrors);
        }
    }
}
