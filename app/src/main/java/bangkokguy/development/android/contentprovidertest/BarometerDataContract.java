package bangkokguy.development.android.contentprovidertest;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 *
 * Created by bangkokguy on 1/21/17.
 */

public final class BarometerDataContract {

    /**
     * The authority of the lentitems provider.
     */
    public static final String AUTHORITY = "bangkokguy.development.android.barometerdata";
    /**
     * The content URI for the top-level
     * lentitems authority.
     */
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    /**
     * Constants for the Items table
     * of the lentitems provider.
     */
    public static final class Items implements CommonColumns {

        /**
         * The content URI for this table.
         */
        public static final
        Uri CONTENT_URI = Uri.withAppendedPath(BarometerDataContract.CONTENT_URI, "items");

        /**
         * The mime type of a directory of items.
         */
        public static final
        String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.de.openminds.lentitems_items";

        /**
         * The mime type of a single item.
         */
        public static final
        String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.de.openminds.lentitems_items";

        /**
         * A projection of all columns
         * in the items table.
         */
        public static final String _ID = BaseColumns._ID;
        public static final String TIME = "time";
        public static final String VALUE = "value";

        public static final String[] PROJECTION_ALL = {_ID, TIME, VALUE};
        /**
         * The default sort order for
         * queries containing NAME fields.
         */
        public static final String SORT_ORDER_DEFAULT = TIME + " ASC";
    }

    /**
     * Constants for the Photos table of the
     * lentitems provider. For each item there
     * is exactly one photo. You can
     * safely call insert with the an already
     * existing ITEMS_ID. You won’t get constraint
     * violations. The content provider takes care
     * of this.<br>
     * Note: The _ID of the new record in this case
     * differs from the _ID of the old record.
     */
    public static final class Photos implements BaseColumns {
        /**
         * The content URI for this table.
         */
        public static final
        Uri CONTENT_URI = Uri.withAppendedPath(BarometerDataContract.CONTENT_URI, "photos");

        /**
         * The mime type of a directory of items.
         */
        public static final
        String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.de.openminds.lentitems_photos";

        /**
         * The mime type of a single item.
         */
        public static final
        String CONTENT_PHOTO_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.de.openminds.lentitems_photos";

        /**
         * A projection of all columns
         * in the items table.
         */
        public static final String _ID = "_id";
        public static final String TIME = "time";
        public static final String VALUE = "value";

        public static final String[] PROJECTION_ALL = {_ID, TIME, VALUE};
        /**
         * The default sort order for
         * queries containing NAME fields.
         */
        public static final String SORT_ORDER_DEFAULT = TIME + " ASC";
    }

    /**
     * Constants for a joined view of Items and
     * Photos. The _id of this joined view is
     * the _id of the Items table.
     */
    public static final class ItemEntities implements CommonColumns {
        /**
         * The content URI for this table.
         */
        public static final
        Uri CONTENT_URI = Uri.withAppendedPath(BarometerDataContract.CONTENT_URI, "entities");

        /**
         * The mime type of a directory of items.
         */
        public static final
        String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.de.openminds.lentitems_entities";

        /**
         * The mime type of a single item.
         */
        public static final
        String CONTENT_ENTITY_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.de.openminds.lentitems_entities";

        /**
         * A projection of all columns
         * in the items table.
         */
        public static final String _ID = "_id";
        public static final String TIME = "time";
        public static final String VALUE = "value";

        public static final String[] PROJECTION_ALL = {_ID, TIME, VALUE};
        /**
         * The default sort order for
         * queries containing NAME fields.
         */
        public static final String SORT_ORDER_DEFAULT = TIME + " ASC";

    }

    /**
     * This interface defines common columns
     * found in multiple tables.
     */
    interface CommonColumns extends BaseColumns {
    }
}