package com.example.moment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import utils.android.Read;
import utils.android.judgment.Login;
import utils.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by HP on 2014/7/18.
 */
public class RegisterActivity extends Activity {

	private String name;
	private String password;
	private String passwordConfirm;
	private String emailAddress;
	private CheckBox checkBox;

	private EditText nameFind;
	private EditText passwordFind;
	private EditText emailAddressFind;
	private EditText passwordConfirmFind;

	private boolean registerFlag = false;
	private Map<String, String> userInformation = new HashMap<String, String>();

	//处理子线程传回的数据（消息）
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			//注册成功
			if (msg.getData().getBoolean("register")) {
				Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
				//进入个人主页（设置）
				//或者进入动态页
				System.out.println("正在尝试进入主页，敬请期待！");

			} else if (msg.getData().getBoolean("emailIsUsed")) {
				//邮箱被注册过
				AlertDialog.Builder dialog = new AlertDialog.Builder(RegisterActivity.this);
				dialog.setMessage(R.string.email_used);
				dialog.setCancelable(true);
				dialog.setTitle(R.string.register_error);
				emailAddressFind.setText("");
				dialog.create().show();
			}else if(!msg.getData().getBoolean("edited")){
					Toast.makeText(RegisterActivity.this,R.string.name_null,Toast.LENGTH_SHORT).show();
			}else {
				//other message could be handled here.
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/**
		 * 设置无标题，全屏幕显示
		 */
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.register);
		/**
		 * 找到各个编辑框
		 */
		nameFind = ((EditText) findViewById(R.id.register_name_edit));
		passwordFind = ((EditText) findViewById(R.id.register_password_edit));
		emailAddressFind = ((EditText) findViewById(R.id.register_email_edit));
		passwordConfirmFind = (EditText) findViewById(R.id.register_password_confirm_edit);

		checkBox = (CheckBox) findViewById(R.id.checkbox);
		name = nameFind.getText().toString();
		password = passwordFind.getText().toString();
		passwordConfirm = passwordConfirmFind.getText().toString();
		emailAddress = emailAddressFind.getText().toString();
		/**
		 * 获取注册按钮
		 */
		Button app_register = (Button) findViewById(R.id.app_register);
		app_register.setOnClickListener(new ButtonOnClickListener());



		checkBox.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				/**
				 * 打开新窗口用户协议给用户浏览
				 */
				TextView protocol = (TextView) findViewById(R.id.protocol);
				protocol.getScrollBarStyle();
				if (!checkBox.isChecked()) {
					String s = getProtocolMessage(getResources().openRawResource(R.raw.test));
					protocol.setMovementMethod(new ScrollingMovementMethod());
					protocol.setText(s);
				}else {
					protocol.setText("");
				}
			}
		});
		nameFind.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					name = nameFind.getText().toString();
					if (name.length() == 0) {
						Toast.makeText(getApplication(), R.string.name_null, Toast.LENGTH_LONG).show();
					}
				}
			}
		});

		passwordFind.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					password = passwordFind.getText().toString();
					if (password.length() == 0) {
						Toast.makeText(getApplication(), R.string.password_null, Toast.LENGTH_LONG).show();
					}
				}
			}
		});
		passwordConfirmFind.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					passwordConfirm = passwordConfirmFind.getText().toString();
					if (!password.equals(passwordConfirm)) {
						Toast.makeText(getApplication(), R.string.password_equal, Toast.LENGTH_LONG).show();
					}
				}
			}
		});
		//在填写邮箱的时候会连接服务器并检测此邮箱是否已经注册过本网站
		emailAddressFind.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
					emailAddress = emailAddressFind.getText().toString();
					//自动以藏输入键盘
					InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
					if (imm.isActive()) {
						imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
					}
					if (!Login.isEmail(emailAddress)) {
						Toast.makeText(getApplication(), R.string.email_wrong, Toast.LENGTH_SHORT).show();
						emailAddressFind.setText("");
					} else {
						new emailCheckThread().start();
					}
					return true;
				}
				return false;
			}
		});
	}

	/**
	 * 检测用户注册的时候，邮箱是否符合注册标准
	 * 必须在子线程中执行
	 */
	private class emailCheckThread extends Thread{
		public void run(){
			emailCheck();
		}
	}
	private void emailCheck() {
		URL emailCheckUrl;
		HttpURLConnection conn;
		try {
			emailCheckUrl = new URL(MainActivity.url + ":8080/phone_isEmailUsed");
			conn = (HttpURLConnection) emailCheckUrl.openConnection();
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestMethod("POST");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setConnectTimeout(2000);

			conn.connect();
			JSONObject object = new JSONObject();
			object.put("email", emailAddress);

			conn.getOutputStream().write(object.toString().getBytes());
			//读取服务器返回的消息
			JSONObject jsonObject = new JSONObject(Read.read(conn.getInputStream()));

			if (jsonObject.getString("isUsed").equals("yes")) {
				Message msg = new Message();
				Bundle data = new Bundle();
				data.putBoolean("emailIsUsed", true);
				msg.setData(data);
				handler.sendMessage(msg);
				conn.disconnect();
				return;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}



	private String getProtocolMessage(InputStream inputStream) {

		InputStreamReader inputStreamReader = null;

		try {
			inputStreamReader = new InputStreamReader(inputStream, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		BufferedReader reader = new BufferedReader(inputStreamReader);
		StringBuffer sb = new StringBuffer();
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	/**
	 * 点击注册按钮
	 */
	private class ButtonOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			if (canRegister()) {
				new registerThread().start();
			}
		}
	}

	private boolean Register() {
		URL url;
		HttpURLConnection connection = null;
		try {
			url = new URL(MainActivity.url + ":8080/phone_register");
			connection = (HttpURLConnection) url.openConnection();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			//设置请求头
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestMethod("POST");
			connection.setDoInput(true);
			connection.setDoOutput(true);
			//post请求不能使用cache
			connection.setUseCaches(false);
			connection.setConnectTimeout(2000);

			//JSONObject user = new JSONObject();
			//JSONArray test = new JSONArray();
			JSONObject test = new JSONObject();

			userInformation.put("name", name);
			userInformation.put("email", emailAddress);
			userInformation.put("password", password);

			test.put("register", userInformation);

			System.out.println(test.toString());

//			//获取输出流，向服务器发送注册信息
//			StringBuffer buffer = new StringBuffer();
//			//向缓冲区注入注册信息
//			byte[] data = buffer.toString().getBytes();
//			//建立连接
			connection.connect();

			OutputStream writeToServer = connection.getOutputStream();
			writeToServer.write(test.toString().getBytes());

			// 取得输入流，并使用Reader读取
			String str = Read.read(connection.getInputStream());
			System.out.println(str);
			// 断开连接
			return true;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			connection.disconnect();
		}
	}

	private class registerThread extends Thread {
		Message registerMessage = new Message();

		@Override
		public void run() {
			if (Register()) {
				registerFlag = true;
			} else {
				registerFlag = false;
			}
			Bundle data = new Bundle();
			data.putBoolean("register", registerFlag);
			registerMessage.setData(data);
			handler.sendMessage(registerMessage);
		}
	}

	private boolean canRegister() {
		if (name.length() == 0 || password.length() == 0 || emailAddress.length() == 0 || (!checkBox.isChecked())) {
			Bundle data = new Bundle();
			Message msg = new Message();
			data.putBoolean("edited",false);
			msg.setData(data);
			handler.sendMessage(msg);
			return false;
		}
		return true;
	}
}
