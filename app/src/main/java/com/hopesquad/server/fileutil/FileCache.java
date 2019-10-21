package com.hopesquad.server.fileutil;
import android.content.Context;
import android.util.Log;

import java.io.File;

public class FileCache {

	private File cacheDir;

	public FileCache(Context context, String cacheName) {
		// Find the dir to save cached images
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), null != cacheName ? cacheName : "DefaultCache");
		} else {
			cacheDir = context.getCacheDir();
		}
		if (!cacheDir.exists()) {
			boolean created = cacheDir.mkdirs();
			Log.d(this.getClass().getName(), "created: " + created);
		}
	}

	public File getFile(String url) {
		String filename = String.valueOf(url.hashCode());
		//Log.d(this.getClass().getCropName(), "filename: " + filename);

		File f = new File(cacheDir, filename);
		return f;
	}

	public void clear() {
		File[] files = cacheDir.listFiles();
		if (files == null) {
			return;
		}
		for (File f : files) {
			f.delete();
		}
	}

}