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
 * ������Login
 * ���ܣ���ȡ��ҳhtml����*/
public class Login {

	String str_url;
	String str_name, str_pwd;
	public static String PHPSESSID = null;
	
	public Login() {
	}

	/*
	 * ��������URLLoad
	 * ���ܣ���ʼ����½url
	 * ������String���û���������
	 * ����ֵ����*/
	public void URLLoad(String name, String pwd) {
		// TODO Auto-generated constructor stub
		str_name = name;
		str_pwd = pwd;

		str_url = "http://mylib.zhbit.com:8080/reader/redr_verify.php"
				+ "?" + "number=" + str_name + "&" + "passwd=" + str_pwd
				+ "&select=cert_no&returnUrl=";

	}

	/*��������SetURL
	 * ���ܣ���ʼ��url
	 * ������String��url
	 * ����ֵ����*/
	public void SetURL(String url) {
		str_url = url;
	}

	/*��������getHtmlCode
	 * ���ܣ���ȡ��ҳ��html����
	 * ��������
	 * ����ֵ����ҳhtml��*/
	public String getHtmlCode() {
		String result = "";

		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet get = new HttpGet(str_url);

			//����Cookie
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
				
				//������httpClient�õ���cookie
				CookieStore mCookieStore = ((AbstractHttpClient) httpclient)
						.getCookieStore();
				List<Cookie> cookies = mCookieStore.getCookies();
				for (int i = 0; i < cookies.size(); i++) {
					// �����Ƕ�ȡCookie['PHPSESSID']��ֵ���ھ�̬�����У���֤ÿ�ζ���ͬһ��ֵ
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
