package com.duckduckgo.mobile.android.dialogs;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.net.http.SslCertificate;
import android.net.http.SslError;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.widget.TextView;

import com.duckduckgo.mobile.android.R;

public final class SSLCertificateDialog extends Builder {

	public SSLCertificateDialog(final Context context, final SslErrorHandler handler, final SslError error) {
		super(context);
		
		setView(getCertificateText(context, error.getCertificate()));
	
        setTitle(R.string.WarnSSLTitle);
        setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                handler.proceed();
            }
        });
        setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            	dialog.dismiss();
            	handler.cancel();
            }
        });
	}
	
	private View getCertificateText(Context context, SslCertificate certificate) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View cv = inflater.inflate(R.layout.dialog_certificate, null);
		TextView tv1 = (TextView) cv.findViewById(R.id.certIssuedBy);
		tv1.setText(certificate.getIssuedBy().getCName());
		TextView tv2 = (TextView) cv.findViewById(R.id.certIssuedTo);
		tv2.setText(certificate.getIssuedTo().getCName());
		return cv;
	}

}
