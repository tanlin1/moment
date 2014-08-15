package com.example.moment;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import content.Person;
import content.TestAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HP on 2014/7/19.
 */
public class HomeActivity extends Activity {

	ListView listView;
	//List<Map<String,Object>> list = new ArrayList<Map<String, Object>>();
	List<Person> list = new ArrayList<Person>();
	SimpleAdapter simple;
	TestAdapter test;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.home);
		initView();

	}
		private void initView() {
			listView = (ListView) super.findViewById(R.id.listView);

			Person p1 = new Person("01","tan1","123456");
			Person p2 = new Person("02","tan2","123457");
			Person p3 = new Person("03","tan3","123458");
			Person p4 = new Person("04","tan4","123459");
			list.add(p1);
			list.add(p2);
			list.add(p3);
			list.add(p4);
			listView.setAdapter(new TestAdapter(this,list));
		}
//
//		private void settingAdapter() {
//			initList();
//			// map中的key
//			String from[] = new String[] {"people"};
//			// 模板中的组件id
//			int to[] = new int[] { R.id.id, R.id.name, R.id.number };
//			simple = new SimpleAdapter(this, list, R.layout.notify,
//					from, to);
//		}

//		private void initList() {
//			// 显示的图片资源
//			// 定义一个二维数组来显示姓名和简介
////			String string[][] = new String[][] { { "刘诗诗", "刘诗诗简介" },
////					{ "刘诗诗2", "刘诗诗2简介" }, { "艾薇儿", "艾薇儿简介" }, { "艾薇儿", "艾薇儿2简介" },
////					{ "朴信惠", "朴信惠简介" }, { "朴信惠2", "朴信惠2简介" }, { "朴信惠3", "朴信惠3简介" },
////					{ "允儿", "允儿简介" }, { "允儿2", "允儿2简介" }, };
//			//初始化list数据
//			for (int i = 0; i < 3; i++) {
//				HashMap<String, Object> map = new HashMap<String, Object>();
//				Person p = new Person(2*i + "", "tan", "1000"+i);
//				map.put("people", p);
////				map.put("name", p.getName());
////				map.put("introduce", p.getNum());
//				list.add(map);
//			}
//		}


//
//		listView = (ListView) findViewById(R.id.list);
//		for(int i = 0; i < 3; i++) {
//			Person p = new Person(2*i + "", "tan-lin", "10000");
//			mList.add(p);
//		}
//		simple = new SimpleAdapter(this,mList,R.layout.notify,new String[]{"id","name","number"},
//				new int[]{R.id.id,R.id.name,R.id.number});
//
//		listView.setAdapter(simple);

//		Intent info =  getIntent();
//		setResult(0,info);
//		finish();


//
//		final NotificationManager notify = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//		final Notification notice = new Notification();
//
//		notice.icon = R.drawable.ic_launcher;
//		notice.tickerText = "状态栏消息";
//		notice.flags = Notification.FLAG_INSISTENT;
//		notice.when = System.currentTimeMillis();
//		notice.defaults = Notification.DEFAULT_SOUND;
//
//		RemoteViews remoteView = new RemoteViews(this.getPackageName(),R.layout.notification);
//		remoteView.setImageViewResource(R.id.image, R.drawable.ic_launcher);
//		remoteView.setTextViewText(R.id.text, "Hello,this message");
//
//		notice.contentView = remoteView;
// 3、为Notification的contentIntent字段定义一个Intent(注意，使用自定义View不需要setLatestEventInfo()方法)

//这儿点击后简答启动Settings模块
//		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,new Intent("notice"), 0);
//		notice.contentIntent = contentIntent;


//		notice.icon = R.drawable.ic_launcher;
//		notice.tickerText = "这里应该是放提示的主要信息";
//		notice.flags = Notification.FLAG_ONGOING_EVENT;
//		notice.when = System.currentTimeMillis();
//		notice.defaults = Notification.DEFAULT_SOUND;
//
//		Button b = (Button) findViewById(R.id.button);
//		b.setOnClickListener(new View.OnClickListener() {
//			PendingIntent pi = PendingIntent.getActivity(HomeActivity.this,0,new Intent("notice"),0);
//			@Override
//			public void onClick(View v) {
//				notice.setLatestEventInfo(HomeActivity.this, "通知小标题", "通知的详细内容，应该有行数或者字数的限制才对",pi);
//				notify.cancelAll();
//				notify.notify(0x1000,notice);
//			}
//		});
	}

//	private void fillListView(){
//		ContentResolver contentResolver = getContentResolver();
//		Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,null,null,null,null);
//		while(cursor.moveToNext()){
//			int idColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID);
//			int disPlayNameColumn = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
//
//			int hasNumber = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
//
//			String id = cursor.getString(idColumn);
//			String disPlayName = cursor.getString(disPlayNameColumn);
//
//			Person p = new Person(id,disPlayName);
//
//			StringBuffer sb = new StringBuffer();
//			if(hasNumber > 0){
//				Cursor phoneCursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.Data.CONTACT_ID + "=" + id,null,null);
//				while(phoneCursor.moveToNext()){
//					String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//					sb.append(phoneNumber).append(" ");
//				}
//			}
//			p.setNum(sb.toString());
//			list.add(p);
//			listView.setAdapter(new TestAdapter(this,list));
//		}
//	}
//}
