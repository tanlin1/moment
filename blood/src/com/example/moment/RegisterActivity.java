package com.example.moment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;
import com.example.services.FileService;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	@Override
	@SuppressLint("NewApi")
	protected void onCreate(Bundle savedInstanceState) {

		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork()   // or .detectAll() for all detectable problems
				.penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath().build());

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
						Toast.makeText(getApplication(), "昵称不能为空！", Toast.LENGTH_LONG).show();
					}else{
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
						Toast.makeText(getApplication(), "密码不能为空！", Toast.LENGTH_LONG).show();
					}else{
						password = passwordFind.getText();
					}
				}
			}
		});
		passwordConfirmFind.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					if (passwordConfirmFind.getText().length() == 0 ){
						Toast.makeText(getApplication(), "密码不一致！", Toast.LENGTH_LONG).show();
					}else{
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

		/**
		 * 找到checkbox，并设置初值为已经点击
		 */
		checkBox = (CheckBox) findViewById(R.id.checkbox);
		checkBox.setChecked(true);

		//new test().start();

	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.getData().get("keyWord").equals("Values")) {
				Log.i("Tag", "this is a test");
			}
		}
	};

	private final class TextviewOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			/**
			 * 打开用户协议给用户浏览
			 */
			String s = getMessage(getResources().openRawResource(R.raw.test));
			System.out.println(s);
		}
	}

	private void saveFile(File file) {
		String content = "test input 中文";
		FileService service = new FileService(getApplicationContext());
		try {
			service.save(file.getName(), content);
		} catch (IOException e) {
			e.printStackTrace();
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
		StringBuffer sb = new StringBuffer("");
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

//			if (name.length() == 0) {
//				Toast.makeText(getApplicationContext(), "昵称不能为空！", Toast.LENGTH_LONG).show();
//			}
//			/**
//			 * 不能继续注册
//			 * 弹出对话框Dialog
//			 */
//			else if (password.toString().length() == 0) {
//				//new AlertDialog.Builder(RegisterActivity.this).setTitle("注册失败").setMessage("请仔细阅读用户协议").show();
//				Toast.makeText(getApplicationContext(), "密码不能为空！", Toast.LENGTH_LONG).show();
//			} else if (password.toString().equals((passwordConfirm).toString()) == false) {
//				//new AlertDialog.Builder(RegisterActivity.this).setTitle("注册失败").setMessage("请仔细阅读用户协议").show();
//				//Log.i("tag", password.toString() + "!!!===" + passwordConfirm.toString());
//				Toast.makeText(getApplicationContext(), R.string.confirm_false, Toast.LENGTH_LONG).show();
//			} else if (emailAdress.length() == 0) {
//				Toast.makeText(getApplicationContext(), "邮箱不能为空，用于找回密码。", Toast.LENGTH_LONG).show();
//			} else if (checkBox.isChecked() == false) {
//				Toast.makeText(getApplicationContext(), "请同意用户协议", Toast.LENGTH_LONG).show();
//			} else if (isEmail(emailAdress.toString()) && checkBox.isChecked()) {
//				/**
//				 * 提交数据
//				 */
//
//				//Toast.makeText(getApplicationContext(), "可以提交！！！！！！！！", Toast.LENGTH_LONG).show();
//			} else {
//				Toast.makeText(getApplicationContext(), "请注意邮箱格式！", Toast.LENGTH_LONG).show();
//			}
			new myThread().start();
		}
	}

	public boolean isEmail(String email) {
		String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(email);
		return m.matches();
	}

	private void Register() {
		String strUrl = "http://192.168.1.100:8080/test2";
		URL url = null;
		try {
			url = new URL(strUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			/**
			 * 进行连接，但是实际上get request要在下一句的connection.getInputStream()
			 * 方法才会真正发到服务器
			 */
			connection.setRequestMethod("POST");
			connection.connect();
			// 取得输入流，并使用Reader读取
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			System.out.println("Contents of get request");

			Bundle data = new Bundle();

			data.putString("keyWord", "Values");

			registerMessage.setData(data);

//			String lines;
//			while ((lines = reader.readLine()) != null) {
//				System.out.println(lines);
//			}
			reader.close();
			// 断开连接
			connection.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	Message registerMessage = new Message();

	private class myThread extends Thread implements Runnable {

		@Override
		public void run() {
			Register();
			handler.sendMessage(registerMessage);
		}
	}
}
