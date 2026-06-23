package com.clemm.notepad;

import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NoteActivity extends AppCompatActivity {

    private long noteId;

    private Note note;

    class SaveBeforeExitClickListener implements DialogInterface.OnClickListener {
        SaveBeforeExitClickListener() {
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
            if (i != -2) {
                if (i != -1) {
                    return;
                } else {
                    NoteActivity.this.saveNote();
                }
            }
            NoteActivity.this.finish();
        }
    }

    private boolean hasNoChanges() {
        return ((EditText) findViewById(R.id.titleEdit)).getText().toString().equals(this.note.getTitle()) && ((EditText) findViewById(R.id.contentEdit)).getText().toString().equals(this.note.getContent());
    }

    private void confirmSaveBeforeExit() {
        if (hasNoChanges()) {
            return;
        }
        SaveBeforeExitClickListener saveBeforeExitClickListener = new SaveBeforeExitClickListener();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.save_a_note);
        builder.setMessage(R.string.sure_to_save);
        builder.setPositiveButton(android.R.string.yes, saveBeforeExitClickListener);
        builder.setNegativeButton(android.R.string.no, saveBeforeExitClickListener);
        builder.show();
    }

    public void saveNote() {
        if (hasNoChanges()) {
            finish();
            return;
        }
        this.note.setDate(new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.ENGLISH).format(new Date()));
        String string = ((EditText) findViewById(R.id.titleEdit)).getText().toString();
        this.note.setTitle(string);
        String string2 = ((EditText) findViewById(R.id.contentEdit)).getText().toString();
        this.note.setContent(string2);
        if (string.equals("") && string2.equals("")) {
            Toast.makeText(this, R.string.empty_note, 0).show();
            return;
        }
        NoteRepository noteRepository = new NoteRepository(this);
        long j = this.noteId;
        if (j != -1) {
            this.note.setId(j);
            noteRepository.updateNote(this.note);
        } else {
            noteRepository.insertNote(this.note);
        }
        Toast.makeText(this, R.string.saved, 0).show();
        finish();
    }

    private void shareCurrentNote() {
        ShareHelper.shareText(this, ((EditText) findViewById(R.id.titleEdit)).getText().toString() + "\n\n" + ((EditText) findViewById(R.id.contentEdit)).getText().toString());
    }

    private void loadNote() {
        Note loadedNote = new NoteRepository(this).getNote(this.noteId);
        this.note = loadedNote;
        if (loadedNote != null) {
            ((EditText) findViewById(R.id.titleEdit)).setText(this.note.getTitle());
            ((EditText) findViewById(R.id.contentEdit)).setText(this.note.getContent());
        }
    }

    private void applySelectedTheme() {
        setTheme(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("pref_key_use_black_theme", false) ? R.style.DarkTheme : R.style.LightTheme);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        applySelectedTheme();
        super.onCreate(bundle);
        setContentView(R.layout.activity_note);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
        Bundle extras = getIntent().getExtras();
        long j = extras != null ? extras.getLong("NOTE_ID", -1L) : -1L;
        this.noteId = j;
        if (j != -1) {
            loadNote();
        } else {
            this.note = new Note();
        }
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_note, menu);
        return true;
    }

    @Override
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i == 4) {
            confirmSaveBeforeExit();
        }
        return super.onKeyDown(i, keyEvent);
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.save_note) {
            saveNote();
        } else if (itemId == R.id.share) {
            shareCurrentNote();
        }
        return super.onOptionsItemSelected(menuItem);
    }
}




