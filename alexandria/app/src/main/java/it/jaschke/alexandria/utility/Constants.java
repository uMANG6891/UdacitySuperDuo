package it.jaschke.alexandria.utility;

/**
 * Created by umang on 15/01/16.
 */
public class Constants {
    public static final String PREF_SEARCH_STATUS = "search_status";

    // Activity extras
    public static final String EAN_BOOK_ID = "EAN";
    public static final String EAN_IS_TABLET = "extra_is_tablet";

    public interface Book {

        String ITEMS = "items";

        String VOLUME_INFO = "volumeInfo";

        String EAN = "ean";
        String TITLE = "title";
        String SUBTITLE = "subtitle";
        String AUTHORS = "authors";
        String DESC = "description";
        String CATEGORIES = "categories";
        String IMG_URL_PATH = "imageLinks";
        String IMG_URL = "thumbnail";
    }
}
