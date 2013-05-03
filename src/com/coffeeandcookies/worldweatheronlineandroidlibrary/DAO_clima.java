package com.coffeeandcookies.worldweatheronlineandroidlibrary;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DAO_clima extends SQLiteOpenHelper
{
	Context contexto;
	
	public DAO_clima(Context context)
	{		
		super(context, "clima", null, 1);
		contexto=context;
	}
	
	//VARIABLES
	private SQLiteDatabase baseDatos;
	private String sql;

	//METODOS
	@Override
	public void onCreate(SQLiteDatabase db) 
	{
		sql="CREATE TABLE clima (code TEXT, url TEXT )";
		db.execSQL(sql);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{}
	
	public boolean existeCodigo(String code,String URL) 
	{
		baseDatos = getWritableDatabase();
		sql = "SELECT * FROM clima where code = "+code;
		Cursor cursor = baseDatos.rawQuery(sql, null);
		if (cursor.getCount()>0)
		{
			baseDatos.close();
			cursor.close();
			return true;
		}
		else
		{	
			sql="INSERT into clima (code,url) "+"values ('"+ code +"','"+ URL +"')";
			baseDatos.execSQL(sql);
			baseDatos.close();
			cursor.close();
			return false;
		}
	}
		
	public void limpiarTabla()
	{
		baseDatos=getWritableDatabase();
		sql="DELETE FROM clima";
		baseDatos.execSQL(sql);
		baseDatos.close();
	}
}