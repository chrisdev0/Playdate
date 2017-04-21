package model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Child {

    @Id
    private Long id;

    private int months;

    @Column(nullable = true)
    private Gender gender;

    



}
