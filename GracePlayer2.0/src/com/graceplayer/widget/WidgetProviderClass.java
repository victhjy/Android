package com.graceplayer.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.graceplayer.activity.MainActivity;
import com.graceplayer.activity.MusicService;
import com.graceplayer.graceplayer.R;

public class WidgetProviderClass extends AppWidgetProvider {
	
	//����㲥��ʶ����
	public static final String BROADCAST_MUSICSERVICE_CONTROL = "MusicService.ACTION_CONTROL";
	public static final String BROADCAST_MUSICSERVICE_UPDATE_STATUS = "MusicService.ACTION_UPDATE";
	
	//�����룬���ݲ�ͬ�������뷢�Ͳ�ͬ�Ĺ㲥
	public static final int RequstCode_StartActivity = 0;
	public static final int RequstCode_Play = 1;
	public static final int RequstCode_Pause = 2;
	public static final int RequstCode_Next = 3;
	public static final int RequstCode_Previous = 4;
	
	//����״̬
	private int status;
	private RemoteViews remoteViews = null;
	private String musicName = null;
	private String musicArtist = null;
	// ��widget��ɾ��ʱ����
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		// TODO Auto-generated method stub
		super.onDeleted(context, appWidgetIds);
		
	}

	// ���һ��widget��ɾ��ʱ����
	@Override
	public void onDisabled(Context context) {
		// TODO Auto-generated method stub
		super.onDisabled(context);
	}

	// ��һ��widget������ʱ����
	@Override
	public void onEnabled(Context context) {
		// TODO Auto-generated method stub
		super.onEnabled(context);
		
	}

	// ���չ㲥�Ļص�����
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.onReceive(context, intent);
		
		if(intent.getAction().equals(BROADCAST_MUSICSERVICE_UPDATE_STATUS))
		{   
			status = intent.getIntExtra("status", -1);
			remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout); 
			switch (status) {
			case MusicService.STATUS_PLAYING:
				//��ȡ����������������
				musicName =  intent.getStringExtra("musicName");
				musicArtist =  intent.getStringExtra("musicArtist");
				
				//�޸ı����Լ���ťͼƬ
				remoteViews.setTextViewText(R.id.widget_tv_title,musicName+" "+musicArtist);  
				remoteViews.setImageViewResource(R.id.widget_btn_play, R.drawable.button_pause);
				
				//����״̬ʱ���������/��ͣ��ť�����ʹ���ָͣ��Ĺ㲥
				Intent intent_pause  = new Intent(BROADCAST_MUSICSERVICE_CONTROL);
				intent_pause.putExtra("command", MusicService.COMMAND_PAUSE);
				PendingIntent pendingIntent_pasue = PendingIntent.getBroadcast(context,RequstCode_Pause, intent_pause, PendingIntent.FLAG_UPDATE_CURRENT);
				remoteViews.setOnClickPendingIntent(R.id.widget_btn_play, pendingIntent_pasue);
				break;
			case MusicService.STATUS_PAUSED:
				//�޸İ�ťͼ��
				remoteViews.setImageViewResource(R.id.widget_btn_play, R.drawable.button_play);
				
				//��ͣ״̬ʱ���������/��ͣ��ť�����ʹ�����ָ��Ĺ㲥
				Intent intent_play  = new Intent(BROADCAST_MUSICSERVICE_CONTROL);
				intent_play.putExtra("command", MusicService.COMMAND_RESUME);
				PendingIntent pendingIntent_play = PendingIntent.getBroadcast(context, RequstCode_Play, intent_play, PendingIntent.FLAG_UPDATE_CURRENT);
				remoteViews.setOnClickPendingIntent(R.id.widget_btn_play, pendingIntent_play);
				break;
			case MusicService.STATUS_STOPPED:
				//�޸ı����Լ���ťͼƬ
				remoteViews.setImageViewResource(R.id.widget_btn_play, R.drawable.button_play);
				remoteViews.setTextViewText(R.id.widget_tv_title, "GracePlayer");
				break;
			default:
				break;
			}
			//���ý�����ʾ������У�����״̬
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);  
            ComponentName componentName = new ComponentName(context,WidgetProviderClass.class);  
            appWidgetManager.updateAppWidget(componentName, remoteViews);  
		}
	}

	// �ڸ���widget ʱ���÷���������
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		// TODO Auto-generated method stub
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout); 
		
		//���͹㲥�����״̬
		Intent intent = new Intent(BROADCAST_MUSICSERVICE_CONTROL);
		intent.putExtra("command", MusicService.COMMAND_CHECK_IS_PLAYING);
		context.sendBroadcast(intent);
		
		//����
		Intent intent_title = new Intent();
		intent_title.setClass(context, MainActivity.class);
		PendingIntent pendingIntent_title = PendingIntent.getActivity(context, RequstCode_StartActivity, intent_title,PendingIntent.FLAG_UPDATE_CURRENT );
		
		//��һ�ף����ʱ���ʹ���һ��ָ��Ĺ㲥
		Intent intent_next = new Intent(BROADCAST_MUSICSERVICE_CONTROL);
		intent_next.putExtra("command",MusicService.COMMAND_NEXT);
		PendingIntent pendingIntent_next = PendingIntent.getBroadcast(context, RequstCode_Next, intent_next,PendingIntent.FLAG_UPDATE_CURRENT);
		
		//��һ�ף����ʱ���ʹ���һ��ָ��Ĺ㲥
		Intent intent_pre = new Intent(BROADCAST_MUSICSERVICE_CONTROL);
		intent_pre.putExtra("command", MusicService.COMMAND_PREVIOUS);
		PendingIntent pendingIntent_pre = PendingIntent.getBroadcast(context, RequstCode_Previous, intent_pre, PendingIntent.FLAG_UPDATE_CURRENT);

		remoteViews.setOnClickPendingIntent(R.id.widget_tv_title, pendingIntent_title);
		remoteViews.setOnClickPendingIntent(R.id.widget_btn_presong, pendingIntent_pre);
		remoteViews.setOnClickPendingIntent(R.id.widget_btn_next, pendingIntent_next);   
		
		appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
	}
}
