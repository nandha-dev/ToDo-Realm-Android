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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import nandha.com.todo.realm.R;
import nandha.com.todo.realm.model.Task;

public class TasksActivity extends AppCompatActivity {

    // Variables
    private Context mContext;
    private static final int ADD_TASK_REQUEST_CODE = 1000;
    private static final int EDIT_TASK_REQUEST_CODE = 1001;

    // Views
    private FloatingActionButton addFloatingActionButton;
    private ListView taskListView;
    private TaskAdapter taskAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("TODo List");
        setSupportActionBar(toolbar);

        // Variables
        mContext = this;

        // Views
        addFloatingActionButton = (FloatingActionButton) findViewById(R.id.activity_tasks_fab_add);
        taskListView = (ListView) findViewById(R.id.activity_tasks_ll_task);

        // List view adapter
        taskAdapter = new TaskAdapter(mContext);
        Collections.sort(App.tasks, new TaskCompare());
        taskListView.setAdapter(taskAdapter);

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
            Collections.sort(App.tasks, new TaskCompare());
            taskAdapter.notifyDataSetChanged();
            Snackbar.make(findViewById(R.id.rootLayout), "New Task added", Snackbar.LENGTH_SHORT)
                    .setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            App.tasks.remove(App.tasks.size() - 1);
                            taskAdapter.notifyDataSetChanged();
                        }
                    })
                    .show();
        } else if (requestCode == EDIT_TASK_REQUEST_CODE && resultCode == RESULT_OK) {
            Collections.sort(App.tasks, new TaskCompare());
            taskAdapter.notifyDataSetChanged();
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

            SimpleDateFormat sdf = new SimpleDateFormat(App.DATE_FORMAT);
            Date date = ((Task) getItem(position)).getDate();
            if (sdf.format(date).equals(sdf.format(getDate(0))))
                dateTextView.setText("Today");
            else if (sdf.format(date).equals(sdf.format(getDate(-1))))
                dateTextView.setText("Yesterday");
            else if (sdf.format(date).equals(sdf.format(getDate(1))))
                dateTextView.setText("Tomorrow");
            else
                dateTextView.setText(sdf.format(((Task) getItem(position)).getDate()));

            convertView.findViewById(R.id.row_task_list_ll_detail).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, AddTaskActivity.class);
                    intent.putExtra(App.INTENT_KEY_POSITION, position);
                    startActivityForResult(intent, EDIT_TASK_REQUEST_CODE);
                }
            });

            doneCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    App.tasks.get(position).setDone(isChecked);
                }
            });
            deleteImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (App.tasks.get(position).isDone()) {
                        App.tasks.remove(position);
                        TaskAdapter.this.notifyDataSetChanged();
                        return;
                    }
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
                    alertDialog.setTitle("Confirm Delete?");
                    alertDialog.setMessage("Do you want to delete the task you created?");
                    alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            App.tasks.remove(position);
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
            return App.tasks.get(position);
        }

        @Override
        public int getCount() {
            return App.tasks.size();
        }
    }

    private class TaskCompare implements Comparator<Task> {
        @Override
        public int compare(Task o1, Task o2) {
            return o1.getDate().compareTo(o2.getDate());
        }
    }
}
