package com.example.moment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import utils.android.judgment.sdcard.FileService;
import utils.json.JSONArray;
import utils.json.JSONObject;
import utils.makejson.JsonTool;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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

	public static String url = "http://192.168.1.103:8080/register";
	public boolean isconnect = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.main);

		//查看模拟器分辨率
//		DisplayMetrics dm = new DisplayMetrics();
//		getWindowManager().getDefaultDisplay().getMetrics(dm);
//		System.out.println(dm.widthPixels + "-------" + dm.heightPixels);


		View imageView = findViewById(R.id.MainView);
		imageView.setBackgroundDrawable(getWallpaper().getCurrent());

		login = (Button) findViewById(R.id.button_login);
		register = (Button) findViewById(R.id.button_register);
		nameEdit = (EditText) findViewById(R.id.set_name);
		passwordEdit = (EditText) findViewById(R.id.set_password);
		//获取昵称编辑框的数据（通过焦点转移）
		nameEdit.setOnFocusChangeListener(new nameFocus());
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
				if (name == null || password == null) {
					Toast.makeText(getApplication(), R.string.login_warning, Toast.LENGTH_LONG).show();
				} else {
					new LoginThread().start();
				}
			}
		});
	}

	//昵称编辑框焦点侦听
	private class nameFocus implements View.OnFocusChangeListener {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (!hasFocus) {
				name = nameEdit.getText().toString();
			}
		}
	}

	private class MyHandler extends Handler {
		public MyHandler() {
		}

		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (!isconnect) {
				Toast.makeText(getApplicationContext(), R.string.login_error, Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(getApplicationContext(), "登录成功！", Toast.LENGTH_LONG).show();
			}
//			if (msg.getData().get("flag").equals("true")) {
//				startActivity(new Intent().setClass(MainActivity.this, LoginActivity.class));
//				Log.i("MainActivity", "test is all right");
//			}
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
				data.putString("flag", String.valueOf(isconnect));
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
			connection.setRequestMethod("GET");
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(true);

			//构造json字符串，并发送

			//{"student":["name":"nihao", "age",18]}


			StringBuffer data = new StringBuffer();
//			data.append(JsonTool.createJsonString("name", name));
//			data.append(JsonTool.createJsonString("password", password));

			//JSONObject jsonObject = new JSONObject("{'name':name,'password':password,'email':'asdsdfsdf@a.vom'}");
			//data.append(jsonObject.toString());
			//data.append("password").append(password);

			byte[] entity = data.toString().getBytes();
			//设置请求头字段
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			//覆盖
			connection.setRequestProperty("user-agent", "Android 4.0.1");
			connection.setConnectTimeout(5000);
			connection.connect();

			OutputStream writeToServer = connection.getOutputStream();
			writeToServer.write("name=124&password=123456&email=1196139850@qq.com".getBytes());
			writeToServer.flush();
			writeToServer.close();

//
//			ByteArrayOutputStream stream=new ByteArrayOutputStream();
//			FileOutputStream fos = openFileOutput("test.jpg",MODE_WORLD_READABLE);
//
//			FileInputStream fis = openFileInput("test.jpg");
//			byte[] buffer=new byte[1024];
//			//int len=-1;
//			while((connection.getInputStream().read(buffer))!= -1) {
//				stream.write(buffer);
//			}
//
////			stream.close();
////			fis.close(); //关闭输入流
//
//
//			fos.write(stream.toByteArray());
////			fos.close(); //关闭输出流




			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
			StringBuilder sb = new StringBuilder();
			String temp;
			while ((temp = br.readLine()) != null) {
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

	List<Person> list = new ArrayList<Person>();

	private void test() {
		JSONObject jsonObject = new JSONObject();
		JSONArray stuArray = new JSONArray(jsonObject);
		for (int i = 0; i < stuArray.length(); i++) {

			JSONObject personObject = stuArray.getJSONObject(i);  //获得JSON数组中的每一个JSONObject对象
			Person person = new Person();
			int id = personObject.getInt("id");                      //获得每一个JSONObject对象中的键所对应的值
			String name = personObject.getString("name");
			int age = personObject.getInt("age");

			person.setId(id);        //将解析出来的属性值存入Person对象
			person.setName(name);
			person.setAge(age);
			list.add(person);        //将解析出来的每一个Person对象添加到List中
		}
	}

	private class Person {
		private int id;
		private String name;
		private int age;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getAge() {
			return age;
		}

		public void setAge(int age) {
			this.age = age;
		}
	}


	public static class MyHttpClient {
		private DefaultHttpClient httpClient;
		private HttpPost httpPost;
		private HttpEntity httpEntity;
		private HttpResponse httpResponse;
		public static String session = null;

		public MyHttpClient() {

		}

		public String executeRequest(String path, List<NameValuePair> params) {
			String ret = "none";
			try {
				this.httpPost = new HttpPost(path);
				httpEntity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
				httpPost.setEntity(httpEntity);
				//第一次一般是还未被赋值，若有值则将SessionId发给服务器
				if (null != session) {
					httpPost.setHeader("Cookie", "ID=" + session);
				}else
					httpClient = new DefaultHttpClient();

				httpResponse = httpClient.execute(httpPost);

				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					HttpEntity entity = httpResponse.getEntity();
					ret = EntityUtils.toString(entity);
					CookieStore mCookieStore = httpClient.getCookieStore();
					List<Cookie> cookies = mCookieStore.getCookies();

					for (int i = 0; i < cookies.size(); i++) {
						//这里是读取Cookie['PHPSESSID']的值存在静态变量中，保证每次都是同一个值
						if ("session".equals(cookies.get(i).getName())) {
							session = cookies.get(i).getValue();
							break;
						}
					}
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return ret;
		}
	}
}