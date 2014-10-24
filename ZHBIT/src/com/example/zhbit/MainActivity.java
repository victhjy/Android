package com.example.zhbit;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private EditText username, userpwd;
	private Button login, cancel;
	private CheckBox ch_Rpwd,ch_useweb;
	private TextView tv_del_name,tv_del_pwd;
	String str_name, str_pwd;
	
	SharedPreferences sharedPreferences;
	Editor editor;
	
	Login loginHtml;
	Handler handler;
	ProgressDialog Pd;
	
	boolean bool_useWeb ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();
		
		
		loginHtml = new Login();

		username = (EditText) findViewById(R.id.MainEditTexttoUserName);
		userpwd = (EditText) findViewById(R.id.MainEditTexttoPWD);
		login = (Button) findViewById(R.id.MainButtontoLogin);
		cancel = (Button) findViewById(R.id.MainButtontoCancel);
		ch_Rpwd = (CheckBox)findViewById(R.id.MainCheckBoxtoRemember);
		ch_useweb =(CheckBox)findViewById(R.id.MainCheckBoxtoUseWeb);
		tv_del_name = (TextView)findViewById(R.id.MainTextViewtoDel1);
		tv_del_pwd = (TextView)findViewById(R.id.MainTextViewtoDel2);
		
		Init();
		//handle用于更新用户UI
		handler = new Handler()
		{
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case 0://密码错误提醒
					(Toast.makeText(getApplicationContext(), "密码错误！",
							Toast.LENGTH_SHORT)).show();
					break;

				case 1://不使用浏览器查看
					(Toast.makeText(getApplicationContext(), "欢迎！"+UserData.str_UserName
							, Toast.LENGTH_SHORT)).show();
					//保存用户名密码
					editor.putString("username", str_name);
					editor.putString("userpwd", str_pwd);
					
					editor.commit();
					
					Intent intent = new Intent();
					intent.setClass(MainActivity.this, ShowActivity.class);
					startActivity(intent);
					break;
				case 2://使用浏览器查看
					(Toast.makeText(getApplicationContext(), "欢迎！"+UserData.str_UserName
							, Toast.LENGTH_SHORT)).show();
					//保存用户名密码
					editor.putString("username", str_name);
					editor.putString("userpwd", str_pwd);
					editor.commit();
					
					Intent intent1 = new Intent();
					intent1.setClass(MainActivity.this, WebShowActivity.class);
					intent1.putExtra("URL", "http://mylib.zhbit.com:8080/reader/redr_verify.php"
				+ "?" + "number=" + str_name + "&" + "passwd=" + str_pwd
				+ "&select=cert_no&returnUrl=");
					startActivity(intent1);
					break;
				}
			};
		};
		//登陆按钮响应事件
		login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				str_name = username.getText().toString();
				str_pwd = userpwd.getText().toString();
				if (str_name.length() != 12) {
					(Toast.makeText(getApplicationContext(), "用户名格式不正确！",
							Toast.LENGTH_SHORT)).show();
				} else {
					
					Pd = ProgressDialog.show(MainActivity.this, "请稍等", "登陆中...", true);
					new Thread(){
						@Override
						public void run() {
							// TODO Auto-generated method stub
							loginHtml.URLLoad(str_name, str_pwd);
							String str_htmlcode = loginHtml.getHtmlCode();
		 
							if (AnalyzeHtml.LoginAnalyze(str_htmlcode) == false) {
								handler.sendEmptyMessage(0);
								Pd.dismiss();
							} else {
								
								UserData.str_UserName = AnalyzeHtml.UserNameAnalyze(str_htmlcode);
								if(bool_useWeb == false)handler.sendEmptyMessage(1);
								else handler.sendEmptyMessage(2);
								Pd.dismiss();
							}
							
						}
					}.start();
					
				}
			}
		});
		//退出按钮响应事件
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
					System.exit(RESULT_CANCELED);
			}
		});
		//删除用户名及密码
		tv_del_name.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				username.setText("");
				userpwd.setText("");
			}
		});
		//删除密码
		tv_del_pwd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				userpwd.setText("");
			}
		});
		//记住密码响应
		ch_Rpwd.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if(arg1==true)
				{
					editor.putBoolean("Remember", true);
					editor.commit();
				}
				else
				{
					editor.putBoolean("Remember", false);
					editor.commit();
				}
			}
		});

		//使用web查看响应
		ch_useweb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if(arg1 == true) bool_useWeb = true;
				else bool_useWeb = false;
			}
		});
		// 监听输入框的变化
		username.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				userpwd.setText("");
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}
		});
	}

	/*
	 * 函数名：Init
	 * 功能：初始化数据
	 * 参数：无
	 * 返回值：无*/
	public void Init()
	{
		bool_useWeb = false;
		if(sharedPreferences.getBoolean("Remember", false)==true)
		{
			username.setText(sharedPreferences.getString("username", ""));
			userpwd.setText(sharedPreferences.getString("userpwd", ""));
			ch_Rpwd.setChecked(true);
		}
		else
		{
			username.setText(sharedPreferences.getString("username", ""));
		}
		UserData.str_NowBookNumber = 0+"";
		UserData.str_BookNumber = 5+"";
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
