package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import javax.validation.constraints.NotNull;

public class NewPasswordDto {
    @NotNull(message = "Wrong password")
    private String newPassword;
    @NotNull(message = "New Password must not be null")
    private String confirmation;
    @NotNull(message = "Password confirmation must not be null")
    private String token;

    public NewPasswordDto() {
    }

    public NewPasswordDto(String oldPassword, String newPassword, String confirmation) {
        this.newPassword = oldPassword;
        this.confirmation = newPassword;
        this.token = confirmation;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmation() {
        return confirmation;
    }

    public void setConfirmation(String confirmation) {
        this.confirmation = confirmation;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
