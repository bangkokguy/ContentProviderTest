package bangkokguy.development.android.contentprovidertest;

import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Sets the columns to retrieve for the user profile
        String[] mProjection = new String[]
                {
                        BarometerDataContract.Items._ID,
                        BarometerDataContract.Items.TIME,
                        BarometerDataContract.Items.VALUE
                };

// Retrieves the profile from the Contacts Provider
        Uri uri = Uri.withAppendedPath(BarometerDataContract.CONTENT_URI, "items");

        Cursor mProfileCursor =
                getContentResolver().query(
                        uri,
                        mProjection,
                        null,
                        null,
                        null);
    }
}
