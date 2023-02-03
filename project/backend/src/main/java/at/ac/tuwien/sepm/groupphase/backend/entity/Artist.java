package at.ac.tuwien.sepm.groupphase.backend.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.ManyToMany;
import javax.persistence.FetchType;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "artist")
public class Artist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "band_name")
    private String bandName;

    @Column(name = "stage_name")
    private String stageName;

    @ManyToMany(mappedBy = "featuredArtists", fetch = FetchType.LAZY)
    private List<Event> events;

    public Artist(String firstName, String lastName, String bandName, String stageName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.bandName = bandName;
        this.stageName = stageName;
    }

    public Artist() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getBandName() {
        return bandName;
    }

    public void setBandName(String bandName) {
        this.bandName = bandName;
    }

    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public String merchName() {
        if (bandName != null) {
            return bandName;
        }
        if (stageName != null) {
            return stageName;
        }
        String name = "";
        if (firstName != null) {
            name += firstName;
        }
        if (lastName != null) {
            if (firstName != null) {
                name += " " + lastName;
            } else {
                name += lastName;
            }
        }
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Artist artist)) {
            return false;
        }
        return Objects.equals(id, artist.id)
            && Objects.equals(firstName, artist.firstName)
            && Objects.equals(lastName, artist.lastName)
            && Objects.equals(bandName, artist.bandName)
            && Objects.equals(stageName, artist.stageName)
            && Objects.equals(events, artist.events);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, bandName, stageName, events);
    }

    @Override
    public String toString() {
        return "Artist{"
            + "id=" + id
            + ", firstName='" + firstName + '\''
            + ", lastName='" + lastName + '\''
            + ", bandName='" + bandName + '\''
            + ", stageName='" + stageName + '\''
            + //", events=" + events +
            '}';
    }

    public static final class ArtistBuilder {
        private Long id;
        private String firstName;
        private String lastName;
        private String bandName;
        private String stageName;
        private List<Event> events;

        private ArtistBuilder() {
        }

        public static ArtistBuilder anArtist() {
            return new ArtistBuilder();
        }

        public ArtistBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public ArtistBuilder withFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public ArtistBuilder withLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public ArtistBuilder withBandName(String bandName) {
            this.lastName = lastName;
            return this;
        }

        public ArtistBuilder withStageName(String stageName) {
            this.stageName = stageName;
            return this;
        }

        public ArtistBuilder withEvents(List<Event> events) {
            this.events = events;
            return this;
        }

        public Artist build() {
            Artist artist = new Artist();
            artist.setId(id);
            artist.setFirstName(firstName);
            artist.setLastName(lastName);
            artist.setBandName(bandName);
            artist.setStageName(stageName);
            artist.setEvents(events);
            return artist;
        }
    }
}
