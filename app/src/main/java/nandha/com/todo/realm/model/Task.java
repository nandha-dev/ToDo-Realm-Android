package nandha.com.todo.realm.model;

import java.util.Date;

import io.realm.annotations.PrimaryKey;

/**
 * Created by nandha on 22/09/15.
 */
public class Task {

    @PrimaryKey
    private long id;

    private String name;
    private Date date;

    private boolean done;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
