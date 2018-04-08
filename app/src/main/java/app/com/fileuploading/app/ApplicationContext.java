package app.com.fileuploading.app;


import com.google.firebase.database.FirebaseDatabase;

import android.app.Application;
import android.content.Context;

public class ApplicationContext extends Application {

    public Context mContext;
    private static FirebaseDatabase database;


    public ApplicationContext() {
        super();
    }

    public ApplicationContext(Context mContext) {
        this.mContext = mContext;
        database = FirebaseDatabase.getInstance();
    }

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public static FirebaseDatabase getDatabase() {
        return database;
    }

}
