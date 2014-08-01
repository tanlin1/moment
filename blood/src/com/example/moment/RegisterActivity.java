package com.example.moment;

import android.app.Activity;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by HP on 2014/7/18.
 */
public class RegisterActivity extends Activity {

	private CharSequence name;
	private CharSequence password;
	private CharSequence passwordConfirm;
	private CharSequence emailAdress;
	private CheckBox checkBox;

	private EditText nameFind;
	private EditText passwordFind;
	private EditText emailAdressFind;
	private EditText passwordConfirmFind;

	private boolean registerFlag = false;

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
		 * 链接到文件，用户协议
		 */
		TextView tv = (TextView) findViewById(R.id.protocol_view);
		tv.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		tv.setOnClickListener(new TextviewOnClickListener());
		/**
		 * 找到各个编辑框
		 */
		nameFind = ((EditText) findViewById(R.id.register_name_edit));
		passwordFind = ((EditText) findViewById(R.id.register_password_edit));
		emailAdressFind = ((EditText) findViewById(R.id.register_email_edit));
		passwordConfirmFind = (EditText) findViewById(R.id.register_password_confirm_edit);

//		name = nameFind.getText();
//		password = passwordFind.getText();
//		passwordConfirm = passwordConfirmFind.getText();
//		emailAdress = emailAdressFind.getText();

		nameFind.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					if (nameFind.getText().length() == 0) {
						Toast.makeText(getApplication(), R.string.name_null, Toast.LENGTH_LONG).show();
					} else {
						name = nameFind.getText();
					}
				}
			}
		});

		passwordFind.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					if (passwordFind.getText().length() == 0) {
						Toast.makeText(getApplication(), R.string.password_null, Toast.LENGTH_LONG).show();
					} else {
						password = passwordFind.getText();
					}
				}
			}
		});
		passwordConfirmFind.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					if (passwordConfirmFind.getText().length() == 0) {
						Toast.makeText(getApplication(), R.string.password_equal, Toast.LENGTH_LONG).show();
					} else {
						passwordConfirm = passwordConfirmFind.getText();
					}
				}
			}
		});

		/**
		 * 获取注册按钮
		 */
		Button app_register = (Button) findViewById(R.id.app_register);
		app_register.setClickable(false);
		app_register.setOnClickListener(new ButtonOnClickListener());
		//new test().start();

	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (registerFlag) {
				Log.i("Tag", "注册成功");
			}else {
				Log.i("Tag","注册失败");
			}
		}
	};

	private final class TextviewOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			/**
			 * 打开先=新窗口用户协议给用户浏览
			 */
			String s = getMessage(getResources().openRawResource(R.raw.test));
			System.out.println(s);
		}
	}
	public String getMessage(InputStream inputStream) {
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
			new myThread().start();
		}
	}

	private boolean Register() {
		URL nur;
		try {
			nur = new URL(MainActivity.url);
			HttpURLConnection connection = (HttpURLConnection) nur.openConnection();
			/**
			 * 进行连接，但是实际上get request要在下一句的connection.getInputStream()
			 * 方法才会真正发到服务器
			 */
			connection.setRequestMethod("POST");
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(true);

			//获取输出流，向服务器发送注册信息
			StringBuffer buffer = new StringBuffer();
			OutputStream writeToServer = connection.getOutputStream();

			//向缓冲区注入注册信息
			buffer.append(name).append(password);

			connection.setConnectTimeout(2000);
			byte[] data = buffer.toString().getBytes();
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("Content-Length", String.valueOf(data.length));
			connection.connect();
			writeToServer.write(data);

			// 取得输入流，并使用Reader读取
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
			String temp;
			StringBuffer sb = new StringBuffer();
			while ((temp = reader.readLine()) != null) {
				sb.append(temp);
			}
			System.out.println(sb);
			reader.close();

			// 断开连接
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

	Message registerMessage = new Message();
	Bundle data = new Bundle();

	private class myThread extends Thread implements Runnable {

		@Override
		public void run() {
			if (Register()) {
				registerFlag = true;
			} else {
				registerFlag = false;
				//registerMessage.setData(new Bundle());
			}
		}
	}
}
