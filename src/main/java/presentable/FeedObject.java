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
    private int objectTypeId;
    private String onClickUrl;

    public FeedObject(Long id, String header, String shortDescription, String imageUrl, int objectTypeId, String onClickUrl) {
        this.id = id;
        this.header = header;
        this.shortDescription = shortDescription;
        this.imageUrl = imageUrl;
        this.objectTypeId = objectTypeId;
        this.onClickUrl = onClickUrl;
    }

    public static FeedObject createFromPlayDate(Playdate playdate) {
        return new FeedObject(
                playdate.getId(),
                playdate.getHeader(),
                playdate.getDescription(),
                Paths.PLAYDATE_IMAGE + "/" + playdate.getId(),
                0,
                Paths.PROTECTED + Paths.GETONEPLAYDATE + "?" + Paths.QueryParams.PLAYDATE_BY_ID + "=" + playdate.getId());
    }

    public static FeedObject createFromPlace(Place place) {
        return new FeedObject(
                place.getId(),
                place.getName(),
                place.getShortDescription(),
                Paths.APIIMAGE + "/" + place.getImageId(),
                1,
                Paths.PROTECTED + Paths.SHOWPLACE + "?" + Paths.QueryParams.PLACE_BY_ID + "=" + place.getId()
        );
    }





}
