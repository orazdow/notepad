package com.clemm.notepad;

import android.content.Context;
import android.content.Intent;

class ShareHelper {
    public static void shareText(Context context, String text) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.SEND");
        intent.putExtra("android.intent.extra.TEXT", text);
        intent.setType("text/plain");
        context.startActivity(Intent.createChooser(intent, context.getResources().getText(R.string.share)));
    }
}



