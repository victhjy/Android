package com.graceplayer.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.graceplayer.data.Music;
import com.graceplayer.data.MusicList;

public class MainActivity extends Activity {

	// 显示组件
	private ImageButton imgBtn_Previous;
	private ImageButton imgBtn_PlayOrPause;
	private ImageButton imgBtn_Stop;
	private ImageButton imgBtn_Next;
	private ListView list;
	
	//歌曲列表对象
	private ArrayList<Music> musicArrayList;
	
	// 当前歌曲的序号，下标从0开始
	private int number = 0;

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
	}
	/** 获取显示组件 */
	private void findViews() {
		imgBtn_Previous = (ImageButton) findViewById(R.id.imageButton1);
		imgBtn_PlayOrPause = (ImageButton) findViewById(R.id.imageButton2);
		imgBtn_Stop = (ImageButton) findViewById(R.id.imageButton3);
		imgBtn_Next = (ImageButton) findViewById(R.id.imageButton4);
		list = (ListView) findViewById(R.id.listView1);
	}
	
	/** 为显示组件注册监听器 */
	private void registerListeners() {
		imgBtn_Previous.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				moveNumberToPrevious();
				play(number);
				imgBtn_PlayOrPause.setBackgroundResource(R.drawable.button_pause);
			}
		});
		imgBtn_PlayOrPause.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				if (player != null && player.isPlaying()) {
					pause();
					imgBtn_PlayOrPause.setBackgroundResource(R.drawable.button_play);
				} else {
					play(number);
					imgBtn_PlayOrPause.setBackgroundResource(R.drawable.button_pause);
				}
			}
		});
		imgBtn_Stop.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				stop();
				imgBtn_PlayOrPause.setBackgroundResource(R.drawable.button_play);
			}
		});
		imgBtn_Next.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				moveNumberToNext();
				play(number);
				imgBtn_PlayOrPause.setBackgroundResource(R.drawable.button_pause);
			}
		});
		list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				number = position ;
				play(number);
				imgBtn_PlayOrPause.setBackgroundResource(R.drawable.button_pause);
			}
		});
	}
	/**初始化音乐列表对象*/
	private void initMusicList() {
		musicArrayList = MusicList.getMusicList();
		//避免重复添加音乐
		if(musicArrayList.isEmpty())
		{
			Cursor mMusicCursor = this.getContentResolver().query(
					MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
					MediaStore.Audio.AudioColumns.TITLE);
			//标题
			int indexTitle = mMusicCursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE);
			//艺术家
			int indexArtist = mMusicCursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST);
			//总时长
			int indexTotalTime = mMusicCursor.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION);
			//路径
			int indexPath = mMusicCursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA);

			/**通过mMusicCursor游标遍历数据库，并将Music类对象加载带ArrayList中*/
			for (mMusicCursor.moveToFirst(); !mMusicCursor.isAfterLast(); mMusicCursor
					.moveToNext()) { 
				String strTitle = mMusicCursor.getString(indexTitle);
				String strArtist = mMusicCursor.getString(indexArtist);
				String strTotoalTime = mMusicCursor.getString(indexTotalTime);
				String strPath = mMusicCursor.getString(indexPath);

				if (strArtist.equals("<unknown>"))
					strArtist = "无艺术家";
				Music music = new Music(strTitle, strArtist, strPath, strTotoalTime);
				musicArrayList.add(music);
			}
		}
	}
	/**设置适配器并初始化listView*/
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
	
	/**如果列表没有歌曲，则播放按钮不可用，并提醒用户*/
	private void checkMusicfile()
	{
		if (musicArrayList.isEmpty()) {
			imgBtn_Next.setEnabled(false);
			imgBtn_PlayOrPause.setEnabled(false);
			imgBtn_Previous.setEnabled(false);
			imgBtn_Stop.setEnabled(false);
			Toast.makeText(getApplicationContext(), "当前没有歌曲文件",Toast.LENGTH_SHORT).show();
		} else {
			imgBtn_Next.setEnabled(true);
			imgBtn_PlayOrPause.setEnabled(true);
			imgBtn_Previous.setEnabled(true);
			imgBtn_Stop.setEnabled(true);
		}
	}

	// 媒体播放类
	private MediaPlayer player = new MediaPlayer();

	
	/** 读取音乐文件 */
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
	/** 播放音乐 */
	private void play(int number) {
		// 停止当前播放
		if (player != null && player.isPlaying()) {
			player.stop();
		}
		load(number);
		player.start();
	}
	/** 暂停音乐 */
	private void pause() {
		if (player.isPlaying()) {
			player.pause();
		}
	}
	/** 停止播放 */
	private void stop() {
			player.stop();
	}
	/** 恢复播放（暂停之后） */
	private void resume() {
		player.start();
	}
	/** 重新播放（播放完成之后） */
	private void replay() {
		player.start();
	}
	
	/** 选择下一曲 */
	private void moveNumberToNext() {
		// 判断是否到达了列表底端
		if ((number ) == MusicList.getMusicList().size()-1) {
			Toast.makeText(MainActivity.this,MainActivity.this.getString(R.string.tip_reach_bottom),Toast.LENGTH_SHORT).show();
		} else {
				++number;
				play(number);
		}
	}

	/** 选择上一曲 */
	private void moveNumberToPrevious() {
		// 判断是否到达了列表顶端
		if (number == 0) {
			Toast.makeText(MainActivity.this,MainActivity.this.getString(R.string.tip_reach_top),Toast.LENGTH_SHORT).show();
		} else {
			--number;
			play(number);
		}
	}
}
