package model;

import lombok.Data;
import org.apache.commons.lang.ArrayUtils;

import javax.persistence.*;

@Entity
@Data
public class ProfilePicture {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Lob
    @Column(columnDefinition = "LONGBLOB", nullable = false)
    private Byte[] image;


    public void setImage(Byte[] image) {
        this.image = image;
    }

    public void setImage(byte[] image) {
        this.image = ArrayUtils.toObject(image);
    }


}
