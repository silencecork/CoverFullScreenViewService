package com.example.servicetakeover;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Toast;

public class TakeOverService extends Service {

	private Thread mThread;
	
	private View mTakeOverView;
	
	private boolean mIsEnable;
	
	private int mProgress;

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		String action = intent.getAction();

		if ("START".equals(action)) {
			showTakeOverView();
		} else if ("FINISH".equals(action)) {
			removeTakeOverView();
		} else if ("PROGRESS".equals(action)) {
			mProgress = intent.getIntExtra("progress", 0);
			if (mTakeOverView == null) {
				showTakeOverView();
			}
			Log.i("TakeOverService", "alpha " + mProgress);
			ColorDrawable d = new ColorDrawable(Color.RED);
			d.setAlpha(mProgress);
			mTakeOverView.setBackgroundDrawable(d);
		}

		return Service.START_NOT_STICKY;
	}

	

	@Override
	public IBinder onBind(Intent arg0) {
		return new ServiceStatus.Stub() {
			
			@Override
			public boolean isEnable() throws RemoteException {
				return mIsEnable;
			}

			@Override
			public int currentProgress() throws RemoteException {
				return mProgress;
			}
		};
	}
	
	private void removeTakeOverView() {
		mIsEnable = false;
		if (mTakeOverView != null) {
			WindowManager mWM = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
//			try {
//				android.view.WindowManagerImpl impl = 
//					android.view.WindowManagerImpl.getDefault();
//				if (mTakeOverView.getParent() != null) {
//					impl.removeView(mTakeOverView);
//				}
//			} catch (NoSuchMethodError e) {
//				e.printStackTrace();
//				mWM.removeView(mTakeOverView);
//			}
			mWM.removeView(mTakeOverView);
		}
		
		if (mThread != null) {
			mThread.interrupt();
		}
		stopSelf();
	}
	
	private void showTakeOverView() {
		if (mTakeOverView != null) {
			return;
		}
		mIsEnable = true;
		WindowManager mWM = (WindowManager)getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
		WindowManager.LayoutParams params = new WindowManager.LayoutParams();
		try {
			mTakeOverView = LayoutInflater.from(this).inflate(R.layout.takeover, null);
			mTakeOverView.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00FF0000")));
			
			params.height = getResources().getDisplayMetrics().heightPixels;
			params.width = getResources().getDisplayMetrics().widthPixels;
			params.type = WindowManager.LayoutParams.TYPE_TOAST;
			params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
					| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
					| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
			params.format = android.graphics.PixelFormat.TRANSLUCENT;
			params.gravity = Gravity.CENTER;
			
//			android.view.WindowManagerImpl impl = android.view.WindowManagerImpl
//					.getDefault();
//			try {
//				impl.addView(mTakeOverView, params);
//			} catch (IllegalStateException e) {
//				e.printStackTrace();
//			}
			mWM.addView(mTakeOverView, params);
		} catch (NoSuchMethodError e) {
			e.printStackTrace();
			mWM.addView(mTakeOverView, params);
		}
		
		mThread = new Thread() {
			public void run() {
				while (true) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		mThread.start();
	}

	
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		
		if (mTakeOverView != null) {
			WindowManager.LayoutParams params = (LayoutParams) mTakeOverView.getLayoutParams();
			params.height = getResources().getDisplayMetrics().heightPixels;
			params.width = getResources().getDisplayMetrics().widthPixels; 
			
			WindowManager mWM = (WindowManager)getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
			mWM.updateViewLayout(mTakeOverView, params);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Toast.makeText(this, "onDestroy take over service", Toast.LENGTH_LONG).show();
	}

	
}
