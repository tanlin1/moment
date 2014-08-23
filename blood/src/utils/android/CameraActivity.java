package utils.android;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.moment.R;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Administrator on 2014/8/18.
 */
public class CameraActivity extends Activity {
	private final int CAMERA_ASK = 1000;
	private String photoName;
	private File photo;
	private File directory;
	private Intent takePhoto;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.camera_layout);
		//以日期命名jpg格式
		photoName = DateFormat.format("yyyy-MM-dd-hh-mm-ss",
				Calendar.getInstance(Locale.CHINA)).toString() + ".jpg";

		directory = new File("/sdcard/moment/photo/");
		directory.mkdirs();

		photo = new File(directory, photoName);

		takePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		takePhoto.addCategory(Intent.CATEGORY_DEFAULT);

		//指定你保存路径，不会在系统默认路径下（当然可以指定）
		takePhoto.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
		//调用系统相机
		startActivityForResult(takePhoto, CAMERA_ASK);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//成功（虽然Intent为空，那是因为我们指定了保存路径，Intent返回的是一个内容提供者Content）
		if (resultCode == Activity.RESULT_OK && requestCode == CAMERA_ASK) {
			String path = directory + "/" + photoName;
			Bitmap bm = BitmapFactory.decodeFile(path);

			((ImageView) findViewById(R.id.camera_photo_scanning)).setImageBitmap(bm);
			//处理上传
			//上传图片到服务器
			Log.i("TAG", "照相成功！" + requestCode);
			handlePictureTake();
		}
	}

	//处理拍摄好的照片
	private void handlePictureTake() {
		Bitmap bitmap = BitmapFactory.decodeFile(directory + "/" + photoName);
		Toast.makeText(this, photoName, Toast.LENGTH_LONG).show();

		// 获取屏幕分辨率
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		// 图片分辨率与屏幕分辨率
		float scale = bitmap.getWidth() / (float) dm.widthPixels;
	}
}
