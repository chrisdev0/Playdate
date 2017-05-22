package presentable;

import apilayer.handlers.Paths;
import lombok.Builder;
import lombok.Getter;
import model.Invite;
import model.Place;
import model.Playdate;


@Builder
@Getter
public class FeedObject {

    public class ObjectTypeId {
        public static final int PLAYDATE = 0;
        public static final int PLACE = 1;
        public static final int AD = 2;
        public static final int COMMENT = 3;
        public static final int INVITE = 4;
    }

    private Long id;

    private String header;
    private String shortDescription;
    private String imageUrl;
    private String placeName;
    private String category;
    private int objectTypeId;
    private String onClickUrl;

    public FeedObject(Long id, String header, String category, String placeName, String shortDescription, String imageUrl, int objectTypeId, String onClickUrl) {
        this.id = id;
        this.header = header;
        this.shortDescription = shortDescription;
        this.category = category;
        this.placeName = placeName;
        this.imageUrl = imageUrl;
        this.objectTypeId = objectTypeId;
        this.onClickUrl = onClickUrl;
    }

    public static FeedObject createFromPlayDate(Playdate playdate) {
        return new FeedObject(
                playdate.getId(),
                playdate.getHeader(),
                "Playdate",
                playdate.getPlace().getName(),
                playdate.getDescription(),
                Paths.APIIMAGE + "/" + playdate.getPlace().getImageId(),
                ObjectTypeId.PLAYDATE,
                Paths.PROTECTED + Paths.GETONEPLAYDATE + "?" + Paths.QueryParams.PLAYDATE_BY_ID + "=" + playdate.getId());
    }

    public static FeedObject createFromPlace(Place place) {
        return new FeedObject(
                place.getId(),
                place.getName(),
                place.getCategory(),
                place.getName(),
                place.getShortDescription(),
                Paths.APIIMAGE + "/" + place.getImageId(),
                ObjectTypeId.PLACE,
                Paths.PROTECTED + Paths.SHOWPLACE + "?" + Paths.QueryParams.PLACE_BY_ID + "=" + place.getId()
        );
    }

    public static FeedObject createFromInvite(Invite invite) {
        return new FeedObject(
                invite.getId(),
                invite.getPlaydate().getHeader(),
                "Inbjudan",
                invite.getPlaydate().getPlace().getName(),
                invite.getPlaydate().getDescription(),
                Paths.APIIMAGE + "/" + invite.getPlaydate().getPlace().getImageId(),
                ObjectTypeId.INVITE,
                Paths.PROTECTED + Paths.GETONEPLAYDATE + "?" + Paths.QueryParams.PLAYDATE_BY_ID + "=" + invite.getPlaydate().getId()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FeedObject that = (FeedObject) o;

        if (objectTypeId != that.objectTypeId) return false;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + objectTypeId;
        return result;
    }
}
