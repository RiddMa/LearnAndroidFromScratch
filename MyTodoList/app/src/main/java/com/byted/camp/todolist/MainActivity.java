package com.byted.camp.todolist;

import android.app.Activity;
import android.content.ContentValues;
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
import com.byted.camp.todolist.ui.NoteListAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD = 1002;

    private RecyclerView recyclerView;
    private NoteListAdapter notesAdapter;
    private TodoDbHelper todoDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        todoDbHelper = new TodoDbHelper(getApplicationContext());
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
//                notesAdapter.refresh(loadNotesFromDatabase());
            }
        });
        notesAdapter.mContext = getBaseContext();
        recyclerView.setAdapter(notesAdapter);

        notesAdapter.refresh(loadNotesFromDatabase());
    }

    @Override
    protected void onDestroy() {
        todoDbHelper.close();
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
        SQLiteDatabase db = todoDbHelper.getReadableDatabase();

        List<Note> list = new ArrayList<>();
        String[] projection = {
                BaseColumns._ID,
                TodoContract.FeedEntry.COLUMN_NAME_PRIORITY,
                TodoContract.FeedEntry.COLUMN_NAME_CONTENT,
                TodoContract.FeedEntry.COLUMN_NAME_state,
                TodoContract.FeedEntry.COLUMN_NAME_DATE
        };

        SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss");// a为am/pm的标记

        Cursor cursor = db.query(TodoContract.FeedEntry.TABLE_NAME,null,null,null,null,null,null);
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                long id = Long.parseLong(cursor.getString(cursor.getColumnIndex(TodoContract.FeedEntry._ID)));
                String date_string = cursor.getString(cursor.getColumnIndex(TodoContract.FeedEntry.COLUMN_NAME_DATE));
                Date date = null;
                try {
                    date = sdf.parse(date_string);
                } catch (ParseException e) {
                    date = new Date();
                }
                int state_int = cursor.getInt(cursor.getColumnIndex(TodoContract.FeedEntry.COLUMN_NAME_state));
                State state = State.from(state_int);
                String content = cursor.getString(cursor.getColumnIndex(TodoContract.FeedEntry.COLUMN_NAME_CONTENT));
                Integer priority = cursor.getInt(cursor.getColumnIndex(TodoContract.FeedEntry.COLUMN_NAME_PRIORITY));
                Note note = new Note(id,date,state,content,priority);
                list.add(note);
                cursor.moveToNext();
            }
        }
        return list;
    }

    private void deleteNote(Note note) {
        // TODO 删除数据
        SQLiteDatabase db = todoDbHelper.getWritableDatabase();

        /*String selection = TodoContract.FeedEntry.COLUMN_NAME_CONTENT + " LIKE ?";
        String[] selectionArgs = {note.getContent()};*/
        String selection = TodoContract.FeedEntry._ID + " LIKE ?";
        String[] selectionArgs = {String.valueOf(note.getId())};
        db.delete(TodoContract.FeedEntry.TABLE_NAME,selection,selectionArgs);

        notesAdapter.refresh(loadNotesFromDatabase());
    }

    private void updateNode(Note note) {
        SQLiteDatabase db = todoDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TodoContract.FeedEntry.COLUMN_NAME_state, note.getState().intValue);

        String selection = TodoContract.FeedEntry._ID + " LIKE ?";
        String[] selectionArgs = {String.valueOf(note.getId())};

        db.update(
                TodoContract.FeedEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );
        notesAdapter.refresh(loadNotesFromDatabase());
        // 更新数据
    }

}
