package com.lazy.floatwindowdemo;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class FloatWindowBig extends LinearLayout implements OnClickListener{

	public static int viewWidth;
	public static int viewHeight;
	private Context context;

	public FloatWindowBig(Context context) {
		super(context);
		this.context = context;
		LayoutInflater.from(context).inflate(R.layout.float_window_big, this);
		View view = (View)findViewById(R.id.big_window);
		viewWidth = view.getLayoutParams().width;
		viewHeight = view.getLayoutParams().height;
		Button close = (Button)findViewById(R.id.close_btn);
		Button back = (Button)findViewById(R.id.back_btn);
		Button clear = (Button)findViewById(R.id.clear_btn);
		close.setOnClickListener(this);
		back.setOnClickListener(this);
		clear.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_btn:
			MyWindowManager.removeBigWindow();
			MyWindowManager.createSmallWindow();			
			break;
		case R.id.close_btn:
			MyWindowManager.removeSmallWindow();
			MyWindowManager.removeBigWindow();
			Context context = MyApplication.getContext();
			Intent intent = new Intent(context, FloatWindowService.class);
			context.stopService(intent);
			break;
		case R.id.clear_btn:
			clearMemory();
			MyWindowManager.removeBigWindow();
			MyWindowManager.createSmallWindow();			
			break;
		default:
			break;
		}
	}

	private void clearMemory() {
        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);  
        List<RunningAppProcessInfo> infoList = am.getRunningAppProcesses();  
        //List<ActivityManager.RunningServiceInfo> serviceInfos = am.getRunningServices(100);  

        long beforeMem = MyWindowManager.getAvailableMemory();  
        String TAG = "";
        Log.d(TAG, "-----------before memory info : " + beforeMem);  
        int count = 0;  
        if (infoList != null) {  
            for (int i = 0; i < infoList.size(); ++i) {  
                RunningAppProcessInfo appProcessInfo = infoList.get(i);  
                Log.d(TAG, "process name : " + appProcessInfo.processName);  
                //importance �ý��̵���Ҫ�̶�  ��Ϊ����������ֵԽ�;�Խ��Ҫ��  
                Log.d(TAG, "importance : " + appProcessInfo.importance);  

                // һ����ֵ����RunningAppProcessInfo.IMPORTANCE_SERVICE�Ľ��̶���ʱ��û�û��߿ս�����  
                // һ����ֵ����RunningAppProcessInfo.IMPORTANCE_VISIBLE�Ľ��̶��Ƿǿɼ����̣�Ҳ�����ں�̨������  
                if (appProcessInfo.importance > RunningAppProcessInfo.IMPORTANCE_VISIBLE) {  
                    String[] pkgList = appProcessInfo.pkgList;  
                    for (int j = 0; j < pkgList.length; ++j) {//pkgList �õ��ý��������еİ���  
                        Log.d(TAG, "It will be killed, package name : " + pkgList[j]);  
                        if (!"com.lazy.floatwindowdemo".equals(pkgList[j])) {
                        	am.killBackgroundProcesses(pkgList[j]);  
                            count++;  
						}
                    }  
                }  
            }  
        }

        long afterMem = MyWindowManager.getAvailableMemory();  
        Log.d(TAG, "----------- after memory info : " + afterMem);  
        Toast.makeText(context, "clear " + count + " process, "  
                    + (afterMem - beforeMem)/1024 + "M", Toast.LENGTH_LONG).show();
	}

}
