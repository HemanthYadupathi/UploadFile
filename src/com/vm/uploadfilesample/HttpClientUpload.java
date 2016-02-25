package com.vm.uploadfilesample;

import java.io.File;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.os.AsyncTask;
import android.provider.Settings.Secure;
import android.util.Log;

/**
 * @author HemanthY
 * @dated Jun 16th 2014
 * @description This class extends the Async Task to Upload the file in the
 *              server for the given API, used the HttpPost class for that.
 *              Where ever you want to Upload the file for server call this
 *              class with the RestAPI and the file object.
 */

public class HttpClientUpload extends AsyncTask<File, Void, String> {

	private Context context;
	private String filePath;
	private HttpParams mHttpParams;
	private HttpResponse response;
	private String TAG = HttpClientUpload.class.getName();

	public HttpClientUpload(Context context) {
		this.context = context;
	}

	@Override
	protected String doInBackground(File... params) {
		File file = params[0];

		filePath = file.getAbsolutePath();

		// mSharedPreferences = context.getSharedPreferences(Model.LoginPref,
		// Context.MODE_PRIVATE);
		// String userId = mSharedPreferences.getString(Model.userid, "");
		String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
		String urlServer = "ServerAPI";

		String serverString = null;
		try {
			serverString = uploadFile(urlServer, file, fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serverString;
	}

	// Upload the given file to the server, return result.
	public String uploadFile(String serverurl, File f, String filename)
			throws Exception {
		String requestInfo = "{\"FileName\":" + "\"" + filename + "\"" + ","
				+ "\"DocumentTypeCode\":\"49K30G\","
				+ "\"RequestType\":\"EmployeeDependent\","
				+ "\"RequestCode\":\"10\"," + "\"fkCompanyCode\":\"VM001\","
				+ "\"UserID\":10404,\"AuditEventDescription\":\"Document  ( "
				+ filename + " )  Uploaded\"," + "\"RequestId\":\"10\","
				+ "\"EntityDescription\":\"EmployeeDependent\","
				+ "\"UserName\":\"Suraj  Mustoor\"} ";

		String result_data = "";
		mHttpParams = new BasicHttpParams();

		HttpClient httpclient = new DefaultHttpClient(mHttpParams);
		try {
			ContentType contentType = ContentType.create(HTTP.PLAIN_TEXT_TYPE,
					HTTP.UTF_8);
			HttpPost httppost = new HttpPost(serverurl);
			String p = "file://" + filePath;
			MultipartEntityBuilder entity = MultipartEntityBuilder.create();

			entity.addPart("request", new StringBody(requestInfo, contentType));
			entity.addPart("files", new FileBody(f));

			// entity.addPart("UploadFile", new StringBody(filename,
			// contentType));
			entity.addPart("filepath", new StringBody(p, contentType));
			entity.addPart(
					"author",
					new StringBody(Secure.getString(
							context.getContentResolver(), Secure.ANDROID_ID),
							contentType));

			httppost.setEntity(entity.build());
			httppost.addHeader("Accept", "application/json");
			httppost.getRequestLine().getUri();

			Log.i(TAG, "executing request " + httppost.getRequestLine());

			response = httpclient.execute(httppost);
			HttpEntity resEntity = response.getEntity();
			String code_message = response.getStatusLine() + "/";

			if (response.getStatusLine().getStatusCode() == 200) {
				Log.i(TAG, code_message);

				if (resEntity != null) {
					// publishProgress(100);
					String result_message = EntityUtils.toString(resEntity);
					resEntity.consumeContent();
					Log.i(TAG, result_message);
					code_message = code_message + result_message;
				}
				httpclient.getConnectionManager().shutdown();
				result_data = code_message;
			} else {

				result_data = null;
			}

		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			result_data = null;
		}
		return result_data;
	}
}
