package com.example.moment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends Activity {

	/**
	 * Called when the activity is first created.
	 */
	EditText nameEdit;
	EditText passwordEdit;
	Button login;
	Button register;
	String password;
	String name;

	public static String url = "http://192.168.1.101:8080/test2";
	public boolean isconnect = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.main);

		//查看模拟器分辨率
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		System.out.println(dm.widthPixels + "-------" + dm.heightPixels);


		View imageView = findViewById(R.id.MainView);
		imageView.setBackgroundDrawable(getWallpaper().getCurrent());

		login = (Button) findViewById(R.id.button_login);
		register = (Button) findViewById(R.id.button_register);
		nameEdit = (EditText) findViewById(R.id.edit_set_name);
		passwordEdit = (EditText) findViewById(R.id.edit_set_password);
		//获取昵称编辑框的数据（通过焦点转移）
		nameEdit.setOnFocusChangeListener(new nameFouse());
		//为编辑框设置回车键检测
		passwordEdit.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER) {
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
				System.out.println(password + "密码---" + "账号...." + name);
				if (name == null || password == null) {
					Toast.makeText(getApplication(), "账号或密码不能为空", Toast.LENGTH_LONG).show();
				} else {
					new LoginThread().start();
				}
			}
		});
	}

	private class nameFouse implements View.OnFocusChangeListener {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (!hasFocus) {
				name = nameEdit.getText().toString();
			}
		}
	}

	private class MyHandler extends Handler {
		public MyHandler(){

		}
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.getData().get("flag").equals("true")) {
				startActivity(new Intent().setClass(MainActivity.this, LoginActivity.class));
				Log.i("MainActivity", "test is all right");
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
				isconnect = true;
				//发送消息给父进程
				Bundle data = new Bundle();
				data.putString("flag",String.valueOf(isconnect));
				msg.setData(data);
				myHandler.sendMessage(msg);
			}
		}

	}

	private boolean login() {

		URL loginUrl;
		try {
			loginUrl = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) loginUrl.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(true);
			StringBuffer data = new StringBuffer();


			data.append("name").append(name);
			data.append("password").append(password);

			byte[] entity = data.toString().getBytes();
			//设置请求头字段
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("Content-Length", String.valueOf(entity.length));
			connection.setConnectTimeout(5000);

			connection.connect();

			OutputStream writeToServer = connection.getOutputStream();
			writeToServer.write(entity);
			writeToServer.flush();
			writeToServer.close();

			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(),"UTF-8"));

			StringBuilder sb = new StringBuilder();
			String temp;
			while((temp = br.readLine()) != null){
				sb.append(temp);
			}
			System.out.println(sb);
			br.close();
			connection.disconnect();
			return true;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}