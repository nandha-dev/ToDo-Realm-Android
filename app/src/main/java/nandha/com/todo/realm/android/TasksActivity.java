package nandha.com.todo.realm.android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import nandha.com.todo.realm.R;
import nandha.com.todo.realm.model.Task;

public class TasksActivity extends AppCompatActivity {

    // Variables
    private Context mContext;
    private App app;
    private static final int ADD_TASK_REQUEST_CODE = 1000;
    private static final int EDIT_TASK_REQUEST_CODE = 1001;

    private TaskAdapter mTaskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("ToDo List");
        setSupportActionBar(toolbar);

        // Variables
        mContext = this;
        app = App.getInstance();

        // Views
        FloatingActionButton addFloatingActionButton = (FloatingActionButton) findViewById(R.id.activity_tasks_fab_add);
        ListView taskListView = (ListView) findViewById(R.id.activity_tasks_ll_task);

        // List view adapter
        mTaskAdapter = new TaskAdapter(mContext);
        taskListView.setAdapter(mTaskAdapter);

        //Listeners
        addFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(mContext, AddTaskActivity.class), ADD_TASK_REQUEST_CODE);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_TASK_REQUEST_CODE && resultCode == RESULT_OK) {
            mTaskAdapter.notifyDataSetChanged();
            Snackbar.make(findViewById(R.id.rootLayout), "New Task added", Snackbar.LENGTH_SHORT)
                    .setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            app.removeTask(app.getTaskCount() - 1);
                            mTaskAdapter.notifyDataSetChanged();
                        }
                    })
                    .show();
        } else if (requestCode == EDIT_TASK_REQUEST_CODE && resultCode == RESULT_OK) {
            mTaskAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tasks, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private class TaskAdapter extends BaseAdapter {
        private Context mContext;

        public TaskAdapter(Context context) {
            this.mContext = context;
        }

        private Date getDate(int day) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, day);
            return calendar.getTime();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) this.mContext
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.row_task_list_layout, parent, false);
            }
            TextView taskTextView = (TextView) convertView.findViewById(R.id.row_task_list_tv_name);
            TextView dateTextView = (TextView) convertView.findViewById(R.id.row_task_list_tv_date);
            ImageView deleteImageView = (ImageView) convertView.findViewById(R.id.row_task_list_iv_delete);
            CheckBox doneCheckBox = (CheckBox) convertView.findViewById(R.id.row_task_list_cb_done);

            taskTextView.setText(((Task) getItem(position)).getName());
            doneCheckBox.setChecked(((Task) getItem(position)).isDone());

            SimpleDateFormat sdf = new SimpleDateFormat(App.DATE_FORMAT);
            Date date = ((Task) getItem(position)).getDate();
            if (sdf.format(date).equals(sdf.format(getDate(0))))
                dateTextView.setText("Today");
            else if (sdf.format(date).equals(sdf.format(getDate(-1))))
                dateTextView.setText("Yesterday");
            else if (sdf.format(date).equals(sdf.format(getDate(1))))
                dateTextView.setText("Tomorrow");
            else if (date.getTime() < getDate(6).getTime()) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                dateTextView.setText(DateFormat.format("EEEE", calendar.getTime()).toString());
            } else
                dateTextView.setText(sdf.format(((Task) getItem(position)).getDate()));

            convertView.findViewById(R.id.row_task_list_ll_detail).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, AddTaskActivity.class);
                    intent.putExtra(App.INTENT_KEY_POSITION, position);
                    startActivityForResult(intent, EDIT_TASK_REQUEST_CODE);
                }
            });


            doneCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Task task = new Task();
                    task.setId(app.getTask(position).getId());
                    task.setDate(app.getTask(position).getDate());
                    task.setName(app.getTask(position).getName());
                    task.setDone(((CheckBox) v).isChecked());
                    app.updateTask(position, task);
                }
            });
            deleteImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (app.getTask(position).isDone()) {
                        app.removeTask(position);
                        TaskAdapter.this.notifyDataSetChanged();
                        return;
                    }
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
                    alertDialog.setTitle("Confirm Delete?");
                    alertDialog.setMessage("Do you want to delete the task you created?");
                    alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            app.removeTask(position);
                            TaskAdapter.this.notifyDataSetChanged();
                        }
                    });
                    alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alertDialog.show();

                }
            });
            return convertView;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Object getItem(int position) {
            return app.getTask(position);
        }

        @Override
        public int getCount() {
            return app.getTaskCount();
        }
    }
}
