package com.example.meishige.model;

public class Tb_tianjia {
	private int _id;
	private String tupian;
	private String zhuti;
	private String time;
	private String didian;
	private String ganchu;
	
	public Tb_tianjia()
	{
		super();
	}
	
	public Tb_tianjia(int id,String tupian, String zhuti,
			String time,String didian,String ganchu) {
		super();
		this._id = id;
		this.tupian = tupian;
		this.zhuti = zhuti;
		this.time = time;
		this.didian = didian;
		this.ganchu = ganchu;
	}
	
	public int getid()
	{
		return _id;
	}
	
	public void setid(int id)
	{
		this._id = id;
	}
	
	public String gettupian()
	{
		return tupian;
	}
	
	public void settupian(String tupian)
	{
		this.tupian = tupian;
	}
	
	public String getzhuti()
	{
		return zhuti;
	}
	
	public void setzhuti(String zhuti)
	{
		this.zhuti = zhuti;
	}
	
	public String gettime()
	{
		return time;
	}
	
	public void settime(String time)
	{
		this.time = time;
	}
	
	public String getdidian()
	{
		return didian;
	}
	
	public void setdidian(String didian)
	{
		this.didian = didian;
	}
	
	public String getganchu()
	{
		return ganchu;
	}
	
	public void setganchu(String ganchu)
	{
		this.ganchu = ganchu;
	}

}
