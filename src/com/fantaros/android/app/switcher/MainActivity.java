package com.fantaros.android.app.switcher;

import java.lang.reflect.Method;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.app.admin.IDevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.fantaros.android.api.AndroidCommand;

public class MainActivity extends Activity {
	private static final String TAG = "Switcher";

	private Button shutdown;
	private Button reboot;
	private Button lock;
	IDevicePolicyManager service;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		shutdown = (Button) findViewById(R.id.buttonShutdown);
		reboot = (Button) findViewById(R.id.buttonReboot);
		lock = (Button) findViewById(R.id.buttonLock);
		if (shutdown != null) {
			shutdown.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					AndroidCommand.execRooted("reboot -p");
				}
			});
		}
		if (reboot != null) {
			reboot.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					AndroidCommand.execRooted("reboot");
				}
			});
		}
		if (lock != null) {
			lock.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					lock();
				}
			});
		}
	}

	// ����
	public void lock() {
		try {
			// ͨ�������ȡ��sdk���صķ���
			Method method = Class.forName("android.os.ServiceManager")
					.getMethod("getService", String.class);
			IBinder binder = (IBinder) method.invoke(null,// �������
					new Object[] { Context.DEVICE_POLICY_SERVICE });
			service = IDevicePolicyManager.Stub.asInterface(binder);
			// �������������
			ComponentName mAdminName = new ComponentName(this,
					MainActivity.class);
			// ע��Ȩ��
			if (service != null) {
				// ע���deviceadmin��Ȩ��
				Intent intent = new Intent(
						DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
				intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
						mAdminName);
				startActivity(intent);
				// ���÷���ʵ������
				service.lockNow();
			}
		} catch (Throwable e) {
			Log.e(TAG, "Lock screen failed!", e);
		}
	}

}
