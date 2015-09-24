package nandha.com.todo.realm.android;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

import nandha.com.todo.realm.model.Task;

/**
 * Created by nandha on 23/09/15.
 */
public class App extends Application {

    public static List<Task> tasks = new ArrayList<>();

    public final static String INTENT_KEY_POSITION = "position";

    public final static String DATE_FORMAT = "dd/MMM/yy";

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
