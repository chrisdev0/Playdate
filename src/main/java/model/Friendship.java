package model;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"friend_id", "requester_id"}))
public class Friendship {
    public Friendship(User friend, User requester) {
        this.friend = friend;
        this.requester = requester;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(referencedColumnName = "id")
    private User friend;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(referencedColumnName = "id")
    private User requester;

    @Column(columnDefinition="DATETIME", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Friendship that = (Friendship) o;

        if (friend != null ? !friend.equals(that.friend) : that.friend != null) return false;
        return requester != null ? requester.equals(that.requester) : that.requester == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (friend != null ? friend.hashCode() : 0);
        result = 31 * result + (requester != null ? requester.hashCode() : 0);
        return result;
    }
}
