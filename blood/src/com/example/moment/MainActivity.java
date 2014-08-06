package com.example.moment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import utils.android.Read;
import utils.android.judgment.Login;
import utils.json.JSONObject;
import utils.json.JSONStringer;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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
	private TextView textView;
	private boolean isconnect = false;
	//public static String url = "http://192.168.1.100";
	public static String url = "http://192.168.191.1";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.main);

		View imageView = findViewById(R.id.MainView);
		imageView.setBackgroundDrawable(getWallpaper().getCurrent());

		login = (Button) findViewById(R.id.button_login);
		register = (Button) findViewById(R.id.button_register);
		emailEdit = (EditText) findViewById(R.id.set_name);
		passwordEdit = (EditText) findViewById(R.id.set_password);

		checkBox = (CheckBox) findViewById(R.id.checkbox);
		textView = (TextView) findViewById(R.id.forget_password);





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

	//昵称编辑框焦点侦听
	private class emailFocus implements View.OnFocusChangeListener {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (!hasFocus) {
				email = emailEdit.getText().toString();
				if(!Login.isEmail(email)){
					Toast.makeText(getApplicationContext(),R.string.email_wrong,Toast.LENGTH_SHORT).show();
					emailEdit.setText("");
					email = null;
				}
			}
		}
	}

	private class MyHandler extends Handler {
		public MyHandler() {
		}
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.getData().get("flag").equals("true")) {
				startActivity(new Intent().setClass(MainActivity.this, LoginActivity.class));
				Toast.makeText(getApplicationContext(), "登录成功！", Toast.LENGTH_LONG).show();
			}else {
				Toast.makeText(getApplicationContext(), R.string.login_error, Toast.LENGTH_LONG).show();
			}
		}
	}

	private class LoginThread extends Thread {

		MyHandler myHandler = new MyHandler();
		Message msg = new Message();

		@Override
		public void run() {
			if (login()) {
				//成功连接到服务器，标志位
				//发送消息给父进程
				Bundle data = new Bundle();
				data.putBoolean("isconnect", true);
				msg.setData(data);
				myHandler.sendMessage(msg);
			}
		}
	}

	private boolean login() {

		URL loginUrl;
		HttpURLConnection connection;
		try {
			loginUrl = new URL(url+":8080/phone_login");
			connection = (HttpURLConnection) loginUrl.openConnection();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
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
			String temp = Read.read(connection.getInputStream());;

			JSONObject serverInformation = new JSONObject(temp);
			System.out.println(temp);
			if(serverInformation.getString("isPassed").equals("no") || serverInformation.getString("server").equals("error")){
				return false;
			}
			return true;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}finally {
			connection.disconnect();
		}
	}
}