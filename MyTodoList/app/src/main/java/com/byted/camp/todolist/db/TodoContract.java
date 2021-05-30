package com.byted.camp.todolist.db;

import android.provider.BaseColumns;

/**
 * Created on 2019/1/22.
 *
 * @author xuyingyi@bytedance.com (Yingyi Xu)
 */
public final class TodoContract {

    // TODO 定义表结构和 SQL 语句常量

    private TodoContract() {
    }

    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "todo_note";
        //        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_state = "state";
        public static final String COLUMN_NAME_CONTENT = "content";
        public static final String COLUMN_NAME_PRIORITY = "priority";
    }

    public static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + FeedEntry.TABLE_NAME + "("
            + FeedEntry._ID + " INTEGER PRIMARY KEY,"
            + FeedEntry.COLUMN_NAME_DATE + " TEXT,"
            + FeedEntry.COLUMN_NAME_state + " INTEGER,"
            + FeedEntry.COLUMN_NAME_CONTENT + " TEXT,"
            + FeedEntry.COLUMN_NAME_PRIORITY + " INTEGER)";

    public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;
}
