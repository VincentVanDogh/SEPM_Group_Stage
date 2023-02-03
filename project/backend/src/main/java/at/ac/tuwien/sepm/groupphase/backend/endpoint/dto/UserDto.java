package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

public class UserDto {
    @NotNull(message = "Email must not be null")
    @Email
    private String email;

    @NotNull(message = "First name must not be null")
    private String firstName;

    @NotNull(message = "Last name must not be null")
    private String lastName;

    private Boolean lockedOut;

    public UserDto() {

    }

    public UserDto(String firstname, String lastname, String email, Boolean lockedOut) {
        this.firstName = firstname;
        this.lastName = lastname;
        this.email = email;
        this.lockedOut = lockedOut;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Boolean getLockedOut() {
        return lockedOut;
    }

    public void setLockedOut(Boolean lockedOut) {
        this.lockedOut = lockedOut;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
