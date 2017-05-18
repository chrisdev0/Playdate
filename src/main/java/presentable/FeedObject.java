package presentable;

import apilayer.handlers.Paths;
import lombok.Builder;
import model.Place;
import model.Playdate;


@Builder
public class FeedObject {

    public class ObjectTypeId {
        private static final int PLAYDATE = 0;
        private static final int PLACE = 1;
        private static final int AD = 2;
        private static final int COMMENT = 3;
    }

    private Long id;

    private String header;
    private String shortDescription;
    private String imageUrl;
    private String placeName;
    private int objectTypeId;
    private String onClickUrl;

    public FeedObject(Long id, String header, String placeName, String shortDescription, String imageUrl, int objectTypeId, String onClickUrl) {
        this.id = id;
        this.header = header;
        this.shortDescription = shortDescription;
        this.placeName = placeName;
        this.imageUrl = imageUrl;
        this.objectTypeId = objectTypeId;
        this.onClickUrl = onClickUrl;
    }

    public static FeedObject createFromPlayDate(Playdate playdate) {
        return new FeedObject(
                playdate.getId(),
                playdate.getHeader(),
                playdate.getPlace().getName(),
                playdate.getDescription(),
                Paths.APIIMAGE + "/" + playdate.getPlace().getImageId(),
                0,
                Paths.PROTECTED + Paths.GETONEPLAYDATE + "?" + Paths.QueryParams.PLAYDATE_BY_ID + "=" + playdate.getId());
    }

    public static FeedObject createFromPlace(Place place) {
        return new FeedObject(
                place.getId(),
                place.getName(),
                place.getName(),
                place.getShortDescription(),
                Paths.APIIMAGE + "/" + place.getImageId(),
                1,
                Paths.PROTECTED + Paths.SHOWPLACE + "?" + Paths.QueryParams.PLACE_BY_ID + "=" + place.getId()
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
