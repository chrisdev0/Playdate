package model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class ProfilePicture {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] image;


}
