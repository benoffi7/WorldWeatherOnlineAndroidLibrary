package com.coffeeandcookies.worldweatheronlineandroidlibrary;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
/**
 * 
 * @author Gonzalo Benoffi
 * gitHub URL >> https://github.com/benoffi7/WorldWeatherOnlineAndroidLibrary
 * >>> LEER la WIKI para su correcta instalacion y uso <<<
 * 
 * >>> Coffee And Cookies - Desarrollo de Aplicaciones Android <<<
 * >>> 2013, Mar del Plata, Argentina <<<
 */
public class WigetClima extends LinearLayout
{
	public WigetClima(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		 if(!isInEditMode())
		 {
			 inicializar();
		 }			 
	}

	public WigetClima(Context context)
	{
		super(context);
		if(!isInEditMode())
		{
			 inicializar();
		}
	}
	
	private void inicializar()
	{
	    String infService = Context.LAYOUT_INFLATER_SERVICE;
	    LayoutInflater li = (LayoutInflater)getContext().getSystemService(infService);
	    li.inflate(R.layout.control_clima, this, true);
	    
	    ImageView imageView = (ImageView)findViewById(R.id.imageView1);
		TextView temperaturaActual = (TextView)findViewById(R.id.textView3);
		TextView temperaturaMinima = (TextView)findViewById(R.id.textView2);
		TextView temperaturaMaxima = (TextView)findViewById(R.id.textView1);
			 
	    new ControladoraClima(getContext(), imageView, temperaturaActual, temperaturaMinima, temperaturaMaxima).execute();
	}
}
