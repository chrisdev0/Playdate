package presentable.frontend;


import lombok.Data;
import model.Gender;
import model.User;

@Data
public class FrontEndUser {

    private final Long id;
    private final String name;
    private final String imageUrl;
    private final String description;
    private final Gender gender;
    private final String thirdPartyId;

    public FrontEndUser(User user) {
        id = user.getId();
        name = user.getName();
        imageUrl = user.getProfilePictureUrl();
        description = user.getDescription();
        gender = user.getGender();
        thirdPartyId = user.getFacebookThirdPartyID();
    }

}
