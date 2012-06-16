package esn.activities;

import esn.classes.Sessions;
import esn.models.UsersManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends Activity {

	Intent intent;

	private ProgressDialog dialog;

	private Handler handler;

	private Context context;

	public SharedPreferences pref;

	public String email;

	private Sessions session;

	public String password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.login);
		session = Sessions.getInstance(context);

		intent = this.getIntent();
		handler = new Handler();
		context = this;
		TextView tvForgotPassword = (TextView) findViewById(R.id.tvForgotPassgord);

		tvForgotPassword
				.setText(Html
						.fromHtml("<a href=\"http://www.esn.com/forgotpassword\">Forgot your password?</a> "));

		tvForgotPassword.setMovementMethod(LinkMovementMethod.getInstance());
	}

	public void BackClicked(View view) {
		setResult(RESULT_CANCELED, intent);

		finish();
	}

	public void LoginClicked(View view) {

		dialog = new ProgressDialog(this);
		dialog.setTitle(this.getResources().getString(R.string.app_login));
		dialog.setTitle(getResources().getString(R.string.app_register));
		dialog.show();
		LoginThread loginThread = new LoginThread();
		loginThread.start();
	}

	public class LoginThread extends Thread {
		public LoginThread() {
		}

		@Override
		public void run() {
			UsersManager usermManager = new UsersManager();
			EditText txtEmail = (EditText) findViewById(R.id.esn_login_Email);
			EditText txtPass = (EditText) findViewById(R.id.esn_login_pass);
			email = txtEmail.getText().toString();
			password = txtPass.getText().toString();
			if (usermManager.Login(email, password)) {

				handler.post(new loginSuccess());
			} else {
				handler.post(new loginFail());
			}
		}

	}

	private class loginFail implements Runnable {
		@Override
		public void run() {
			dialog.dismiss();
			AlertDialog.Builder alert = new AlertDialog.Builder(context);
			alert.setTitle("Failed!");
			alert.setMessage("Username or password is wrong!");
			alert.show();
		}
	}

	private class loginSuccess implements Runnable {
		@Override
		public void run() {
			session.put("email", email);
			session.put("password", password);
			dialog.dismiss();
			Intent intent = new Intent(context, HomeActivity.class);
			startActivity(intent);
			finish();
		}
	}

}
