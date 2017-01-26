package com.duckduckgo.mobile.android.dialogs;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.util.DDGUtils;

public final class RestartApplicationDialogBuilder extends AlertDialog.Builder{

	public RestartApplicationDialogBuilder(final Context context, final Intent intent) {
		super(context);
        setTitle(context.getResources().getString(R.string.RestartApplication));
        setMessage(context.getString(R.string.RestartApplicationReasonTor));
        setCancelable(false);
        setPositiveButton(context.getResources().getString(R.string.Yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int buttonId) {
                AlarmManager mgr = (AlarmManager) DDGApplication.getInstance().getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 10, PendingIntent.getActivity(DDGApplication.getInstance().getBaseContext(), 0, new Intent(intent), PendingIntent.FLAG_UPDATE_CURRENT));
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
        setNegativeButton(context.getResources().getString(R.string.No), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int buttonId) {
                dialog.dismiss();
            }
        });
        setIcon(android.R.drawable.ic_dialog_info);
	}
}
