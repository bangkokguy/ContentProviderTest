package bangkokguy.development.android.contentprovidertest;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static android.content.Context.MODE_PRIVATE;

public class BarometerData extends ContentProvider {

    private static final String TAG = BarometerData.class.getSimpleName();
    private static final boolean DEBUG = true;

    final static int DB_VERSION = 1;
    final static String DB_NAME = "my.db";

    // helper constants for use with the UriMatcher
    private static final int ITEM_LIST = 1;
    private static final int ITEM_ID = 2;
    private static final int PHOTO_LIST = 5;
    private static final int PHOTO_ID = 6;
    private static final int ENTITY_LIST = 10;
    private static final int ENTITY_ID = 11;

    private static final UriMatcher URI_MATCHER;
    // prepare the UriMatcher
    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(BarometerDataContract.AUTHORITY, "items", ITEM_LIST);
        URI_MATCHER.addURI(BarometerDataContract.AUTHORITY, "items/#", ITEM_ID);
        URI_MATCHER.addURI(BarometerDataContract.AUTHORITY, "photos", PHOTO_LIST);
        URI_MATCHER.addURI(BarometerDataContract.AUTHORITY, "photos/#", PHOTO_ID);
        URI_MATCHER.addURI(BarometerDataContract.AUTHORITY, "entities", ENTITY_LIST);
        URI_MATCHER.addURI(BarometerDataContract.AUTHORITY, "entities/#", ENTITY_ID);
    }

    public BarometerData() {
        if(DEBUG)Log.d(TAG,"BarometerData()");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data at the given URI.
        if(DEBUG)Log.d(TAG,"getType()");
        switch (URI_MATCHER.match(uri)) {
            case ITEM_LIST:
                return BarometerDataContract.Items.CONTENT_TYPE;
            case ITEM_ID:
                return BarometerDataContract.Items.CONTENT_ITEM_TYPE;
            case PHOTO_ID:
                return BarometerDataContract.Photos.CONTENT_PHOTO_TYPE;
            case PHOTO_LIST:
                return BarometerDataContract.Photos.CONTENT_TYPE;
            case ENTITY_ID:
                return BarometerDataContract.ItemEntities.CONTENT_ENTITY_TYPE;
            case ENTITY_LIST:
                return BarometerDataContract.ItemEntities.CONTENT_TYPE;
            default:
                return null;
        }
    }

    boolean isInBatchMode() {
        return false;
    }

    private Uri getUriForId(long id, Uri uri) {
        if (id > 0) {
            Uri itemUri = ContentUris.withAppendedId(uri, id);
            if (!isInBatchMode()) {
                // notify all listeners of changes:
                getContext().
                        getContentResolver().
                        notifyChange(itemUri, null);
            }
            return itemUri;
        }
        // s.th. went wrong:
        throw new SQLException(
                "Problem while inserting into uri: " + uri);
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        if (URI_MATCHER.match(uri) != ITEM_LIST
                && URI_MATCHER.match(uri) != PHOTO_LIST) {
            throw new IllegalArgumentException(
                    "Unsupported URI for insertion: " + uri);
        }
        SQLiteDatabase db = prepareDatabase.getWritableDatabase();
        if (URI_MATCHER.match(uri) == ITEM_LIST) {
            long id =
                    db.insert(
                            DbSchema.TBL_ITEMS,
                            null,
                            values);
            return getUriForId(id, uri);
        } else {
            // this insertWithOnConflict is a special case;
            // CONFLICT_REPLACE means that an existing entry
            // which violates the UNIQUE constraint on the
            // item_id column gets deleted. In this case this
            // INSERT behaves nearly like an UPDATE. Though
            // the new row has a new primary key.
            // See how I mentioned this in the Contract class.
            long id =
                    db.insertWithOnConflict(
                            DbSchema.TBL_PHOTOS,
                            null,
                            values,
                            SQLiteDatabase.CONFLICT_REPLACE);
            return getUriForId(id, uri);
        }
    }

    public class PrepareDatabase extends SQLiteOpenHelper {

        private final static String TAG = "PrepareDatabase";

        Context context;

        public PrepareDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
            super(context, name, factory, version, errorHandler);
            this.context = context;
        }

        public PrepareDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
            this.context = context;
        }

        public PrepareDatabase(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
            this.context = context;
            if(DEBUG)Log.d(TAG,"PrepareDatabase()");
        }

        private void executeSQLScript(SQLiteDatabase database, String asset_name) {

            if(DEBUG)Log.d(TAG,"executeSQLScript() "+asset_name);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte buf[] = new byte[1024];
            int len;
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = null;

            try {
                inputStream = assetManager.open(asset_name);
                while ((len = inputStream.read(buf)) != -1) {
                    outputStream.write(buf, 0, len);
                }
                outputStream.close();
                inputStream.close();

                String[] createScript = outputStream.toString().split(";");
                for (String aCreateScript : createScript) {
                    String sqlStatement = aCreateScript.trim();
                    if (DEBUG) Log.d(TAG, "SQL Statement " + sqlStatement);
                    // TODO You may want to parse out comments here
                    if(DEBUG)Log.d(TAG, "Path:"+database.getPath());
                    if (sqlStatement.length() > 0) {
                        database.execSQL(sqlStatement + ";");
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, "Handle Script Failed to Load");
            } catch (SQLException e) {
                Log.e(TAG, "Handle Script Failed to Execute");
            }
        }


        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            if (DEBUG) Log.d(TAG, "onCreate()");
            executeSQLScript(sqLiteDatabase, "create.sql");
                /*sqLiteDatabase.execSQL(
                        "CREATE TABLE IF NOT EXISTS " +
                                "BarometerDB " +
                                "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                "measureTime TEXT, measuredValue NUMERIC);");*/
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }
    }

    PrepareDatabase prepareDatabase = null;

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        if (DEBUG) Log.d(TAG, "onCreate()");
        prepareDatabase = new PrepareDatabase(getContext());
        SQLiteDatabase db = getContext().openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);
        prepareDatabase.onCreate(db);
        return true;
    }

    @Override
    public Cursor query(
            @NonNull Uri uri,
            String[] projection,
            String selection,
            String[] selectionArgs,
            String sortOrder)
    {
        if(DEBUG)Log.d(TAG,"query()");

        SQLiteDatabase db = prepareDatabase.getReadableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        boolean useAuthorityUri = false;
        switch (URI_MATCHER.match(uri)) {
            case ITEM_LIST:
                builder.setTables(DbSchema.TBL_ITEMS);
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = BarometerDataContract.Items.SORT_ORDER_DEFAULT;
                }
                break;
            case ITEM_ID:
                builder.setTables(DbSchema.TBL_ITEMS);
                // limit query to one row at most:
                builder.appendWhere(BarometerDataContract.Items._ID + " = " +
                        uri.getLastPathSegment());
                break;
            case PHOTO_LIST:
                builder.setTables(DbSchema.TBL_PHOTOS);
                break;
            case PHOTO_ID:
                builder.setTables(DbSchema.TBL_PHOTOS);
                // limit query to one row at most:
                builder.appendWhere(BarometerDataContract.Photos._ID +
                        " = " +
                        uri.getLastPathSegment());
                break;
            case ENTITY_LIST:
                builder.setTables(DbSchema.LEFT_OUTER_JOIN_STATEMENT);
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = BarometerDataContract.ItemEntities.SORT_ORDER_DEFAULT;
                }
                useAuthorityUri = true;
                break;
            case ENTITY_ID:
                builder.setTables(DbSchema.LEFT_OUTER_JOIN_STATEMENT);
                // limit query to one row at most:
                builder.appendWhere(DbSchema.TBL_ITEMS +
                        "." +
                        BarometerDataContract.Items._ID +
                        " = " +
                        uri.getLastPathSegment());
                useAuthorityUri = true;
                break;
            default:
                throw new IllegalArgumentException(
                        "Unsupported URI: " + uri);
        }
        Cursor cursor =
                builder.query(
                        db,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
        // if we want to be notified of any changes:
        if (useAuthorityUri) {
            cursor.setNotificationUri(
                    getContext().getContentResolver(),
                    BarometerDataContract.CONTENT_URI);
        } else {
            cursor.setNotificationUri(
                    getContext().getContentResolver(),
                    uri);
        }
        return cursor;
    }

    @Override
    public int update(
            @NonNull Uri uri,
            ContentValues values,
            String selection,
            String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
