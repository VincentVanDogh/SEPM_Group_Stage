package at.ac.tuwien.sepm.groupphase.backend.entity;

import at.ac.tuwien.sepm.groupphase.backend.type.EventType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;
import javax.persistence.ManyToOne;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;
import javax.persistence.ManyToMany;
import javax.persistence.JoinTable;
import java.util.List;

@Entity
@Table(name = "event")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 4095)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType type;

    @Column(nullable = false)
    private int duration; /* in minutes */

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "location_id", referencedColumnName = "id", nullable = false)
    private Location location;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE, mappedBy = "event")
    private List<Act> acts;

    @ManyToMany(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @JoinTable(
        name = "artists_featured",
        joinColumns = @JoinColumn(name = "artist_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "event_id", referencedColumnName = "id")
    )
    private List<Artist> featuredArtists;

    public Event() {
    }

    public Event(String name, String description, EventType type, int duration) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.duration = duration;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<Act> getActs() {
        return acts;
    }

    public void setActs(List<Act> acts) {
        this.acts = acts;
    }

    public List<Artist> getFeaturedArtists() {
        return featuredArtists;
    }

    public void setFeaturedArtists(List<Artist> featuredArtists) {
        this.featuredArtists = featuredArtists;
    }


    public static final class EventBuilder {

        private Long id;
        private String name;
        private String description;
        private EventType type;
        private int duration;
        private Location location;
        private List<Act> acts;
        private List<Artist> featuredArtists;

        private EventBuilder() {
        }

        public static EventBuilder anEvent() {
            return new EventBuilder();
        }

        public EventBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public EventBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public EventBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public EventBuilder withEventType(EventType type) {
            this.type = type;
            return this;
        }

        public EventBuilder withDuration(int duration) {
            this.duration = duration;
            return this;
        }

        public EventBuilder withLocation(Location location) {
            this.location = location;
            return this;
        }

        public EventBuilder withActs(List<Act> acts) {
            this.acts = acts;
            return this;
        }

        public EventBuilder withArtists(List<Artist> featuredArtists) {
            this.featuredArtists = featuredArtists;
            return this;
        }

        public Event build() {
            Event event = new Event();
            event.setId(id);
            event.setName(name);
            event.setDescription(description);
            event.setType(type);
            event.setDuration(duration);
            event.setLocation(location);
            event.setActs(acts);
            event.setFeaturedArtists(featuredArtists);
            return event;
        }

    }
}
