package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class DBAPIImage {

    private String apiResourceId;

    private byte[] imageAsByte;

}
