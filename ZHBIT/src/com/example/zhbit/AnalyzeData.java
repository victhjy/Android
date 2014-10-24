package com.example.zhbit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/*
 * ������AnalyzeData
 * ���ܣ�������AnalyzeHtml���������ݡ���ʼ��UserData���ArrayList<Book>��̬���󡢷���ArrayList<Book>����
 * */
public class AnalyzeData {

	static String DateNumber = null;
	public AnalyzeData() {
		// TODO Auto-generated constructor stub
	}

	/*
	 * ��������getDateNubmer
	 * ���ܣ����ػ������ڵ���ǰ���ڵ�����
	 * ���������String���ͣ�����2014-2-3
	 * ����ֵ��String���ͣ�����22*/
	public static String getDateNubmer(String str_date)
	{
		long q = 0;
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cale = Calendar.getInstance();
		String str_date_now =  cale.get(Calendar.YEAR)+"-"+(cale.get(Calendar.MONTH)+1)+"-"+cale.get(Calendar.DATE);

		try {
			Date nowdate = ft.parse( str_date_now );
			Date returnbookdate = ft.parse(str_date);
			
			q = (returnbookdate.getTime()-nowdate.getTime());
			q = q/1000/60/60/24;
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return q+"";
		   
	}
	
	/*������getflag_exTime
	���ܣ�
	�жϵ�ǰͼ���Ƿ���
	������String��-22
	����ֵ��boolea��true/false
	*/
	
	public static boolean getflag_exTime(String str)
	{
		int a = Integer.parseInt(str);
		if(a>=0) return false;
		else return true;
	}
	
	/*
	 * ��������getBookObject
	 * ���ܣ���ȡBook�����б�
	 * ���������String ��L0272634 ������Щ�¶�:��ر�.�ڶ���,��馣����ӻ������� ���������� 2014-01-14 2014-03-15 ���
	 * ����ֵ��ArrayList<Book>��Book���ArrayList���ݶ���
	*/
	
	public static ArrayList<Book> getBookObject(String[] strlist) {
		
		ArrayList<Book> booklist = new ArrayList<Book>();
		for(String a:strlist) System.out.println(a);
		if (strlist.toString() != "���ĸ����¼Ϊ��")
		{
			
			for (String a : strlist) {
				
				String[] b = a.split(" ");
				DateNumber = getDateNubmer(b[b.length - 2]);
				System.out.println("RTime:"+b[b.length - 2]);
				Book book = new Book(b[0], b[1], b[2],
						b[b.length - 3], b[b.length - 2],
						DateNumber, getflag_exTime(DateNumber));
				booklist.add(book);
				UserData.UserBookList.add(book);
			}
			return booklist;
		}
		else
		{
			Book book = new Book();
			book.setBookName("���ĸ����¼Ϊ��");
			booklist.add(book);
			return booklist;
		}
		
	}
}
//����ͼ������
//public static String getBookAuthor(String[] list)
//{
//	String BookAuthor = "";
//	
//	if(list.length == 7) BookAuthor = list[2];
//	else
//	{
//		for(int i=0;i<list.length-6;i++)
//		{
//			BookAuthor += list[2+i];
//		}
//	}
//	return BookAuthor;
//}
