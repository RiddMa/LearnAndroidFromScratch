package com.byted.camp.todolist.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.byted.camp.todolist.NoteOperator;
import com.byted.camp.todolist.R;
import com.byted.camp.todolist.beans.Note;
import com.byted.camp.todolist.operation.activity.SettingActivity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created on 2019/1/23.
 *
 * @author xuyingyi@bytedance.com (Yingyi Xu)
 */
public class NoteListAdapter extends RecyclerView.Adapter<NoteViewHolder> {

    private final NoteOperator operator;
    private final List<Note> notes = new ArrayList<>();

    public Context mContext;

    public NoteListAdapter(NoteOperator operator) {
        this.operator = operator;
    }

    public void refresh(List<Note> newNotes) {
        notes.clear();
        if (newNotes != null) {

            //todo 根据${com.byted.camp.todolist.operation.activity.SettingActivity} 中设置的sp控制是否将已完成的完成排到最后，默认不排序
            SharedPreferences sp = mContext.getSharedPreferences("user", Context.MODE_PRIVATE);
            boolean flag = sp.getBoolean(SettingActivity.KEY_IS_NEED_SORT, false);
            if (flag) {
                Collections.sort(newNotes, new Comparator<Note>() {
                    @Override
                    public int compare(Note o1, Note o2) {
                        int val1 = o1.getState().intValue - o2.getState().intValue;
                        int val2 = Long.valueOf(o1.getId()-o2.getId()).intValue();
                        if (val1 == 0) return val2;
                        else return val1;
                    }
                });
            }
            notes.addAll(newNotes);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int pos) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(itemView, operator);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int pos) {
        holder.bind(notes.get(pos));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }
}
