package com.example.zhbit;



import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
/*
 * 类名：AnalyzeHtml
 * 功能：解析原始网页的数据*/
public class AnalyzeHtml {

	public AnalyzeHtml() {
	}

	/*
	 * 函数名：LoginAnalyze
	 * 功能：判断用户登陆是否成功
	 * 参数：String，html网页的初始数据
	 * 返回值：Boolean ，成功=true，失败=false
	 * 备注：本类使用了jsoup-1.7.3.jar包，需要手动添加
	 */
	public static boolean LoginAnalyze(String htmlcode) 
	{
		if (htmlcode.contains("密码错误")) {
			return false;
		}
		return true;
	}

	/*
	 * 函数名：UserNameAnalyze
	 * 功能：解析网页中的用户名
	 * 参数：String，html网页的初始数据
	 * 返回值：String，用户名*/
	public static String UserNameAnalyze(String htmlcode) {
		String userName = "";		
		
		Document doc1 = Jsoup.parse(htmlcode);
		Element content1 = doc1.getElementById("menu");
		
		String text = content1.text().toString();
		String[] list = text.substring(text.indexOf("我的图书馆")+"我的图书馆".length(), text.indexOf("注销")).split(" ");

		userName = list[1];
		return userName;
	}

	/*
	 * 函数名：Booklist
	 * 功能：解析网页中用户借书的数据
	 * 参数：String，html网页的原始数据
	 * 返回值：String[]，例：L0272634 明朝那些事儿:典藏本.第二部,朱棣：逆子还是明君 当年明月著 2014-01-14 2014-03-15 书库 
	 * 若记录为空，返回String[]，"您的该项记录为空"*/
	
	public static String[] Booklist(String htmlcode)
	{
		String[] list;
		Document doc = Jsoup.parse(htmlcode);
		Element content = doc.getElementById("mylib_content");		//根据id找到该内容
		
		String text = content.text().toString()+" ";				//末尾添上空格 
		
		System.out.println(text);
		if(!text.contains("您的该项记录为空"))
		{
			list = text.substring(text.indexOf("L")).split("无 ");	//根据“无 ”进行分割
			UserData.str_NowBookNumber = list.length+"";
			return list;
		}
		return new String[]{"您的该项记录为空"};
		
	}
}


	//将Unicode码转中文
//	public static String parseUnicode(String line) {
//		
//		line = line.replace("&#x", "\\u").replace(";", "");
//		int len = line.length();
//		char[] out = new char[len];// 保存解析以后的结果
//		int outLen = 0;
//		for (int i = 0; i < len; i++) {
//			char aChar = line.charAt(i);
//			if (aChar == '\\') {
//				aChar = line.charAt(++i);
//				if (aChar == 'u') {
//					int value = 0;
//					for (int j = 0; j < 4; j++) {
//						aChar = line.charAt(++i);
//						switch (aChar) {
//						case '0':
//						case '1':
//						case '2':
//						case '3':
//						case '4':
//						case '5':
//						case '6':
//						case '7':
//						case '8':
//						case '9':
//							value = (value << 4) + aChar - '0';
//							break;
//						case 'a':
//						case 'b':
//						case 'c':
//						case 'd':
//						case 'e':
//						case 'f':
//							value = (value << 4) + 10 + aChar - 'a';
//							break;
//						case 'A':
//						case 'B':
//						case 'C':
//						case 'D':
//						case 'E':
//						case 'F':
//							value = (value << 4) + 10 + aChar - 'A';
//							break;
//						default:
//							throw new IllegalArgumentException(
//									"Malformed \\uxxxx encoding.");
//						}
//					}
//					out[outLen++] = (char) value;
//				} else {
//					if (aChar == 't')
//						aChar = '\t';
//					else if (aChar == 'r')
//						aChar = '\r';
//					else if (aChar == 'n')
//						aChar = '\n';
//					else if (aChar == 'f')
//						aChar = '\f';
//					out[outLen++] = aChar;
//				}
//			} else {
//				out[outLen++] = aChar;
//			}
//		}
//		return new String(out, 0, outLen);
//	}

