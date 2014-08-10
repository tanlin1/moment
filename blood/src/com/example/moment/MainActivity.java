package com.example.moment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import utils.android.Read;
import utils.android.Write;
import utils.android.judgment.Login;
import utils.json.JSONObject;
import utils.json.JSONStringer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.*;

public class MainActivity extends Activity {

	/**
	 * Called when the activity is first created.
	 */
	private EditText emailEdit;
	private EditText passwordEdit;
	private Button login;
	private Button register;
	private String password;
	private String email;
	private CheckBox checkBox;
	private TextView toFindPassword;
	//public static String url = "http://192.168.1.100";
	public static String url = "http://192.168.191.1";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.main);
		checkInternet(this);

		View imageView = findViewById(R.id.MainView);
		imageView.setBackgroundDrawable(getWallpaper().getCurrent());

		login = (Button) findViewById(R.id.button_login);
		register = (Button) findViewById(R.id.button_register);
		emailEdit = (EditText) findViewById(R.id.set_name);
		passwordEdit = (EditText) findViewById(R.id.set_password);

		checkBox = (CheckBox) findViewById(R.id.checkbox);
		toFindPassword = (TextView) findViewById(R.id.find_password);


		checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				if (isChecked) {
					try {
						Write.write(MainActivity.this, "test.txt", "yes\r\n你好吗？");
					} catch (IOException e) {
						System.out.println("写入文件出错！");
						e.printStackTrace();
					}
				}

			}
		});
		toFindPassword.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//连接到服务器找回密码
				startActivity(new Intent().setClass(MainActivity.this, HomeActivity.class));
			}
		});

		//获取昵称编辑框的数据（通过焦点转移）
		emailEdit.setOnFocusChangeListener(new emailFocus());
		//为编辑框设置回车键检测
		passwordEdit.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
					password = passwordEdit.getText().toString();
					//自动以藏输入键盘
					InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
					if (imm.isActive()) {
						imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
					}
					return true;
				}
				return false;
			}
		});

		//点击注册，跳转到注册页面
		//验证用户是否存在等信息在注册页面进行
		register.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent().setClass(MainActivity.this, RegisterActivity.class));
			}
		});
		//点击登录，进行验证用户名以及密码。
		login.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (email == null || password == null) {
					Toast.makeText(getApplication(), R.string.login_warning, Toast.LENGTH_SHORT).show();
				} else {
					new LoginThread().start();
				}
			}
		});
	}

	private void checkInternet(Context context) {
		ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifiInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo mobile = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		if ((wifiInfo.getState() == NetworkInfo.State.DISCONNECTED) && (mobile.getState() == NetworkInfo.State.DISCONNECTED)) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
			dialog.setTitle(R.string.login_dialog_title);
			dialog.setMessage(R.string.net_warning);
			dialog.setPositiveButton(R.string.login_dialog_ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					startActivity(new Intent(Settings.ACTION_SETTINGS));
				}
			});
			dialog.setNegativeButton(R.string.login_dialog_cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//startActivity(new Intent(Settings.ACTION_ADD_ACCOUNT));
					dialog.dismiss();
				}
			});
			dialog.create().show();
		}
	}

	//昵称编辑框焦点侦听
	private class emailFocus implements View.OnFocusChangeListener {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (!hasFocus) {
				email = emailEdit.getText().toString();
				if (!Login.isEmail(email)) {
					Toast.makeText(getApplicationContext(), R.string.email_wrong, Toast.LENGTH_SHORT).show();
					emailEdit.setText("");
					email = null;
				}
			}
		}
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Bundle data = msg.getData();
			if ("true".equals(data.getString("password"))) {
				Toast.makeText(getApplicationContext(), "登录成功！", Toast.LENGTH_SHORT).show();
				//startActivity();
			} else if ("false".equals(data.getString("password"))) {
				Toast.makeText(MainActivity.this, R.string.login_error, Toast.LENGTH_SHORT).show();
			} else if ("yes".equals(data.getString("timeout"))) {
				Toast.makeText(MainActivity.this, R.string.timeout, Toast.LENGTH_SHORT).show();
			}
		}
	};

	private class LoginThread extends Thread {

		@Override
		public void run() {
			login();
		}

	}

	private void login() {

		URL loginUrl;
		HttpURLConnection connection = null;
		try {
			loginUrl = new URL(url + ":8080/phone_login");
			connection = (HttpURLConnection) loginUrl.openConnection();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			connection.setRequestMethod("POST");
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);

			//构造json字符串，并发送
			JSONStringer jsonStringer = new JSONStringer();
			String transfer;
			transfer = jsonStringer.object().key("email").value(email).key("password").value(password).endObject().toString();
			System.out.println(transfer);

			//设置请求头字段
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//          这个属性将被用于大文件传输，有效的提高效率
//			connection.setRequestProperty("Content-Type","multipart/form-data");
			//有相同的属性则覆盖
			connection.setRequestProperty("user-agent", "Android 4.0.1");
			connection.setConnectTimeout(5000);
			connection.connect();

			OutputStream writeToServer = connection.getOutputStream();
			writeToServer.write(transfer.getBytes());
			writeToServer.flush();
			writeToServer.close();


			// 取得输入流，并使用Reader读取
			JSONObject serverInformation = Read.read(connection.getInputStream());
			if (serverInformation.getString("isPassed").equals("no") || serverInformation.getString("server").equals("error")) {
				sendMessage("password", "false");
			}
			if (serverInformation.getString("isPassed").equals("yes")) {
				sendMessage("password", "true");
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (SocketTimeoutException e) {
			sendMessage("timeout", "yes");
		} catch (SocketException e) {
			System.out.println("********************");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			connection.disconnect();
		}
	}

	private void sendMessage(String key, String value) {
		Bundle data = new Bundle();
		Message msg = new Message();
		data.putString(key, value);
		msg.setData(data);
		handler.sendMessage(msg);
	}
}