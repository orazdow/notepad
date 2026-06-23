package com.clemm.notepad;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

class NoteListAdapter extends ArrayAdapter<Note> {

    private final List<Note> notes;
    private final Activity activity;

    private static class ViewHolder {
        public final TextView titleView;
        public final TextView dateView;

        public ViewHolder(View view) {
            this.titleView = (TextView) view.findViewById(R.id.titleView);
            this.dateView = (TextView) view.findViewById(R.id.dateView);
        }
    }

    public NoteListAdapter(Activity activity, List<Note> notes) {
        super(activity, R.layout.note_layout, notes);
        this.notes = notes;
        this.activity = activity;
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public int getCount() {
        return this.notes.size();
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public long getItemId(int i) {
        return this.notes.get(i).getId();
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = this.activity.getLayoutInflater().inflate(R.layout.note_layout, viewGroup, false);
            view.setTag(new ViewHolder(view));
        }
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.titleView.setText(this.notes.get(i).getTitle());
        viewHolder.dateView.setText(this.notes.get(i).getDate());
        return view;
    }
}



