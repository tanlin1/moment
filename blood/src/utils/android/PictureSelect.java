package utils.android;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by Administrator on 2014/8/21.
 */
public class PictureSelect extends Activity {

	private final int PICTURE_ASK = 1001;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		Intent pictureSelect = new Intent(Intent.ACTION_GET_CONTENT);
		pictureSelect.setType("image/*");

		//调用系统相册
		startActivityForResult(pictureSelect, PICTURE_ASK);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//成功（虽然Intent为空，那是因为我们指定了保存路径，Intent返回的是一个内容提供者Content）
		if (resultCode == Activity.RESULT_OK && requestCode == PICTURE_ASK) {

			handlePictureSelect(data);
		}
	}

	//处理选择的图片
	private void handlePictureSelect(Intent data) {

		ContentResolver resolver = getContentResolver();
		boolean isBig = false;
		// 照片的原始资源地址
		Uri imgUri = data.getData();

	}
}
