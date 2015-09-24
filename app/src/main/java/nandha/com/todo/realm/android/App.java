package nandha.com.todo.realm.android;

import android.app.Application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import nandha.com.todo.realm.model.Task;

/**
 * Created by nandha on 23/09/15.
 */
public class App extends Application {

    private static List<Task> tasks = new ArrayList<>();

    public final static String INTENT_KEY_POSITION = "position";
    public final static String DATE_FORMAT = "dd/MMM/yy";

    private static App app = new App();

    private App() {
    }

    public static App getInstance() {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    protected void addTask(Task task) {
        tasks.add(task);
        Collections.sort(tasks, new TaskCompare());
    }

    protected void updateTask(int position, Task task) {
        tasks.set(position, task);
        Collections.sort(tasks, new TaskCompare());
    }

    protected void removeTask(int position) {
        tasks.remove(position);
        Collections.sort(tasks, new TaskCompare());
    }

    protected Task getTask(int position) {
        return tasks.get(position);
    }

    protected int getTaskCount() {
        return tasks.size();
    }

    private class TaskCompare implements Comparator<Task> {
        @Override
        public int compare(Task o1, Task o2) {
            return o1.getDate().compareTo(o2.getDate());
        }
    }
}
