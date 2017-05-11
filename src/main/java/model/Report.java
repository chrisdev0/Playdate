package model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by martinsenden on 2017-05-11.
 */

@Data
@NoArgsConstructor
@Entity
public class Report {
    public Report(User reporter, User reportedUser, String reportDescription) {
        this.reportedUser = reportedUser;
        this.reporter = reporter;
        this.reportDescription = reportDescription;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private User reportedUser;

    @ManyToOne
    private User reporter;

    private String reportDescription;

    @Type(type = "timestamp")
    private Date createdAt = new Date();

}
