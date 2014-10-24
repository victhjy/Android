package com.example.zhbit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/*
 * 类名：AnalyzeData
 * 功能：解析由AnalyzeHtml处理后的数据、初始化UserData类的ArrayList<Book>静态对象、返回ArrayList<Book>对象
 * */
public class AnalyzeData {

	static String DateNumber = null;
	public AnalyzeData() {
		// TODO Auto-generated constructor stub
	}

	/*
	 * 函数名：getDateNubmer
	 * 功能：返回还书日期到当前日期的天数
	 * 输入参数：String类型，例：2014-2-3
	 * 返回值：String类型，例：22*/
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
	
	/*函数名getflag_exTime
	功能：
	判断当前图书是否超期
	参数：String，-22
	返回值：boolea，true/false
	*/
	
	public static boolean getflag_exTime(String str)
	{
		int a = Integer.parseInt(str);
		if(a>=0) return false;
		else return true;
	}
	
	/*
	 * 函数名：getBookObject
	 * 功能：获取Book对象列表
	 * 输入参数：String ，L0272634 明朝那些事儿:典藏本.第二部,朱棣：逆子还是明君 当年明月著 2014-01-14 2014-03-15 书库
	 * 返回值：ArrayList<Book>，Book类的ArrayList数据对象
	*/
	
	public static ArrayList<Book> getBookObject(String[] strlist) {
		
		ArrayList<Book> booklist = new ArrayList<Book>();
		for(String a:strlist) System.out.println(a);
		if (strlist.toString() != "您的该项记录为空")
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
			book.setBookName("您的该项记录为空");
			booklist.add(book);
			return booklist;
		}
		
	}
}
//返回图书作者
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
