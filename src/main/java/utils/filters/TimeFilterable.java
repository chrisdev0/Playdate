package utils.filters;

import apilayer.Constants;

public class TimeFilterable {


    public enum TimeFilter {
        ALL,
        HISTORY,
        RECENT_HAPPENED,
        NEAR_FUTURE,
        FUTURE
    }


    public static long getStartTime(TimeFilter timeFilter) {
        switch (timeFilter) {
            case RECENT_HAPPENED:
                return System.currentTimeMillis() - Constants.COUNT_PLAYDATE_AS_RECENT;
            case FUTURE:
            case NEAR_FUTURE:
                return System.currentTimeMillis();
            default:
                return 0L;
        }
    }

    public static long getEndTime(TimeFilter timeFilter) {
        switch (timeFilter) {
            case RECENT_HAPPENED:
            case HISTORY:
                return System.currentTimeMillis();
            case FUTURE:
            case ALL:
                return Long.MAX_VALUE;
            case NEAR_FUTURE:
                return System.currentTimeMillis() + Constants.COUNT_PLAYDATE_AS_NEAR_CUTOFF;
            default:
                return 0L;
        }
    }





}
