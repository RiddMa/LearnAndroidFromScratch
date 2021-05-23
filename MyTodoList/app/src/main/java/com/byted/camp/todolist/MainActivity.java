package com.byted.camp.todolist;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.byted.camp.todolist.beans.Note;
import com.byted.camp.todolist.beans.State;
import com.byted.camp.todolist.db.TodoContract;
import com.byted.camp.todolist.db.TodoDbHelper;
import com.byted.camp.todolist.operation.activity.DatabaseActivity;
import com.byted.camp.todolist.operation.activity.DebugActivity;
import com.byted.camp.todolist.operation.activity.SettingActivity;
import com.byted.camp.todolist.operation.db.FeedReaderContract;
import com.byted.camp.todolist.ui.NoteListAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE_ADD = 1002;

    private RecyclerView recyclerView;
    private NoteListAdapter notesAdapter;

    TodoDbHelper dbHelper = new TodoDbHelper(getApplicationContext());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                        new Intent(MainActivity.this, NoteActivity.class),
                        REQUEST_CODE_ADD);
            }
        });

        recyclerView = findViewById(R.id.list_todo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        notesAdapter = new NoteListAdapter(new NoteOperator() {
            @Override
            public void deleteNote(Note note) {
                MainActivity.this.deleteNote(note);
            }

            @Override
            public void updateNote(Note note) {
                MainActivity.this.updateNode(note);
            }
        });
        recyclerView.setAdapter(notesAdapter);

        notesAdapter.refresh(loadNotesFromDatabase());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingActivity.class));
                return true;
            case R.id.action_debug:
                startActivity(new Intent(this, DebugActivity.class));
                return true;
            case R.id.action_database:
                startActivity(new Intent(this, DatabaseActivity.class));
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD
                && resultCode == Activity.RESULT_OK) {
            notesAdapter.refresh(loadNotesFromDatabase());
        }
    }

    private List<Note> loadNotesFromDatabase() {
        // TODO 从数据库中查询数据，并转换成 JavaBeans
        List<Note> noteList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                TodoContract.TodoEntry.COLUMN_NAME_ID,
                TodoContract.TodoEntry.COLUMN_NAME_DATE,
                TodoContract.TodoEntry.COLUMN_NAME_STATE,
                TodoContract.TodoEntry.COLUMN_NAME_CONTENT,
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                TodoContract.TodoEntry.COLUMN_NAME_ID + " DESC";

        Cursor cursor = db.query(
                TodoContract.TodoEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );

        Log.i(TAG, "perfrom query data:");
        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(TodoContract.TodoEntry.COLUMN_NAME_ID));

            SimpleDateFormat sdf = new SimpleDateFormat();
            sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
            Date date = null;
            try {
                date = sdf.parse(cursor.getString(cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_NAME_DATE)));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            State state = State.from(cursor.getInt(cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_NAME_STATE)));

            String content = cursor.getString(cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_NAME_CONTENT));

            Note tmpNote = new Note(id);
            tmpNote.setState(state);
            tmpNote.setDate(date);
            tmpNote.setContent(content);

            noteList.add(tmpNote);

            Log.i(TAG, noteList.toString());
        }
        cursor.close();
        return noteList;
    }

    private void deleteNote(Note note) {
        // TODO 删除数据
    }

    private void updateNode(Note note) {
        // 更新数据
    }

}
