package nandha.com.todo.realm.android;

import android.app.Application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;
import nandha.com.todo.realm.model.Task;

/**
 * Created by nandha on 23/09/15.
 */
public class App extends Application {

    private static List<Task> tasks;

    public final static String INTENT_KEY_POSITION = "position";
    public final static String DATE_FORMAT = "dd/MMM/yy";

    private static App app;

    // Realm
    private Realm realm;

    public static App getInstance() {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        realm = Realm.getInstance(this);
        tasks = new ArrayList<>();

        RealmResults<Task> tasksResult = realm.where(Task.class).findAll();

        for (Task task : tasksResult)
            tasks.add(task);

    }

    protected Task getTask(int position) {
        return tasks.get(position);
    }

    protected void addTask(Task task) {
        realm.beginTransaction();
        Task newTask = realm.createObject(Task.class);
        newTask.setName(task.getName());
        newTask.setDate(task.getDate());
        newTask.setId(UUID.randomUUID().toString());
        realm.commitTransaction();

        tasks.add(newTask);
        Collections.sort(tasks, new TaskCompare());
    }

    protected void updateTask(int position, Task task) {
        Task newTask = realm.where(Task.class).equalTo("id", task.getId()).findFirst();

        realm.beginTransaction();
        newTask.setDate(task.getDate());
        newTask.setDone(task.isDone());
        newTask.setName(task.getName());
        realm.commitTransaction();

        tasks.set(position, task);
        Collections.sort(tasks, new TaskCompare());
    }

    protected void removeTask(int position) {
        Task newTask = realm.where(Task.class).equalTo("id", getTask(position).getId()).findFirst();

        realm.beginTransaction();
        newTask.removeFromRealm();
        realm.commitTransaction();

        tasks.remove(position);
        Collections.sort(tasks, new TaskCompare());
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
