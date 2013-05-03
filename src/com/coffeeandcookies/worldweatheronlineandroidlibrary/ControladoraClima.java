package com.coffeeandcookies.worldweatheronlineandroidlibrary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class ControladoraClima extends AsyncTask<Void, Void, Bitmap>
{
	private static final String TAG = "WorldWeatherOnlineAndroidLibrary";
	private static final String CIUDAD = "Mar del Plata";
	private static final String API = "zrtvh3vmjywa3uzkqej5ahdm";
	
	ImageView imageView;
	TextView temperaturaActual;
	TextView temperaturaMinima;
	TextView temperaturaMaxima;
	
	String temperaturaActualT;
	String temperaturaMinimaT;
	String temperaturaMaximaT;
	String codigo;
	
	ProgressDialog pd;
	
	String URL = "http://api.worldweatheronline.com/free/v1/weather.ashx?q="+CIUDAD.trim().replace(" ", "+")+"&format=json&num_of_days=5&key="+API;
	static Context context;
	
	public ControladoraClima(Context contexto, ImageView imageView, TextView temperaturaActual, TextView temperaturaMinima, TextView temperaturaMaxima)
	{
		ControladoraClima.context = contexto;
		this.imageView = imageView;
		this.temperaturaActual = temperaturaActual;
		this.temperaturaMinima = temperaturaMinima;
		this.temperaturaMaxima = temperaturaMaxima;
	}
	
	@Override
	protected void onPreExecute()
	{
		pd = new ProgressDialog(context);
		pd.setMessage("Bucando clima");
		pd.show();

		super.onPreExecute();
	}
	
	@Override
	protected Bitmap doInBackground(Void... params)
	{
		if (!NecesitoDescargar())
		{
			Log.d(TAG, "No necesito descargar clima");
			return null;
		}
		else
		{
			Log.d(TAG, "Necesito descargar clima");
			try 
	 		{
		 		StringBuilder builder = new StringBuilder();
		 		HttpClient client = new DefaultHttpClient();
		 		HttpGet httpGet = new HttpGet(URL);
		 		Log.d(TAG, URL);
		 		httpGet.setHeader("Accept","application/json");
		 		httpGet.setHeader("Content-Type","application/json");
	 			HttpResponse response = client.execute(httpGet);
	 			StatusLine statusLine = response.getStatusLine();
	 			int statusCode = statusLine.getStatusCode();
	 			if (statusCode == 200) 
	 			{
					HttpEntity entity = response.getEntity();
	 				InputStream content = entity.getContent();
	 				BufferedReader reader = new BufferedReader(new InputStreamReader(content));
	 				String line;
	 				while ((line = reader.readLine()) != null) 
	 				{
	 						builder.append(line);
	 				}
	 				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	 	 			nameValuePairs.add(new BasicNameValuePair("id","2"));
	 	 		 	 			
	 	 	    	if ( builder.toString().length()>0)
	 	 	    	{    		 	    			
	 	 	    			JSONObject obj = new JSONObject(builder.toString());
	 	 	    			JSONObject objData = obj.getJSONObject("data");	
	 	 	    			//current_condition
	 	 	    			JSONArray arrayCurrentCondition = objData.getJSONArray("current_condition");
	 	 	    			Log.d(TAG, "Cantidad de elementos arrayCurrentCondition: "+arrayCurrentCondition.length());
	 	 	    			if (arrayCurrentCondition.length()>0)
	 	 	    			{
		 	 	    			JSONObject objCurrentCondition = arrayCurrentCondition.getJSONObject(0);
		 	 	    			Log.d(TAG, "temp_C: "+objCurrentCondition.getString("temp_C"));
		 	 	    			temperaturaActualT = objCurrentCondition.getString("temp_C");
		 	 	    			Log.d(TAG, "weatherCode: "+objCurrentCondition.getString("weatherCode"));
		 	 	    			codigo = objCurrentCondition.getString("weatherCode");
		 	 	    			JSONArray arrayWeatherIconUrl = objCurrentCondition.getJSONArray("weatherIconUrl");
		 	 	    			Log.d(TAG, "Cantidad de elementos arrayWeatherIconUrl: "+arrayWeatherIconUrl.length());
		 	 	    			JSONObject objWeatherIconUrl = arrayWeatherIconUrl.getJSONObject(0);
		 	 	    			Log.d(TAG, "value: "+objWeatherIconUrl.getString("value"));
		 	 	    			//weather
		 	 	    			Log.d(TAG, "---------------------------------------------------");
		 	 	    			JSONArray arrayWeather = objData.getJSONArray("weather");
		 	 	    			Log.d(TAG, "Cantidad de elementos arrayWeather: "+arrayWeather.length());
		 	 	    			if (arrayWeather.length()>0)
		 	 	    			{
		 	 	    				JSONObject objWeather = arrayWeather.getJSONObject(0);
		 	 	 	    			Log.d(TAG, "tempMaxC: "+objWeather.getString("tempMaxC"));
		 	 	 	    			Log.d(TAG, "tempMinC: "+objWeather.getString("tempMinC"));
		 	 	 	    			temperaturaMaximaT = objWeather.getString("tempMaxC");
		 	 	 	    			temperaturaMinimaT = objWeather.getString("tempMinC");
		 	 	    			}
		 	 	    			
		 	 	    			if (!ControladoraClima.existeCodigo(objCurrentCondition.getString("weatherCode"),objWeatherIconUrl.getString("value")))
		 	 	    			{
		 	 	    				return ControladoraClima.DescargarGuardarImagen(objWeatherIconUrl.getString("value"),objCurrentCondition.getString("weatherCode"));
		 	 	    			}
		 	 	    			else //si lo tengo en la BD
		 	 	    			{
		 	 	    				return asignarImagen(codigo);
		 	 	    			}
	 	 	    			}
	 	 	    			else
	 	 	    			{
	 	 	    				Log.e(TAG, "arrayCurrentCondition vacio");
	 	 	 	 	    		return null;
	 	 	    			}
	 	 	     	}
	 	 	    	else
	 	 	    	{
	 	 	    		Log.e(TAG, "Respuesta vacia");
	 	 	    		return null;
	 	 	    	}
	 			} 
	 			else 
	 			{
	 				Log.e(TAG, "Failed to download file ControladoraClima");
	 				return null;
	 			} 	 			
	 		}
	 	    catch(Exception e)
	 	    {
	 	        Log.e(TAG, "Error ControladoraClima >> "+e.getMessage()); 	
	 	        e.printStackTrace();
	 	        return null;
	 	    }
		}
	}
	
	private Bitmap asignarImagen(String codigo)
	{
		File directory = new File(Environment.getExternalStorageDirectory()+File.separator+"clima-images");
		File imagen=new File(directory,"clima_"+codigo+".png");
		if (imagen.exists())
		{
		    Bitmap myBitmap = BitmapFactory.decodeFile(imagen.getAbsolutePath());
		    Log.d(TAG,"Se encontro imagen clima");	 
		    return myBitmap;
		}
		else
		{
			Log.e(TAG,"No se encontro imagen clima");	    	
		    return null;
		}				
	}

	private boolean NecesitoDescargar()
	{		
		try
		{
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String ultimaActualizacion = prefs.getString("ultimaActualizacion", "2010-01-01 00:00:00");
			
			//ultimaActualizacion = "2013-05-03 09:41:00";
			
			Date ultimaActualizacionD=sdf.parse(ultimaActualizacion);
			Date currentDateandTime =new Date();
			
			long diferencia = currentDateandTime.getTime() - ultimaActualizacionD.getTime();
			long diferenciaHoras = diferencia / (60 * 60 * 1000);

			Log.d(TAG, "Diferencia horas: "+diferenciaHoras);
			
			if (diferenciaHoras>6)
			{				
				return true;
			}
			else
			{
				return false;
			}
		}
		catch (ParseException e)
		{		
			e.printStackTrace();
			return true;
		}
	}

	private static Bitmap DescargarGuardarImagen(String uRL2,String code)
	{
		Bitmap bm = null;
		try 
		{
			 HttpGet httpRequest = null;
             httpRequest = new HttpGet(uRL2);
             HttpClient httpclient = new DefaultHttpClient();
             HttpResponse response = (HttpResponse) httpclient.execute(httpRequest);
             HttpEntity entity = response.getEntity();
             BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
             InputStream instream = bufHttpEntity.getContent();
             bm = BitmapFactory.decodeStream(instream);
             Log.d(TAG, "Imagen OK: "+uRL2);
             String file_path = Environment.getExternalStorageDirectory()+File.separator+"clima-images";
     		 File dir = new File(file_path);  	 
     		 if(!dir.exists())
     		    				dir.mkdirs();
     		 File file = new File(dir, "clima_"+code+".png");
     		 FileOutputStream fOut = new FileOutputStream(file);
     		 bm.compress(Bitmap.CompressFormat.PNG, 90, fOut);
     		 fOut.flush();
     		 fOut.close();
     		 Log.d(TAG, "Imagen descargada OK y guardada OK");
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			Log.e(TAG, "Imagen NO descargada OK");
		}
		return bm;
	}

	private static boolean existeCodigo(String code, String URL)
	{
		DAO_clima dao = new DAO_clima(context);
		return dao.existeCodigo(code, URL);
	}	
	
	@Override
	protected void onPostExecute(Bitmap result)
	{
		if (pd!=null && pd.isShowing())
		{
			pd.dismiss();
		}
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
	
		if (result!=null)
		{
			imageView.setImageBitmap(result);			
			SharedPreferences.Editor editor = prefs.edit();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String currentDateandTime = sdf.format(new Date());
            editor.putString("temperaturaActual",temperaturaActualT);
            editor.putString("temperaturaMinima",temperaturaMinimaT);
            editor.putString("temperaturaMaxima",temperaturaMaximaT);
            editor.putString("codigoClima",codigo);
            editor.putString("ultimaActualizacion",currentDateandTime);
            editor.commit();       
		}
		else
		{
			temperaturaActualT = prefs.getString("temperaturaActual", "10");
			temperaturaMaximaT = prefs.getString("temperaturaMaxima", "20");
			temperaturaMinimaT = prefs.getString("temperaturaMinima", "15");
			result = asignarImagen(prefs.getString("codigoClima", "1"));
			if (result!=null)
			{
				imageView.setImageBitmap(result);	
			}
		}
		
		temperaturaActual.setText("Actual: "+temperaturaActualT+" °C");
		temperaturaMaxima.setText("Maxima: "+temperaturaMaximaT+" °C");
		temperaturaMinima.setText("Minima: "+temperaturaMinimaT+" °C");
		super.onPostExecute(result);
	}
}
