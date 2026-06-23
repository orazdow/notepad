package com.clemm.notepad;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class NotepadNotesActivity extends AppCompatActivity {

    private static final int REQUEST_SETTINGS = 0;
    private static final int REQUEST_EXPORT_NOTE = 2;

    private NoteRepository noteRepository;

    Parcelable listViewState;

    private long pendingExportNoteId = -1;

    class EmptyCancelClickListener implements DialogInterface.OnClickListener {
        EmptyCancelClickListener(NotepadNotesActivity notepadNotesActivity) {
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
        }
    }

    class DeleteAfterPasswordClickListener implements DialogInterface.OnClickListener {

        final Note note;

        final EditText passwordEditText;

        final long noteId;

        DeleteAfterPasswordClickListener(Note note, EditText editText, long j) {
            this.note = note;
            this.passwordEditText = editText;
            this.noteId = j;
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
            if (this.note.getPassword().equals(this.passwordEditText.getText().toString())) {
                NotepadNotesActivity.this.confirmDeleteNote(this.noteId);
            } else {
                Toast.makeText(NotepadNotesActivity.this.getApplicationContext(), R.string.incorrect_password, 0).show();
            }
        }
    }

    class CancelDeletePasswordClickListener implements DialogInterface.OnClickListener {
        CancelDeletePasswordClickListener(NotepadNotesActivity notepadNotesActivity) {
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
        }
    }

    class DeleteConfirmedClickListener implements DialogInterface.OnClickListener {

        final long noteId;

        DeleteConfirmedClickListener(long j) {
            this.noteId = j;
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
            NotepadNotesActivity.this.noteRepository.deleteNote(this.noteId);
            NotepadNotesActivity.this.refreshNoteList();
        }
    }

    class CancelDeleteClickListener implements DialogInterface.OnClickListener {
        CancelDeleteClickListener(NotepadNotesActivity notepadNotesActivity) {
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
        }
    }

    class ShareAfterPasswordClickListener implements DialogInterface.OnClickListener {

        final Note note;

        final EditText passwordEditText;

        final long noteId;

        ShareAfterPasswordClickListener(Note note, EditText editText, long j) {
            this.note = note;
            this.passwordEditText = editText;
            this.noteId = j;
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
            if (this.note.getPassword().equals(this.passwordEditText.getText().toString())) {
                NotepadNotesActivity.this.shareNote(this.noteId);
            } else {
                Toast.makeText(NotepadNotesActivity.this.getApplicationContext(), R.string.incorrect_password, 0).show();
            }
        }
    }

    class CancelSharePasswordClickListener implements DialogInterface.OnClickListener {
        CancelSharePasswordClickListener(NotepadNotesActivity notepadNotesActivity) {
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
        }
    }

    class ExportAfterPasswordClickListener implements DialogInterface.OnClickListener {

        final Note note;

        final EditText passwordEditText;

        final long noteId;

        ExportAfterPasswordClickListener(Note note, EditText editText, long j) {
            this.note = note;
            this.passwordEditText = editText;
            this.noteId = j;
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
            if (this.note.getPassword().equals(this.passwordEditText.getText().toString())) {
                NotepadNotesActivity.this.exportNote(this.noteId);
            } else {
                Toast.makeText(NotepadNotesActivity.this.getApplicationContext(), R.string.incorrect_password, 0).show();
            }
        }
    }

    class CancelExportPasswordClickListener implements DialogInterface.OnClickListener {
        CancelExportPasswordClickListener(NotepadNotesActivity notepadNotesActivity) {
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
        }
    }

    class NoteActionClickListener implements DialogInterface.OnClickListener {

        final long noteId;

        NoteActionClickListener(long j) {
            this.noteId = j;
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
            if (i == 0) {
                NotepadNotesActivity.this.openNoteWithUnlock(this.noteId);
                return;
            }
            if (i == 1) {
                NotepadNotesActivity.this.confirmDeleteWithUnlock(this.noteId);
                return;
            }
            if (i == 2) {
                NotepadNotesActivity.this.shareWithUnlock(this.noteId);
            } else if (i == 3) {
                NotepadNotesActivity.this.exportWithUnlock(this.noteId);
            } else {
                if (i != 4) {
                    return;
                }
                NotepadNotesActivity.this.showLockDialog(this.noteId);
            }
        }
    }

    class NoteClickListener implements AdapterView.OnItemClickListener {
        NoteClickListener() {
        }

        @Override // android.widget.AdapterView.OnItemClickListener
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            NotepadNotesActivity.this.openNoteWithUnlock(j);
        }
    }

    class UnlockNoteClickListener implements DialogInterface.OnClickListener {

        final Note note;

        final EditText passwordEditText;

        UnlockNoteClickListener(Note note, EditText editText) {
            this.note = note;
            this.passwordEditText = editText;
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
            Context applicationContext;
            int i2;
            if (this.note.getPassword().equals(this.passwordEditText.getText().toString())) {
                this.note.setPassword("");
                NotepadNotesActivity.this.noteRepository.updateNote(this.note);
                applicationContext = NotepadNotesActivity.this.getApplicationContext();
                i2 = R.string.unlocked;
            } else {
                applicationContext = NotepadNotesActivity.this.getApplicationContext();
                i2 = R.string.incorrect_password;
            }
            Toast.makeText(applicationContext, i2, 0).show();
        }
    }

    class LockNoteClickListener implements DialogInterface.OnClickListener {

        final Note note;

        final EditText passwordEditText;

        LockNoteClickListener(Note note, EditText editText) {
            this.note = note;
            this.passwordEditText = editText;
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
            this.note.setPassword(this.passwordEditText.getText().toString());
            NotepadNotesActivity.this.noteRepository.updateNote(this.note);
            Toast.makeText(NotepadNotesActivity.this.getApplicationContext(), R.string.locked, 0).show();
        }
    }

    class DismissDialogClickListener implements DialogInterface.OnClickListener {
        DismissDialogClickListener(NotepadNotesActivity notepadNotesActivity) {
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
        }
    }

    class SortModeClickListener implements DialogInterface.OnClickListener {
        SortModeClickListener() {
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
            SharedPreferences.Editor editorEdit = PreferenceManager.getDefaultSharedPreferences(NotepadNotesActivity.this.getApplicationContext()).edit();
            editorEdit.putInt("sort_mode", i);
            editorEdit.apply();
            NotepadNotesActivity.this.refreshNoteList();
        }
    }

    class NoteLongClickListener implements AdapterView.OnItemLongClickListener {
        NoteLongClickListener() {
        }

        @Override // android.widget.AdapterView.OnItemLongClickListener
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long j) {
            NotepadNotesActivity.this.showNoteActions(j);
            return true;
        }
    }

    class OpenAfterPasswordClickListener implements DialogInterface.OnClickListener {

        final Note note;

        final EditText passwordEditText;

        final long noteId;

        OpenAfterPasswordClickListener(Note note, EditText editText, long j) {
            this.note = note;
            this.passwordEditText = editText;
            this.noteId = j;
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
            if (this.note.getPassword().equals(this.passwordEditText.getText().toString())) {
                NotepadNotesActivity.this.openNoteEditor(this.noteId);
            } else {
                Toast.makeText(NotepadNotesActivity.this.getApplicationContext(), R.string.incorrect_password, 0).show();
            }
        }
    }

    public void confirmDeleteWithUnlock(long j) {
        Note selectedNote = this.noteRepository.getNote(j);
        if (!selectedNote.isLocked()) {
            confirmDeleteNote(j);
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.enter_password);
        EditText editText = new EditText(this);
        editText.setGravity(17);
        builder.setView(editText);
        builder.setCancelable(true);
        builder.setPositiveButton(R.string.unlock, new DeleteAfterPasswordClickListener(selectedNote, editText, j));
        builder.setNegativeButton(android.R.string.cancel, new CancelDeletePasswordClickListener(this));
        builder.show();
    }

    public void exportWithUnlock(long j) {
        Note selectedNote = this.noteRepository.getNote(j);
        if (!selectedNote.isLocked()) {
            exportNote(j);
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.enter_password);
        EditText editText = new EditText(this);
        editText.setGravity(17);
        builder.setView(editText);
        builder.setCancelable(true);
        builder.setPositiveButton(R.string.unlock, new ExportAfterPasswordClickListener(selectedNote, editText, j));
        builder.setNegativeButton(android.R.string.cancel, new CancelExportPasswordClickListener(this));
        builder.show();
    }

    public void showLockDialog(long j) {
        int i;
        DialogInterface.OnClickListener lockNoteClickListener;
        Note selectedNote = this.noteRepository.getNote(j);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        EditText editText = new EditText(this);
        editText.setGravity(17);
        builder.setView(editText);
        builder.setCancelable(true);
        if (selectedNote.isLocked()) {
            builder.setTitle(R.string.unlock_by_password);
            i = R.string.unlock;
            lockNoteClickListener = new UnlockNoteClickListener(selectedNote, editText);
        } else {
            builder.setTitle(R.string.lock_with_password);
            i = R.string.lock;
            lockNoteClickListener = new LockNoteClickListener(selectedNote, editText);
        }
        builder.setPositiveButton(i, lockNoteClickListener);
        builder.show();
    }

    public void showNoteActions(long j) {
        Resources resources;
        int i;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Note selectedNote = this.noteRepository.getNote(j);
        ArrayList arrayList = new ArrayList();
        arrayList.add(getResources().getString(R.string.edit));
        arrayList.add(getResources().getString(R.string.delete));
        arrayList.add(getResources().getString(R.string.share));
        arrayList.add(getResources().getString(R.string.export));
        if (selectedNote.isLocked()) {
            resources = getResources();
            i = R.string.unlock_by_password;
        } else {
            resources = getResources();
            i = R.string.lock_with_password;
        }
        arrayList.add(resources.getString(i));
        builder.setAdapter(new ArrayAdapter(this, R.layout.menu_item, R.id.menuItem, arrayList), new NoteActionClickListener(j));
        builder.setCancelable(true);
        builder.show();
    }

    public void openNoteWithUnlock(long j) {
        Note selectedNote = this.noteRepository.getNote(j);
        if (!selectedNote.isLocked()) {
            openNoteEditor(j);
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.enter_password);
        EditText editText = new EditText(this);
        editText.setGravity(17);
        builder.setView(editText);
        builder.setCancelable(true);
        builder.setPositiveButton(R.string.unlock, new OpenAfterPasswordClickListener(selectedNote, editText, j));
        builder.setNegativeButton(android.R.string.cancel, new EmptyCancelClickListener(this));
        builder.show();
    }

    public void shareWithUnlock(long j) {
        Note selectedNote = this.noteRepository.getNote(j);
        if (!selectedNote.isLocked()) {
            shareNote(j);
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.enter_password);
        EditText editText = new EditText(this);
        editText.setGravity(17);
        builder.setView(editText);
        builder.setCancelable(true);
        builder.setPositiveButton(R.string.unlock, new ShareAfterPasswordClickListener(selectedNote, editText, j));
        builder.setNegativeButton(android.R.string.cancel, new CancelSharePasswordClickListener(this));
        builder.show();
    }

    public void confirmDeleteNote(long j) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.delete_a_note);
        builder.setMessage(R.string.sure_to_delete);
        builder.setPositiveButton(android.R.string.yes, new DeleteConfirmedClickListener(j));
        builder.setNegativeButton(android.R.string.no, new CancelDeleteClickListener(this));
        builder.show();
    }

    public void exportNote(long j) {
        Note selectedNote = this.noteRepository.getNote(j);
        this.pendingExportNoteId = j;

        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, buildExportFileName(selectedNote));
        startActivityForResult(intent, REQUEST_EXPORT_NOTE);
    }

    public void openNoteEditor(long j) {
        Intent intent = new Intent(this, (Class<?>) NoteActivity.class);
        intent.putExtra("NOTE_ID", j);
        startActivity(intent);
    }

    public void shareNote(long j) {
        ShareHelper.shareText(this, this.noteRepository.getNote(j).toString());
    }

    private void showAboutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setMessage(R.string.about_app);
        builder.setNeutralButton(android.R.string.ok, new DismissDialogClickListener(this));
        builder.show();
    }

    private void createNote() {
        openNoteEditor(-1L);
    }

    private void openSettings() {
        startActivityForResult(new Intent(this, (Class<?>) SettingsActivity.class), REQUEST_SETTINGS);
    }

    private void showSortDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        ArrayList arrayList = new ArrayList();
        arrayList.add(getResources().getString(R.string.by_title));
        arrayList.add(getResources().getString(R.string.by_create_date));
        arrayList.add(getResources().getString(R.string.by_change_date));
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.select_dialog_singlechoice, arrayList);
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        builder.setTitle(R.string.sorting);
        builder.setSingleChoiceItems(arrayAdapter, defaultSharedPreferences.getInt("sort_mode", 1), new SortModeClickListener());
        builder.setCancelable(true);
        builder.show();
    }

    public void refreshNoteList() {
        ((ListView) findViewById(R.id.notesListView)).setAdapter((ListAdapter) new NoteListAdapter(this, this.noteRepository.getNotes(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt("sort_mode", 1))));
    }

    private void applySelectedTheme() {
        setTheme(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("pref_key_use_black_theme", false) ? R.style.DarkTheme : R.style.LightTheme);
    }

    @Override
    protected void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == REQUEST_EXPORT_NOTE) {
            if (i2 == RESULT_OK && intent != null && intent.getData() != null) {
                writeNoteToUri(intent.getData());
            }
            return;
        }
        if (i == REQUEST_SETTINGS) {
            finish();
            startActivity(new Intent(this, (Class<?>) NotepadNotesActivity.class));
        }
    }

    @Override
    protected void onCreate(Bundle bundle) {
        applySelectedTheme();
        super.onCreate(bundle);
        setContentView(R.layout.activity_notepad_notes);
        this.noteRepository = new NoteRepository(this);
        refreshNoteList();
        ((ListView) findViewById(R.id.notesListView)).setOnItemClickListener(new NoteClickListener());
        ((ListView) findViewById(R.id.notesListView)).setOnItemLongClickListener(new NoteLongClickListener());
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notepad_notes, menu);
        return true;
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.about) {
            showAboutDialog();
        } else if (itemId == R.id.add_note) {
            createNote();
        } else if (itemId == R.id.preferences) {
            openSettings();
        } else if (itemId == R.id.sorting) {
            showSortDialog();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    protected void onPause() {
        this.listViewState = ((ListView) findViewById(R.id.notesListView)).onSaveInstanceState();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshNoteList();
        if (this.listViewState != null) {
            ((ListView) findViewById(R.id.notesListView)).onRestoreInstanceState(this.listViewState);
        }
    }

    private String buildExportFileName(Note note) {
        String title = note.getTitle() == null ? "" : note.getTitle().replaceAll("\\p{Punct}", "").trim();
        if (title.isEmpty()) {
            title = "note-" + note.getId();
        }
        return title + ".txt";
    }

    private void writeNoteToUri(Uri uri) {
        Note note = this.noteRepository.getNote(this.pendingExportNoteId);
        if (note == null) {
            return;
        }
        try (OutputStream outputStream = getContentResolver().openOutputStream(uri)) {
            if (outputStream == null) {
                throw new IOException("Unable to open export destination");
            }
            outputStream.write(note.getContent().getBytes(StandardCharsets.UTF_8));
            Toast.makeText(this, R.string.saved, 1).show();
        } catch (IOException e) {
            Toast.makeText(this, e.getLocalizedMessage(), 0).show();
        }
    }
}







