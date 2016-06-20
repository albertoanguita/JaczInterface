package jacz.face.util;

import jacz.database.DatabaseMediator;

/**
 * Created by alberto on 6/20/16.
 */
public enum MediaItemType {
    MOVIE,
    TV_SERIES,
    CHAPTER,
    ALL,
    FAVORITE;

    public static MediaItemType buildType(DatabaseMediator.ItemType itemType) {
        switch (itemType) {
            case MOVIE:
                return MOVIE;
            case TV_SERIES:
                return TV_SERIES;
            case CHAPTER:
                return CHAPTER;
            default:
                throw new IllegalArgumentException("Invalid media item type: " + itemType.toString());
        }
    }
}
