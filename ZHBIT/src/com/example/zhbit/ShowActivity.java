package com.example.zhbit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ShowActivity extends Activity {

	ListView myListView;
	List<Map<String, String>> list_Map;
	
	TextView textView, textview2;
	
	SimpleAdapter sim_adapter;
	ArrayList<Book> booklist;
	
	private Handler hander;
	String[] list;
	Login httpdown;
	ProgressDialog Pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.showactivity);

		myListView = (ListView) findViewById(R.id.ShowListView);
		textView = (TextView) findViewById(R.id.ShowListTextView);
		textview2 = (TextView) findViewById(R.id.listViewTexttoBookRetReturnDate);

		booklist = new ArrayList<Book>();
		list_Map = new ArrayList<Map<String, String>>();

		String[] from = new String[] { "No", "BookName", "BookNo",
				"BookAuthor", "BookTime", "BookReturnTime",
				"BookRestReturnTime" };
		int[] to = { R.id.listViewTexttoNo, R.id.listViewTexttoBookName,
				R.id.listViewTexttoBookNo, R.id.listViewTexttoBookAuthor,
				R.id.listViewTexttoBookTime, R.id.listViewTexttoBookReturnDate,
				R.id.listViewTexttoBookRetReturnDate };
		sim_adapter = new SimpleAdapter(this, list_Map, R.layout.listview_xml,
				from, to);
		hander = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case 0:

					textView.setText("当前借书情况" + "("
							+ UserData.str_NowBookNumber + "/"
							+ UserData.str_BookNumber + ")");
					sim_adapter.notifyDataSetChanged();
					myListView.setAdapter(sim_adapter);
					if (UserData.str_NowBookNumber == "0")
						setContentView(R.layout.emptybookactivity_xml);

					break;

				default:
					break;
				}
				super.handleMessage(msg);
			}
		};
		Pd = ProgressDialog.show(ShowActivity.this, "请稍等", "加载信息中...", true);
		new Thread() {
			public void run() {
				httpdown = new Login();
				httpdown.SetURL("http://mylib.zhbit.com:8080/reader/book_lst.php");

				// 解析书列表
				list = AnalyzeHtml.Booklist(httpdown.getHtmlCode());
				if (!(UserData.str_NowBookNumber == "0")) {
					booklist = AnalyzeData.getBookObject(list);
					for (Book b : booklist/* UserData.UserBookList */) {
						System.out.println("No:" + b.getBookNo());
						System.out.println("BookName:" + b.getBookName());
						System.out.println("Author:" + b.getBookAuthor());
						System.out.println("BookTime:" + b.getBookTime());
						System.out.println("ReturnTime:"
								+ b.getBookReturnTime());
						System.out.println("RestReturnTime:"
								+ b.getBookRestReturnTime());
						System.out.println("Flag:" + b.getBookExtimeFlag());
					}
				}
				list_Map = getData();
				hander.sendEmptyMessage(0);
				Pd.dismiss();
			};
		}.start();

	}

	private List<Map<String, String>> getData() {

		int num = 0;
		for (Book book : UserData.UserBookList) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("No", "" + ++num);
			map.put("BookName", book.getBookName());
			map.put("BookNo", "编号:" + book.getBookNo());
			map.put("BookAuthor", book.getBookAuthor());
			map.put("BookTime", "借书日期:" + book.getBookTime());
			map.put("BookReturnTime", "还书日期:" + book.getBookReturnTime());
			if (book.getBookExtimeFlag() == false) {
				map.put("BookRestReturnTime",
						"离还书还有" + book.getBookRestReturnTime() + "日  ");

			} else {
				map.put("BookRestReturnTime", "已超期"
						+ book.getBookRestReturnTime().replace("-", "") + "日  ");

			}
			list_Map.add(map);
		}

		return list_Map;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub

		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		booklist.clear();
		UserData.UserBookList.clear();
		UserData.str_BookNumber = "5";
		UserData.str_NowBookNumber = "0";
		super.onDestroy();
	}
}
