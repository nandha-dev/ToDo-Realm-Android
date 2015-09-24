package nandha.com.todo.realm.android;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import nandha.com.todo.realm.R;
import nandha.com.todo.realm.model.Task;

public class AddTaskActivity extends AppCompatActivity {

    // Variables
    private Context mContext;
    private App app;

    private EditText mTaskNameEditText;
    private EditText mTaskDateEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Add Task");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Variables
        mContext = this;
        app = App.getInstance();
        final int position = getIntent().getIntExtra(App.INTENT_KEY_POSITION, -1);

        // Views
        Button mAddButton = (Button) findViewById(R.id.activity_add_task_bt_add);
        mTaskNameEditText = (EditText) findViewById(R.id.activity_add_task_et_name);
        mTaskDateEditText = (EditText) findViewById(R.id.activity_add_task_et_date);

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (position == -1)
                    addTask();
                else
                    editTask(position);
            }
        });
        mTaskDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePicker(view);
            }
        });
        mTaskDateEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b)
                    datePicker(view);
            }
        });

        if (position != -1) {
            fillTask(app.getTask(position));
            mAddButton.setText("EDIT");
        }
    }

    private void fillTask(Task task) {
        mTaskNameEditText.setText(task.getName());
        SimpleDateFormat sdf = new SimpleDateFormat(App.DATE_FORMAT);
        mTaskDateEditText.setText(sdf.format(task.getDate()));
    }

    private void addTask() {
        String taskName = mTaskNameEditText.getText().toString();
        String taskDate = mTaskDateEditText.getText().toString();
        if (taskName.length() == 0 || taskName.equals("")) {
            mTaskNameEditText.setError("Should be filled");
            return;
        }
        if (taskDate.length() == 0 || taskDate.equals("")) {
            mTaskDateEditText.setError("Should be filled");
            return;
        }
        Task task = getTask(taskName, taskDate);
        if (task == null) {
            mTaskDateEditText.setError("Not a correct date");
            return;
        }
        task.setDone(false);
        app.addTask(task);
        setResult(RESULT_OK);
        finish();
    }

    private void editTask(int position) {
        String taskName = mTaskNameEditText.getText().toString();
        String taskDate = mTaskDateEditText.getText().toString();
        if (taskName.length() == 0 || taskName.equals("")) {
            mTaskNameEditText.setError("Should be filled");
            return;
        }
        if (taskDate.length() == 0 || taskDate.equals("")) {
            mTaskDateEditText.setError("Should be filled");
            return;
        }
        Task task = getTask(taskName, taskDate);
        if (task == null) {
            mTaskDateEditText.setError("Not a correct date");
            return;
        }
        task.setId(app.getTask(position).getId());
        app.updateTask(position, task);
        setResult(RESULT_OK);
        finish();
    }


    private void datePicker(final View view) {
        Calendar currentTime = Calendar.getInstance();
        int year = currentTime.get(Calendar.YEAR);
        int monthOfYear = currentTime.get(Calendar.MONTH);
        int dayOfMonth = currentTime.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog mDatePickerDialog = new DatePickerDialog(mContext, 0, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, monthOfYear, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat(App.DATE_FORMAT);
                ((EditText) view).setText(sdf.format(calendar.getTime()));
            }
        }, year, monthOfYear, dayOfMonth);

        mDatePickerDialog.setTitle("Select Time");
        mDatePickerDialog.show();
    }

    private Task getTask(String name, String dateString) {
        Task task = new Task();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(App.DATE_FORMAT);
            task.setName(name);
            task.setDate(sdf.parse(dateString));
            task.setDone(false);
            return task;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

}
