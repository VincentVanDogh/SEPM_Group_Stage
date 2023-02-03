package at.ac.tuwien.sepm.groupphase.backend.entity;


import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import java.util.List;


import java.util.Objects;
import java.util.Set;

@Entity(name = "application_user")
public class ApplicationUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100, unique = true)
    private String email;

    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(nullable = false, length = 100)
    private String lastName;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(columnDefinition = "boolean default false")
    private Boolean admin;

    @Column(name = "locked_out", columnDefinition = "boolean default false")
    private Boolean lockedOut;

    @Column(name = "times_wrong_pw_entered", columnDefinition = "integer default 0")
    private Integer timesWrongPwEntered;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE, mappedBy = "buyer")
    @OrderBy("id DESC")
    private List<TicketAcquisition> orders;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE, mappedBy = "buyer")
    @OrderBy("id DESC")
    private List<TicketAcquisition> tickets;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "buyer")
    @OrderBy("id DESC")
    private List<MerchPurchase> merchPurchases;

    @ManyToMany(mappedBy = "readBy", fetch = FetchType.LAZY)
    private Set<Message> readNews;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public Boolean getLockedOut() {
        return lockedOut;
    }

    public void setLockedOut(Boolean lockedOut) {
        this.lockedOut = lockedOut;
    }

    public Integer getTimesWrongPwEntered() {
        return timesWrongPwEntered;
    }

    public void setTimesWrongPwEntered(Integer timesWrongPwEntered) {
        this.timesWrongPwEntered = timesWrongPwEntered;
    }

    public List<TicketAcquisition> getOrders() {
        return orders;
    }

    public void setOrders(List<TicketAcquisition> orders) {
        this.orders = orders;
    }

    public Set<Message> getReadNews() {
        return readNews;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ApplicationUser applicationUser)) {
            return false;
        }
        return Objects.equals(id, applicationUser.id)
            && Objects.equals(email, applicationUser.email)
            && Objects.equals(firstName, applicationUser.firstName)
            && Objects.equals(lastName, applicationUser.lastName)
            && Objects.equals(password, applicationUser.password)
            && Objects.equals(lockedOut, applicationUser.lockedOut)
            && Objects.equals(timesWrongPwEntered, applicationUser.timesWrongPwEntered)
            && Objects.equals(admin, applicationUser.admin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, firstName, lastName, password, admin, lockedOut, timesWrongPwEntered);
    }

    @Override
    public String toString() {
        return "ApplicationUser{"
            + "id=" + id
            + ", email='" + email + '\''
            + ", firstName='" + firstName + '\''
            + ", lastName='" + lastName + '\''
            + ", password='" + password + '\''
            + ", admin=" + admin
            + ", lockedOut=" + lockedOut
            + ", timesWrongPwEntered=" + timesWrongPwEntered
            + '}';
    }


    public static final class ApplicationUserBuilder {
        private Long id;
        private String email;

        private String firstName;

        private String lastName;
        private String password;
        private Boolean admin;
        private Boolean lockedOut;
        private Long bonusPoints;
        private Integer timesWrongPwEntered;

        private ApplicationUserBuilder() {
        }

        public static ApplicationUserBuilder anApplicationUser() {
            return new ApplicationUserBuilder();
        }

        public ApplicationUserBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public ApplicationUserBuilder withEmail(String email) {
            this.email = email;
            return this;
        }

        public ApplicationUserBuilder withFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public ApplicationUserBuilder withLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public ApplicationUserBuilder withPassword(String password) {
            this.password = password;
            return this;
        }

        public ApplicationUserBuilder isAdmin(Boolean admin) {
            this.admin = admin;
            return this;
        }

        public ApplicationUserBuilder isLockedOut(Boolean lockedOut) {
            this.lockedOut = lockedOut;
            return this;
        }

        public ApplicationUserBuilder withBonusPoints(Long bonusPoints) {
            this.bonusPoints = bonusPoints;
            return this;
        }

        public ApplicationUserBuilder withTimesWrongPwEntered(Integer timesWrongPwEntered) {
            this.timesWrongPwEntered = timesWrongPwEntered;
            return this;
        }

        public ApplicationUser build() {
            ApplicationUser applicationUser = new ApplicationUser();
            applicationUser.setId(id);
            applicationUser.setEmail(email);
            applicationUser.setFirstName(firstName);
            applicationUser.setLastName(lastName);
            applicationUser.setPassword(password);
            applicationUser.setLockedOut(lockedOut);
            applicationUser.setAdmin(admin);
            applicationUser.setTimesWrongPwEntered(timesWrongPwEntered);
            return applicationUser;
        }
    }
}
