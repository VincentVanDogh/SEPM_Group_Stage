package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import javax.validation.constraints.NotNull;

public class PasswordDto {
    @NotNull(message = "Wrong password")
    private String oldPassword;
    @NotNull(message = "New Password must not be null")
    private String newPassword;
    @NotNull(message = "Password confirmation must not be null")
    private String confirmation;

    public PasswordDto() {
    }

    public PasswordDto(String oldPassword, String newPassword, String confirmation) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.confirmation = confirmation;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
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
}
