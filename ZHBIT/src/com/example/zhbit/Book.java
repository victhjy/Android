package com.example.zhbit;

import android.R.bool;
/*类名：Book
 *功能：记录书的属性*/
public class Book {

	private String str_BookNo; 			// 书编号
	private String str_BookName; 		// 书名
	private String str_BookAuthor; 		// 书作者名
	private String str_BookTime; 		// 借书时间
	private String str_BookReturnTime; 	// 图书归还时间
	private String str_BookRestReturnTime; // 图书剩余归还时间
	private boolean flag_exTime; 			// 超期标记

	public Book() {
		// TODO Auto-generated constructor stub
		flag_exTime = false;
	}

	public Book(String str_BookName, String str_str_BookReturnTime,
			String str_BookRestReturnTime) {
		this.str_BookName = str_BookName;
		this.str_BookReturnTime = str_str_BookReturnTime;
		this.str_BookRestReturnTime = str_BookRestReturnTime;
	}

	public Book(String str_BookNo, String str_BookName, String str_BookAuthor,
			String str_BookTime, String str_BookReturnTime, String str_BookRestReturnTime,
			Boolean flag_exTime) {
		this.str_BookNo = str_BookNo;
		this.str_BookName = str_BookName;
		this.str_BookAuthor = str_BookAuthor;
		this.str_BookTime = str_BookTime;
		this.str_BookReturnTime = str_BookReturnTime;
		this.str_BookRestReturnTime = str_BookRestReturnTime;
		this.flag_exTime = flag_exTime;
		
	}

	public String getBookNo()
	{
		return this.str_BookNo;
	}
	public String getBookName()
	{
		return this.str_BookName;
	}
	public String getBookAuthor()
	{
		return this.str_BookAuthor;
	}
	public String getBookTime()
	{
		return this.str_BookTime;
	}
	public String getBookReturnTime()
	{
		return this.str_BookReturnTime;
	}
	public String getBookRestReturnTime()
	{
		return this.str_BookRestReturnTime;
	}
	public boolean getBookExtimeFlag()
	{
		return this.flag_exTime;
	}
	public void setBookExtimeFlag(boolean flag)
	{
		this.flag_exTime = flag;
	}
	public void setBookName(String a)
	{
		this.str_BookName = a;
	}
	
}
