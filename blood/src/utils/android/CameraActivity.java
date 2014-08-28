package utils.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import com.example.moment.R;
import utils.PartFactory;
import utils.android.photo.Upload;
import utils.makejson.JsonTool;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Administrator on 2014/8/18.
 */
public class CameraActivity extends Activity {
	public static String BOUNDARY = "---------------------------7de8c1a80910";
	private final int CAMERA_ASK = 1000;
	private String photoName;
	private File directory;
	private Button post;
	private Button handle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.camera_layout);
		//以日期命名jpg格式
		photoName = DateFormat.format("yyyy-MM-dd-hh-mm-ss",
				Calendar.getInstance(Locale.CHINA)).toString() + ".jpg";


		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			//File path = Environment.getExternalStorageDirectory(); //取得sdcard文件路径
			//File path = Environment.(); //取得sdcard文件路径

			StorageManager manager = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
			try {
				Method methodMnt = manager.getClass().getMethod("getVolumePaths");

				String[] path = (String[]) methodMnt.invoke(manager);

				System.out.println(path[1]);

				String filePath = path[0] + "/moment/photo/";
				directory = new File(filePath);
				if (!directory.exists()) {
					directory.mkdirs();
				}
				File photo = new File(directory, photoName);

				Intent takePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				takePhoto.addCategory(Intent.CATEGORY_DEFAULT);

				//指定你保存路径，不会在系统默认路径下（当然可以指定）
				takePhoto.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
				//调用系统相机
				startActivityForResult(takePhoto, CAMERA_ASK);

			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		post = (Button) findViewById(R.id.camera_button_photo_direct_post);
		handle = (Button) findViewById(R.id.camera_button_handle_photo);
		//成功（虽然Intent为空，那是因为我们指定了保存路径，Intent返回的是一个内容提供者Content）
		if (resultCode == Activity.RESULT_OK && requestCode == CAMERA_ASK) {
			//处理上传
			//上传图片到服务器
			String path = directory + "/" + photoName;
			Bitmap bitmap = BitmapFactory.decodeFile(path);
			((ImageView) findViewById(R.id.camera_photo_scanning)).setImageBitmap(bitmap);
			post.setOnClickListener(new View.OnClickListener() {
				//点击上传原图，就开启上传线程
				@Override
				public void onClick(View v) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							handlePictureTake();
						}
					}).start();
				}
			});
		}
	}

	//处理拍摄好的照片
	private void handlePictureTake() {

		HttpURLConnection connection = null;
		String path = directory + "/" + photoName;
		Bitmap bitmap = BitmapFactory.decodeFile(path);

		try {
			connection = Upload.getUrlConnection();

			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setUseCaches(false);
			connection.setConnectTimeout(3000);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("User-Agent", "Android 4.0.1");

			connection.setRequestProperty("Content-Type",
					"multipart/form-data; boundary=" + BOUNDARY);
			connection.connect();
			connection.setReadTimeout(5000);

			ByteArrayOutputStream photoByteArray = new ByteArrayOutputStream();
			byte[] start;
			byte[] end;
			if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, photoByteArray)) {
				byte[] bmp = photoByteArray.toByteArray();
				start = PartFactory.PartBuilder("text", "text", "text/plain", JsonTool.createJsonString("hhh","dddj").getBytes());
				end = PartFactory.PartBuilder("photo", photoName, "image/jpeg", bmp, true);
				connection.getOutputStream().write(start);
				connection.getOutputStream().write(end);
			}
			System.out.println(connection.getResponseCode());

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
		// 获取屏幕分辨率
		// 图片分辨率与屏幕分辨率
	}
}
