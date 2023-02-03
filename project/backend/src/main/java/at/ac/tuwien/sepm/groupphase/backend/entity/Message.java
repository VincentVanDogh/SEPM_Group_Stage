package at.ac.tuwien.sepm.groupphase.backend.entity;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;


@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "published_at")
    private LocalDateTime publishedAt;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 500)
    private String summary;

    @Column(nullable = false, length = 10000)
    private String text;

    @Column(nullable = true, length = 10000)
    private String image;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "news_read",
        joinColumns = @JoinColumn(name = "message_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id")
    )
    private Set<ApplicationUser> readBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Set<ApplicationUser> getReadBy() {
        return readBy;
    }

    public void setReadBy(Set<ApplicationUser> readBy) {
        this.readBy = readBy;
    }

    public void removeUser(ApplicationUser user) {
        this.readBy.remove(user);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Message message)) {
            return false;
        }
        return Objects.equals(id, message.id)
            && Objects.equals(publishedAt, message.publishedAt)
            && Objects.equals(title, message.title)
            && Objects.equals(summary, message.summary)
            && Objects.equals(text, message.text)
            && Objects.equals(image, message.image);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, publishedAt, title, summary, text, image);
    }

    @Override
    public String toString() {
        return "Message{"
            + "id=" + id
            + ", publishedAt=" + publishedAt
            + ", title='" + title + '\''
            + ", summary='" + summary + '\''
            + ", text='" + text + '\''
            + ", image='" + image + '\''
            + '}';
    }


    public static final class MessageBuilder {
        private Long id;
        private LocalDateTime publishedAt;
        private String title;
        private String summary;
        private String text;
        private String image;
        private Set<ApplicationUser> readBy;

        private MessageBuilder() {
        }

        public static MessageBuilder aMessage() {
            return new MessageBuilder();
        }

        public MessageBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public MessageBuilder withPublishedAt(LocalDateTime publishedAt) {
            this.publishedAt = publishedAt;
            return this;
        }

        public MessageBuilder withTitle(String title) {
            this.title = title;
            return this;
        }

        public MessageBuilder withSummary(String summary) {
            this.summary = summary;
            return this;
        }

        public MessageBuilder withText(String text) {
            this.text = text;
            return this;
        }

        public MessageBuilder withImage(String image) {
            this.image = image;
            return this;
        }

        public MessageBuilder withReadBy(Set<ApplicationUser> readBy) {
            this.readBy = readBy;
            return this;
        }

        public Message build() {
            Message message = new Message();
            message.setId(id);
            message.setPublishedAt(publishedAt);
            message.setTitle(title);
            message.setSummary(summary);
            message.setText(text);
            message.setImage(image);
            message.setReadBy(readBy);
            return message;
        }
    }
}