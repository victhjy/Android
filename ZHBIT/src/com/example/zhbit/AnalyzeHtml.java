package com.example.zhbit;



import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
/*
 * ������AnalyzeHtml
 * ���ܣ�����ԭʼ��ҳ������*/
public class AnalyzeHtml {

	public AnalyzeHtml() {
	}

	/*
	 * ��������LoginAnalyze
	 * ���ܣ��ж��û���½�Ƿ�ɹ�
	 * ������String��html��ҳ�ĳ�ʼ����
	 * ����ֵ��Boolean ���ɹ�=true��ʧ��=false
	 * ��ע������ʹ����jsoup-1.7.3.jar������Ҫ�ֶ����
	 */
	public static boolean LoginAnalyze(String htmlcode) 
	{
		if (htmlcode.contains("�������")) {
			return false;
		}
		return true;
	}

	/*
	 * ��������UserNameAnalyze
	 * ���ܣ�������ҳ�е��û���
	 * ������String��html��ҳ�ĳ�ʼ����
	 * ����ֵ��String���û���*/
	public static String UserNameAnalyze(String htmlcode) {
		String userName = "";		
		
		Document doc1 = Jsoup.parse(htmlcode);
		Element content1 = doc1.getElementById("menu");
		
		String text = content1.text().toString();
		String[] list = text.substring(text.indexOf("�ҵ�ͼ���")+"�ҵ�ͼ���".length(), text.indexOf("ע��")).split(" ");

		userName = list[1];
		return userName;
	}

	/*
	 * ��������Booklist
	 * ���ܣ�������ҳ���û����������
	 * ������String��html��ҳ��ԭʼ����
	 * ����ֵ��String[]������L0272634 ������Щ�¶�:��ر�.�ڶ���,��馣����ӻ������� ���������� 2014-01-14 2014-03-15 ��� 
	 * ����¼Ϊ�գ�����String[]��"���ĸ����¼Ϊ��"*/
	
	public static String[] Booklist(String htmlcode)
	{
		String[] list;
		Document doc = Jsoup.parse(htmlcode);
		Element content = doc.getElementById("mylib_content");		//����id�ҵ�������
		
		String text = content.text().toString()+" ";				//ĩβ���Ͽո� 
		
		System.out.println(text);
		if(!text.contains("���ĸ����¼Ϊ��"))
		{
			list = text.substring(text.indexOf("L")).split("�� ");	//���ݡ��� �����зָ�
			UserData.str_NowBookNumber = list.length+"";
			return list;
		}
		return new String[]{"���ĸ����¼Ϊ��"};
		
	}
}


	//��Unicode��ת����
//	public static String parseUnicode(String line) {
//		
//		line = line.replace("&#x", "\\u").replace(";", "");
//		int len = line.length();
//		char[] out = new char[len];// ��������Ժ�Ľ��
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

