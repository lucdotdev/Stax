package com.hover.stax.destruct;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.hover.sdk.permissions.PermissionHelper;
import com.hover.stax.R;
import com.hover.stax.utils.PermissionUtils;
import com.hover.stax.utils.Utils;

import java.util.Date;

public class SelfDestruct extends AppCompatActivity {
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.self_destruct_layout);
		findViewById(R.id.continue_btn).setOnClickListener(view -> attemptDownload());
	}

	public static boolean isTime(Context c) {
		long currentTime = new Date().getTime();
		long selfDestructTime = Long.parseLong(Utils.getBuildConfigValue(c, "SELF_DESTRUCT").toString());
		return currentTime >= selfDestructTime;
	}

	private void attemptDownload() {
		if (PermissionUtils.hasWritePermission(this)) {
			downloadLatest();
		} else {
			requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (new PermissionHelper(this).permissionsGranted(grantResults))
			downloadLatest();
	}

	public void downloadLatest() {
		DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
		Uri uri = Uri.parse("http://maven.usehover.com/apps/stax_release.apk");

		DownloadManager.Request request = new DownloadManager.Request(uri);
		request.setTitle("Stax Update");
		request.setDescription("Downloading");
		request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
		request.setVisibleInDownloadsUi(true);
		request.setDestinationInExternalPublicDir("", "stax.apk");
		downloadmanager.enqueue(request);
		updateView();
	}

	private void updateView() {
		((TextView) findViewById(R.id.explainer)).setText(R.string.uninstall_cardbody);
		((AppCompatButton) findViewById(R.id.continue_btn)).setText(R.string.btn_uninstall);
		findViewById(R.id.continue_btn).setOnClickListener(view -> uninstall());
	}

	public void uninstall() {
		Intent intent = new Intent(Intent.ACTION_DELETE);
		intent.setData(Uri.parse("package:" + Utils.getPackage(this)));
		startActivity(intent);
	}
}
