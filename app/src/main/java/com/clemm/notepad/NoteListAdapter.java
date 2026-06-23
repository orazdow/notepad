package com.clemm.notepad;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import java.util.List;
import java.util.Set;

class NoteListAdapter extends ArrayAdapter<Note> {

    private final List<Note> notes;
    private final Activity activity;
    private final boolean selectionMode;
    private final Set<Long> selectedNoteIds;

    private static class ViewHolder {
        public final CheckBox selectedCheckBox;
        public final TextView titleView;
        public final TextView dateView;

        public ViewHolder(View view) {
            this.selectedCheckBox = (CheckBox) view.findViewById(R.id.selectedCheckBox);
            this.titleView = (TextView) view.findViewById(R.id.titleView);
            this.dateView = (TextView) view.findViewById(R.id.dateView);
        }
    }

    public NoteListAdapter(Activity activity, List<Note> notes, boolean selectionMode, Set<Long> selectedNoteIds) {
        super(activity, R.layout.note_layout, notes);
        this.notes = notes;
        this.activity = activity;
        this.selectionMode = selectionMode;
        this.selectedNoteIds = selectedNoteIds;
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
        Note note = this.notes.get(i);
        viewHolder.titleView.setText(note.getTitle());
        viewHolder.dateView.setText(note.getDate());
        viewHolder.selectedCheckBox.setVisibility(this.selectionMode ? View.VISIBLE : View.GONE);
        viewHolder.selectedCheckBox.setChecked(this.selectedNoteIds.contains(note.getId()));
        return view;
    }
}



