package com.example.moment;

import android.app.Activity;
import android.app.NotificationManager;
import android.os.Bundle;

/**
 * Created by Administrator on 2014/8/10.
 */
public class NotifyActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notify);
		NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		manager.cancel(0x1000);
	}
//
//	public class ShowContact{
//
//		private String id;
//		private String displayName;
//		private String phoneNumber;
//
//		public ShowContact(String id, String displayName){
//			this.id = id;
//			this.phoneNumber = phoneNumber;
//		}
//		public ShowContact(String id, String displayName, String phoneNumber){
//			this.id = id;
//			this.displayName = displayName;
//			this.phoneNumber = phoneNumber;
//		}
//
//		public String getPhoneNumber() {
//			return phoneNumber;
//		}
//
//		public void setPhoneNumber(String phoneNumber) {
//			this.phoneNumber = phoneNumber;
//		}
//
//		public String getDisplayName() {
//			return displayName;
//		}
//
//		public void setDisplayName(String displayName) {
//			this.displayName = displayName;
//		}
//
//		public String getId() {
//			return id;
//		}
//
//		public void setId(String id) {
//			this.id = id;
//		}
//	}
}
