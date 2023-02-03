package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import java.util.List;

public class UserPageDto {

    private int numberOfPages;
    private List<UserDto> users;

    public UserPageDto(int numberOfPages, List<UserDto> users) {
        this.numberOfPages = numberOfPages;
        this.users = users;
    }

    public UserPageDto() {
    }

    public int getNumberOfPages() {
        return numberOfPages;
    }

    public void setNumberOfPages(int numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    public List<UserDto> getUsers() {
        return users;
    }

    public void setUsers(List<UserDto> users) {
        this.users = users;
    }
}
