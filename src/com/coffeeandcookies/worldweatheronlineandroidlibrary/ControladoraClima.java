package com.coffeeandcookies.worldweatheronlineandroidlibrary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
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

/**
 * Clase principal de la libreria. 
 * 
 * @author Gonzalo Benoffi
 * gitHub URL >> https://github.com/benoffi7/WorldWeatherOnlineAndroidLibrary
 * >>> LEER la WIKI para su correcta instalacion y uso <<<
 * 
 * >>> Coffee And Cookies - Desarrollo de Aplicaciones Android <<<
 * >>> 2013, Mar del Plata, Argentina
 */
public class ControladoraClima extends AsyncTask<Void, Void, Bitmap>
{
	ImageView imageView;
	TextView temperaturaActual;
	TextView temperaturaMinima;
	TextView temperaturaMaxima;
	
	String temperaturaActualT;
	String temperaturaMinimaT;
	String temperaturaMaximaT;
	String codigo;
	
	ProgressDialog pd;
	
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
			Log.d(Config.TAG, "No necesito descargar clima");
			return null;
		}
		else
		{
			Log.d(Config.TAG, "Necesito descargar clima");
			try 
	 		{
		 		StringBuilder builder = new StringBuilder();
		 		HttpClient client = new DefaultHttpClient();
		 		HttpGet httpGet = new HttpGet(Config.getUrl());
		 		Log.d(Config.TAG, Config.getUrl());
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
	 	 	    			Log.d(Config.TAG, "Cantidad de elementos arrayCurrentCondition: "+arrayCurrentCondition.length());
	 	 	    			if (arrayCurrentCondition.length()>0)
	 	 	    			{
		 	 	    			JSONObject objCurrentCondition = arrayCurrentCondition.getJSONObject(0);
		 	 	    			Log.d(Config.TAG, "temp_C: "+objCurrentCondition.getString("temp_C"));
		 	 	    			temperaturaActualT = objCurrentCondition.getString("temp_C");
		 	 	    			Log.d(Config.TAG, "weatherCode: "+objCurrentCondition.getString("weatherCode"));
		 	 	    			codigo = objCurrentCondition.getString("weatherCode");
		 	 	    			JSONArray arrayWeatherIconUrl = objCurrentCondition.getJSONArray("weatherIconUrl");
		 	 	    			Log.d(Config.TAG, "Cantidad de elementos arrayWeatherIconUrl: "+arrayWeatherIconUrl.length());
		 	 	    			JSONObject objWeatherIconUrl = arrayWeatherIconUrl.getJSONObject(0);
		 	 	    			Log.d(Config.TAG, "value: "+objWeatherIconUrl.getString("value"));
		 	 	    			//weather
		 	 	    			Log.d(Config.TAG, "---------------------------------------------------");
		 	 	    			JSONArray arrayWeather = objData.getJSONArray("weather");
		 	 	    			Log.d(Config.TAG, "Cantidad de elementos arrayWeather: "+arrayWeather.length());
		 	 	    			if (arrayWeather.length()>0)
		 	 	    			{
		 	 	    				JSONObject objWeather = arrayWeather.getJSONObject(0);
		 	 	 	    			Log.d(Config.TAG, "tempMaxC: "+objWeather.getString("tempMaxC"));
		 	 	 	    			Log.d(Config.TAG, "tempMinC: "+objWeather.getString("tempMinC"));
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
	 	 	    				Log.e(Config.TAG, "arrayCurrentCondition vacio");
	 	 	 	 	    		return null;
	 	 	    			}
	 	 	     	}
	 	 	    	else
	 	 	    	{
	 	 	    		Log.e(Config.TAG, "Respuesta vacia");
	 	 	    		return null;
	 	 	    	}
	 			} 
	 			else 
	 			{
	 				Log.e(Config.TAG, "Failed to download file ControladoraClima");
	 				return null;
	 			} 	 			
	 		}
	 	    catch(Exception e)
	 	    {
	 	        Log.e(Config.TAG, "Error ControladoraClima >> "+e.getMessage()); 	
	 	        e.printStackTrace();
	 	        return null;
	 	    }
		}
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
		
		temperaturaActual.setText(context.getResources().getString(R.string.text_actual)+" "+temperaturaActualT+" °C");
		temperaturaMaxima.setText(context.getResources().getString(R.string.text_maxima)+" "+temperaturaMaximaT+" °C");
		temperaturaMinima.setText(context.getResources().getString(R.string.text_minima)+" "+temperaturaMinimaT+" °C");
		super.onPostExecute(result);
	}
	
	/**
	 * Revisa la SD Card en busca del icono del clima para evitar la descarga.
	 * @param codigo - el codigo del clima
	 * @return Bitmap - el bitmap del icono o null si no exite
	 */
	private Bitmap asignarImagen(String codigo)
	{
		File directory = new File(Environment.getExternalStorageDirectory()+File.separator+"clima-images");
		File imagen=new File(directory,"clima_"+codigo+".png");
		if (imagen.exists())
		{
		    Bitmap myBitmap = BitmapFactory.decodeFile(imagen.getAbsolutePath());
		    Log.d(Config.TAG,"Se encontro imagen clima");	 
		    return myBitmap;
		}
		else
		{
			Log.e(Config.TAG,"No se encontro imagen clima");	    	
		    return null;
		}				
	}
	/**
	 * Obtiene la ultima de fecha de actualizacion y la compara con la fecha actual
	 * Si la diferencia entre esas horas es mayor o igual a la configurada [1] devuelve TRUE <p>
	 * [1] Config.HORAS
	 *                            
	 * @return true si necesito datos del clima /// false si no paso el tiempo indicado
	 */
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

			Log.d(Config.TAG, "Diferencia horas: "+diferenciaHoras);
			
			if (diferenciaHoras>=Config.HORAS)
			{				
				return true;
			}
			else
			{
				return false;
			}
		}
		catch (Exception e)
		{		
			e.printStackTrace();
			return true;
		}
	}

	/**
	 * Descarga el icono correspondiente al clima y lo almacena en la tarjeta de memoria.[1] <p>
	 * [1] El directorio de almacenamiento se puede configurar en Config.DIR.
	 * @param uRL2 - la url del icono a descarga
	 * @param code - codigo interno del clima
	 * @return bitmap - la imagen descargada o null si hubo error
	 */
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
             Log.d(Config.TAG, "Imagen OK: "+uRL2);
             String file_path = Environment.getExternalStorageDirectory()+File.separator+Config.DIR;
     		 File dir = new File(file_path);  	 
     		 if(!dir.exists())
     		    				dir.mkdirs();
     		 File file = new File(dir, "clima_"+code+".png");
     		 FileOutputStream fOut = new FileOutputStream(file);
     		 bm.compress(Bitmap.CompressFormat.PNG, 90, fOut);
     		 fOut.flush();
     		 fOut.close();
     		 Log.d(Config.TAG, "Imagen descargada OK y guardada OK");
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			Log.e(Config.TAG, "Imagen NO descargada");
		}
		return bm;
	}

	/**
	 * Reviso si el codigo solicitado ya se encuentra disponible en la BD. Si no lo encuentra, devuelve FALSE
	 * y lo agrega para futuras busquedas 
	 * @param code - codigo interno del clima
	 * @param URL - la url del icono a descarga
	 * @return boolean - true si existe - false si no lo encuentra
	 */	
	private static boolean existeCodigo(String code, String URL)
	{
		DAO_clima dao = new DAO_clima(context);
		return dao.existeCodigo(code, URL);
	}	
}
