package com.graceplayer.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.graceplayer.data.Music;
import com.graceplayer.data.MusicList;
import com.graceplayer.model.PropertyBean;

public class MainActivity extends Activity {

	// ��ʾ���
	private ImageButton imgBtn_Previous;
	private ImageButton imgBtn_PlayOrPause;
	private ImageButton imgBtn_Stop;
	private ImageButton imgBtn_Next;
	private ListView list;
	private TextView text_Current;
	private TextView text_Duration;
	private SeekBar seekBar;
	private RelativeLayout root_Layout;
	// ���½�������Handler
	private Handler seekBarHandler;
	// ��ǰ�����ĳ���ʱ��͵�ǰλ�ã������ڽ�����
	private int duration;
	private int time;
	// ���������Ƴ���
	private static final int PROGRESS_INCREASE = 0;
	private static final int PROGRESS_PAUSE = 1;
	private static final int PROGRESS_RESET = 2;
	//�����б����
	private ArrayList<Music> musicArrayList;
	// ��ǰ��������ţ��±��0��ʼ
	private int number = 0;
	// ����״̬
	private int status;
	// �㲥������
	private StatusChangedReceiver receiver;

	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		findViews();
		registerListeners();
		initMusicList();
		initListView();
		checkMusicfile();
		duration = 0;
		time = 0;
		// �󶨹㲥�����������Խ��չ㲥
		bindStatusChangedReceiver();
		initSeekBarHandler();
		startService(new Intent(this, MusicService.class));
		status = MusicService.COMMAND_STOP;
		
	}
	/** �󶨹㲥������ */
	private void bindStatusChangedReceiver() {
		receiver = new StatusChangedReceiver();
		IntentFilter filter = new IntentFilter(
				MusicService.BROADCAST_MUSICSERVICE_UPDATE_STATUS);
		registerReceiver(receiver, filter);
	}
	/** ��ȡ��ʾ��� */
	private void findViews() {
		imgBtn_Previous = (ImageButton) findViewById(R.id.imageButton1);
		imgBtn_PlayOrPause = (ImageButton) findViewById(R.id.imageButton2);
		imgBtn_Stop = (ImageButton) findViewById(R.id.imageButton3);
		imgBtn_Next = (ImageButton) findViewById(R.id.imageButton4);
		list = (ListView) findViewById(R.id.listView1);
		seekBar = (SeekBar) findViewById(R.id.seekBar1);
		text_Current = (TextView) findViewById(R.id.textView1);
		text_Duration = (TextView) findViewById(R.id.textView2);
		root_Layout = (RelativeLayout) findViewById(R.id.relativeLayout1);
	}
	
	/** Ϊ��ʾ���ע������� */
	private void registerListeners() {
		imgBtn_Previous.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				sendBroadcastOnCommand(MusicService.COMMAND_PREVIOUS);
			}
		});
		imgBtn_PlayOrPause.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				switch (status) {
				case MusicService.STATUS_PLAYING:
					sendBroadcastOnCommand(MusicService.COMMAND_PAUSE);
					break;
				case MusicService.STATUS_PAUSED:
					sendBroadcastOnCommand(MusicService.COMMAND_RESUME);
					break;
				case MusicService.COMMAND_STOP:
					sendBroadcastOnCommand(MusicService.COMMAND_PLAY);
				default:
					break;
				}
			}
		});
		imgBtn_Stop.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				sendBroadcastOnCommand(MusicService.COMMAND_STOP);
			}
		});
		imgBtn_Next.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				sendBroadcastOnCommand(MusicService.COMMAND_NEXT);
			}
		});
		list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				number = position;
				sendBroadcastOnCommand(MusicService.COMMAND_PLAY);
			}
		});
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				if (status == MusicService.STATUS_PLAYING) {
					// ���͹㲥��MusicService��ִ����ת
					sendBroadcastOnCommand(MusicService.COMMAND_SEEK_TO);
					// �������ָ��ƶ�
					seekBarHandler.sendEmptyMessageDelayed(PROGRESS_INCREASE,
							1000);
				}
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// ��������ͣ�ƶ�
				seekBarHandler.sendEmptyMessage(PROGRESS_PAUSE);
			}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (status != MusicService.STATUS_STOPPED) {
					time = progress;
					// �����ı�
					text_Current.setText(formatTime(time));
				}
			}
		});
	}
	/**��ʼ�������б����*/
	private void initMusicList() {
		musicArrayList = MusicList.getMusicList();
		//�����ظ��������
		if(musicArrayList.isEmpty())
		{
			Cursor mMusicCursor = this.getContentResolver().query(
					MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
					MediaStore.Audio.AudioColumns.TITLE);
			//����
			int indexTitle = mMusicCursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE);
			//������
			int indexArtist = mMusicCursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST);
			//��ʱ��
			int indexTotalTime = mMusicCursor.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION);
			//·��
			int indexPath = mMusicCursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA);

			/**ͨ��mMusicCursor�α�������ݿ⣬����Music�������ش�ArrayList��*/
			for (mMusicCursor.moveToFirst(); !mMusicCursor.isAfterLast(); mMusicCursor
					.moveToNext()) { 
				String strTitle = mMusicCursor.getString(indexTitle);
				String strArtist = mMusicCursor.getString(indexArtist);
				String strTotoalTime = mMusicCursor.getString(indexTotalTime);
				String strPath = mMusicCursor.getString(indexPath);

				if (strArtist.equals("<unknown>"))
					strArtist = "��������";
				Music music = new Music(strTitle, strArtist, strPath, strTotoalTime);
				musicArrayList.add(music);
			}
		}
	}
	/**��������������ʼ��listView*/
	private void initListView() {
		List<Map<String, String>> list_map = new ArrayList<Map<String, String>>();
		HashMap<String, String> map;
		SimpleAdapter simpleAdapter;
		for (Music music : musicArrayList) {
			map = new HashMap<String, String>();
			map.put("musicName", music.getmusicName());
			map.put("musicArtist", music.getmusicArtist());
			list_map.add(map);
		}

		String[] from = new String[] { "musicName", "musicArtist" };
		int[] to = { R.id.listview_tv_title_item, R.id.listview_tv_artist_item };

		simpleAdapter = new SimpleAdapter(this, list_map, R.layout.listview,from, to);
		list.setAdapter(simpleAdapter);
	}
	
	/**����б�û�и������򲥷Ű�ť�����ã��������û�*/
	private void checkMusicfile()
	{
		if (musicArrayList.isEmpty()) {
			imgBtn_Next.setEnabled(false);
			imgBtn_PlayOrPause.setEnabled(false);
			imgBtn_Previous.setEnabled(false);
			imgBtn_Stop.setEnabled(false);
			Toast.makeText(getApplicationContext(), "��ǰû�и����ļ�",Toast.LENGTH_SHORT).show();
		} else {
			imgBtn_Next.setEnabled(true);
			imgBtn_PlayOrPause.setEnabled(true);
			imgBtn_Previous.setEnabled(true);
			imgBtn_Stop.setEnabled(true);
		}
	}
	/** ��ʽ�������� -> "mm:ss" */
	private String formatTime(int msec) {
		int minute = (msec / 1000) / 60;
		int second = (msec / 1000) % 60;
		String minuteString;
		String secondString;
		if (minute < 10) {
			minuteString = "0" + minute;
		} else {
			minuteString = "" + minute;
		}
		if (second < 10) {
			secondString = "0" + second;
		} else {
			secondString = "" + second;
		}
		return minuteString + ":" + secondString;
	}
	private void initSeekBarHandler() {
		seekBarHandler = new Handler() {
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case PROGRESS_INCREASE:
					if (seekBar.getProgress() < duration) {
						// ������ǰ��1��
						seekBar.incrementProgressBy(1000);
						seekBarHandler.sendEmptyMessageDelayed(PROGRESS_INCREASE, 1000);
						// �޸���ʾ��ǰ���ȵ��ı�
						text_Current.setText(formatTime(time));
						time += 1000;
					}
					break;
				case PROGRESS_PAUSE:
					seekBarHandler.removeMessages(PROGRESS_INCREASE);
					break;
				case PROGRESS_RESET:
					// ���ý���������
					seekBarHandler.removeMessages(PROGRESS_INCREASE);
					seekBar.setProgress(0);
					text_Current.setText("00:00");
					break;
				}
			}
		};
	}
	/** ��������������ֲ��š�����������MusicService���� */
	private void sendBroadcastOnCommand(int command) {

		Intent intent = new Intent(MusicService.BROADCAST_MUSICSERVICE_CONTROL);
		intent.putExtra("command", command);
		// ���ݲ�ͬ�����װ��ͬ������
		switch (command) {
		case MusicService.COMMAND_PLAY:
			intent.putExtra("number", number);
			break;
		case MusicService.COMMAND_SEEK_TO:
			intent.putExtra("time", time);
			break;
		case MusicService.COMMAND_PREVIOUS:
		case MusicService.COMMAND_NEXT:
		case MusicService.COMMAND_PAUSE:
		case MusicService.COMMAND_STOP:
		case MusicService.COMMAND_RESUME:
		default:
			break;
		}
		sendBroadcast(intent);
	}
	/** �ڲ��࣬���ڲ�����״̬���µĽ��չ㲥 */
	class StatusChangedReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			// ��ȡ������״̬
			status = intent.getIntExtra("status", -1);
			switch (status) {
			case MusicService.STATUS_PLAYING:
				String musicName = intent.getStringExtra("musicName");
				String musicArtist = intent.getStringExtra("musicArtist");
				seekBarHandler.removeMessages(PROGRESS_INCREASE);
				time = intent.getIntExtra("time", 0);
				duration = intent.getIntExtra("duration", 0);
				number = intent.getIntExtra("number", number);
				list.setSelection(number);
				seekBar.setProgress(time);
				seekBar.setMax(duration);
				seekBarHandler.sendEmptyMessageDelayed(PROGRESS_INCREASE, 1000);
				text_Duration.setText(formatTime(duration));
				imgBtn_PlayOrPause.setBackgroundResource(R.drawable.pause);
				// ����Activity�ı��������֣���ʾ���ڲ��ŵĸ���
				MainActivity.this.setTitle("���ڲ���:" + musicName + " "+ musicArtist);
				break;
			case MusicService.STATUS_PAUSED:
				seekBarHandler.sendEmptyMessage(PROGRESS_PAUSE);
				String string = MainActivity.this.getTitle().toString().replace("���ڲ���:", "����ͣ:");
				MainActivity.this.setTitle(string);
				imgBtn_PlayOrPause.setBackgroundResource(R.drawable.play);
				break;
			case MusicService.STATUS_STOPPED:
				time = 0;
				duration = 0;
				text_Current.setText(formatTime(time));
				text_Duration.setText(formatTime(duration));
				seekBarHandler.sendEmptyMessage(PROGRESS_RESET);
				MainActivity.this.setTitle("GracePlayer");
				imgBtn_PlayOrPause.setBackgroundResource(R.drawable.play);
				break;
			case MusicService.STATUS_COMPLETED:
				number = intent.getIntExtra("number", 0);
				if(number == MusicList.getMusicList().size()-1) 
					sendBroadcastOnCommand(MusicService.STATUS_STOPPED);
				else
					sendBroadcastOnCommand(MusicService.COMMAND_NEXT);
				seekBarHandler.sendEmptyMessage(PROGRESS_RESET);
				MainActivity.this.setTitle("GracePlayer");
				imgBtn_PlayOrPause.setBackgroundResource(R.drawable.play);
				break;
			default:
				break;
			}
		}
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (status == MusicService.STATUS_STOPPED) {
			stopService(new Intent(this, MusicService.class));
		}
		super.onDestroy();
	}
	/** ����Activity�����⣬�����޸ı���ͼƬ�� */
	private void setTheme(String theme) {
		if ("��ɫ".equals(theme)) {
			root_Layout.setBackgroundResource(R.drawable.bg_color);
		} else if ("����".equals(theme)) {
			root_Layout.setBackgroundResource(R.drawable.bg_digit_flower);
		} else if ("Ⱥɽ".equals(theme)) {
			root_Layout.setBackgroundResource(R.drawable.bg_mountain);
		} else if ("С��".equals(theme)) {
			root_Layout.setBackgroundResource(R.drawable.bg_running_dog);
		} else if ("��ѩ".equals(theme)) {
			root_Layout.setBackgroundResource(R.drawable.bg_snow);
		} else if ("Ů��".equals(theme)) {
			root_Layout.setBackgroundResource(R.drawable.bg_music_girl);
		} else if ("����".equals(theme)) {
			root_Layout.setBackgroundResource(R.drawable.bg_blur);
		}
	}

	/** �����˵� */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		sendBroadcastOnCommand(MusicService.COMMAND_CHECK_IS_PLAYING);
		PropertyBean property = new PropertyBean(MainActivity.this);
		String theme = property.getTheme();
		// ����Activity������
		setTheme(theme);
	}

	/** ����˵�����¼� */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_theme:
			// ��ʾ�б�Ի���
			new AlertDialog.Builder(this)
					.setTitle("��ѡ������")
					.setItems(R.array.theme,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									// ��ȡ��array.xml�ж������������
									String theme = PropertyBean.THEMES[which];
									// ����Activity������
									setTheme(theme);
									// ����ѡ�������
									PropertyBean property = new PropertyBean(
											MainActivity.this);
									property.setAndSaveTheme(theme);
								}
							}).show();
			break;
		case R.id.menu_about:
			// ��ʾ�ı��Ի���
			new AlertDialog.Builder(MainActivity.this).setTitle("GracePlayer")
					.setMessage(R.string.about2).show();
			break;
		case R.id.menu_quit:
			//�˳�����
			new AlertDialog.Builder(MainActivity.this).setTitle("��ʾ")
			.setMessage(R.string.quit_message).setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					System.exit(0);
				}
			}).setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
				}
			}).show();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	// ý�岥����
	private MediaPlayer player = new MediaPlayer();

	
	/** ��ȡ�����ļ� */
	private void load(int number) {
		try {
			player.reset();
			player.setDataSource(MusicList.getMusicList().get(number).getmusicPath());
			player.prepare();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/** �������� */
	private void play(int number) {
		// ֹͣ��ǰ����
		if (player != null && player.isPlaying()) {
			player.stop();
		}
		load(number);
		player.start();
	}
	/** ��ͣ���� */
	private void pause() {
		if (player.isPlaying()) {
			player.pause();
		}
	}
	/** ֹͣ���� */
	private void stop() {
			player.stop();
	}
	/** �ָ����ţ���֮ͣ�� */
	private void resume() {
		player.start();
	}
	/** ���²��ţ��������֮�� */
	private void replay() {
		player.start();
	}
	
	/** ѡ����һ�� */
	private void moveNumberToNext() {
		// �ж��Ƿ񵽴����б�׶�
		if ((number ) == MusicList.getMusicList().size()-1) {
			Toast.makeText(MainActivity.this,MainActivity.this.getString(R.string.tip_reach_bottom),Toast.LENGTH_SHORT).show();
		} else {
				++number;
				play(number);
		}
	}

	/** ѡ����һ�� */
	private void moveNumberToPrevious() {
		// �ж��Ƿ񵽴����б���
		if (number == 0) {
			Toast.makeText(MainActivity.this,MainActivity.this.getString(R.string.tip_reach_top),Toast.LENGTH_SHORT).show();
		} else {
			--number;
			play(number);
		}
	}
}
