package esn.classes;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;

public class HttpHelper {
	private String url;

	public HttpHelper(String URL) {
		this.url = (URL.endsWith("/")) ? URL : URL + "/";
	}

	private HttpResponse post(String url, Bundle headers,
			List<NameValuePair> params) throws ClientProtocolException,
			IOException {
		// instance client
		HttpClient client = new DefaultHttpClient();
		// instance HttpPost object
		HttpPost httpPost = new HttpPost(url);
		// add header
		if (headers != null) {
			for (String key : headers.keySet()) {
				httpPost.addHeader(key, headers.getString(key));
			}
		}
		if (params != null) {
			httpPost.setEntity(new UrlEncodedFormEntity(params));
		}
		// execute request
		return client.execute(httpPost);
	}

	private String read(InputStream in) throws IOException {
		// instance buffered input stream
		BufferedInputStream bfs = new BufferedInputStream(in);
		// Byte array buffer
		ByteArrayBuffer baf = new ByteArrayBuffer(20);

		int current = 0;
		while ((current = bfs.read()) != -1) {
			baf.append((byte) current);
		}

		return new String(baf.toByteArray());
	}

	public JSONObject invokeWebMethod(String method)
			throws ClientProtocolException, IOException, JSONException {

		return invokeWebMethod(method, null);
	}

	/**
	 * ham goi WebMethod Cua .net tra ve JSONObject
	 * 
	 * @param method
	 *            Method in Asp.net page
	 * @param params
	 *            parameter for this method
	 * @return JSONObject result
	 * @throws JSONException
	 * @throws IOException
	 */
	public JSONObject invokeWebMethod(String method, Bundle params)
			throws JSONException, IOException {
		// init result
		JSONObject result = null;

		Bundle headers = new Bundle();
		headers.putString("Content-Type", "application/json");
		headers.putString("Content-Encoding", "utf-8");
		// set params
		List<NameValuePair> dataParams = null;
		if (params != null) {
			// instance dataParams
			dataParams = new ArrayList<NameValuePair>();
			// duyet het cac key trong params
			for (String key : params.keySet()) {
				dataParams.add(new BasicNameValuePair(key, params
						.getString(key)));
			}
		}
		String url_method = this.url + method;
		// execute post request
		HttpResponse response = post(url_method, headers, dataParams);
		// get input stream
		InputStream in = response.getEntity().getContent();
		// get result
		result = new JSONObject(read(in));
		if (result.has("d")) {
			result = result.getJSONObject("d");
		}
		return result;
	}

	public JSONObject get(String method) throws ClientProtocolException,
			IOException, JSONException {
		JSONObject result = null;
		HttpClient client = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(this.url);
		HttpResponse response = client.execute(httpPost);

		InputStream intputStream = response.getEntity().getContent();

		BufferedInputStream bfs = new BufferedInputStream(intputStream);
		ByteArrayBuffer baf = new ByteArrayBuffer(20);

		int current = 0;
		while ((current = bfs.read()) != -1) {
			baf.append((byte) current);
		}

		String jsonString = new String(baf.toByteArray());
		result = new JSONObject(jsonString);

		return result;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}