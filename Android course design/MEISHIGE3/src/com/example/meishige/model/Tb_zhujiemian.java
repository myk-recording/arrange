package com.example.meishige.model;

public class Tb_zhujiemian {
	private int _id;
	private String tupian;
	private String zhuti;
	
	public Tb_zhujiemian()
	{
		super();
	}
	
	public Tb_zhujiemian(int id,String tupian, String zhuti) {
		super();
		this._id = id;
		this.tupian = tupian;
		this.zhuti = zhuti;
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

}

