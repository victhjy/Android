package com.example.zhbit;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
/*
 * 类名：Login
 * 功能：获取网页html数据*/
public class Login {

	String str_url;
	String str_name, str_pwd;
	public static String PHPSESSID = null;
	
	public Login() {
	}

	/*
	 * 函数名：URLLoad
	 * 功能：初始化登陆url
	 * 参数：String，用户名，密码
	 * 返回值：无*/
	public void URLLoad(String name, String pwd) {
		// TODO Auto-generated constructor stub
		str_name = name;
		str_pwd = pwd;

		str_url = "http://mylib.zhbit.com:8080/reader/redr_verify.php"
				+ "?" + "number=" + str_name + "&" + "passwd=" + str_pwd
				+ "&select=cert_no&returnUrl=";

	}

	/*函数名：SetURL
	 * 功能：初始化url
	 * 参数：String，url
	 * 返回值：无*/
	public void SetURL(String url) {
		str_url = url;
	}

	/*函数名：getHtmlCode
	 * 功能：获取网页的html代码
	 * 参数：无
	 * 返回值：网页html码*/
	public String getHtmlCode() {
		String result = "";

		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet get = new HttpGet(str_url);

			//设置Cookie
			if (null != PHPSESSID) {
				get.setHeader("Cookie", "PHPSESSID=" + PHPSESSID);
				
			}
			HttpResponse response = httpclient.execute(get);

			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				BufferedReader br = new BufferedReader(new InputStreamReader(
						entity.getContent(), "utf-8"));
				String line;
				while ((line = br.readLine()) != null) {

					result += line + "\n";
				}
				
				//保存由httpClient得到的cookie
				CookieStore mCookieStore = ((AbstractHttpClient) httpclient)
						.getCookieStore();
				List<Cookie> cookies = mCookieStore.getCookies();
				for (int i = 0; i < cookies.size(); i++) {
					// 这里是读取Cookie['PHPSESSID']的值存在静态变量中，保证每次都是同一个值
					if ("PHPSESSID".equals(cookies.get(i).getName())) {
						PHPSESSID = cookies.get(i).getValue();
						break;
					}
				}

			}
		} catch (Exception e) {
			return "Fail!" + e.toString();
		}

		return result;

	}

}
