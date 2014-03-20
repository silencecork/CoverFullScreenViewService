package com.example.servicetakeover;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class MainActivity extends Activity {
	
	private SeekBar mBar;
	private boolean mIsEnable;
	private ServiceStatus mStatus;
	
	private ServiceConnection mConn = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mStatus = ServiceStatus.Stub.asInterface(service);
			try {
				mIsEnable = mStatus.isEnable();
				int progress = mStatus.currentProgress();
				if (mBar != null) {
					mBar.setEnabled(mIsEnable);
					mBar.setProgress(progress);
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mBar = (SeekBar) findViewById(R.id.seekBar1);
		mBar.setEnabled(false);
		mBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				int progress = seekBar.getProgress();
				Intent intent = new Intent("PROGRESS");
				intent.putExtra("progress", progress);
				startService(intent);
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
			}
		});
		
//		Intent intent = new Intent(this, TakeOverService.class);
//		bindService(intent, mConn, Service.BIND_AUTO_CREATE);
	}
	
	public void enable(View v) {
		Intent intent = new Intent("START");
		startService(intent);
		
		mBar.setEnabled(true);
	}
	
	public void disable(View v) {
		Intent intent = new Intent("FINISH");
		startService(intent);
		if (mBar != null) {
			mBar.setProgress(0);
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
//		if (mConn != null) {
//			unbindService(mConn);
//		}
	}
	
}
