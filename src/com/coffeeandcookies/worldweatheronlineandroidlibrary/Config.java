package com.coffeeandcookies.worldweatheronlineandroidlibrary;

/**
 * Clase donde se almacenan constantes de configuracion
 * @author Gonzalo Benoffi
 * gitHub URL >> https://github.com/benoffi7/WorldWeatherOnlineAndroidLibrary
 * 
 * >>> LEER la WIKI para su correcta instalacion y uso <<<
 * 
 * >>> Coffee And Cookies - Desarrollo de Aplicaciones Android <<<
 * >>> 2013, Mar del Plata, Argentina
 */
public class Config
{
	/**
	 * Etiqueta a utilizar para el Log
	 */
	static final String TAG = "WorldWeatherOnlineAndroidLibrary";
	/**
	 * Ciudad que queremos el clima
	 */	
	private static final String CIUDAD = "Mar del Plata";
	/**
	 * Nuestra API KEY de WorldWeatherOnlineAndroid
	 */
	private static final String API = "TU API KEY";	
	/**
	 * La URL del servicio para obtener los datos del clima
	 */
	private static final String URL = "http://api.worldweatheronline.com/free/v1/weather.ashx?q="+CIUDAD+"&format=json&num_of_days=5&key="+API;
	/**
	 * Cantidad de horas que tienen que pasar para obtener datos nuevos del servicio. 
	 * Hay que tener en cuenta que la API no es gratitua y tenemos un limite de llamados por hora.
	 * La libreria utiliza un sistema de cache para mostrar los datos cuando la diferencia de horas no se ha alcanzado.
	 */
	public static final long HORAS = 6;
	/**
	 * Directorio donde guardaremos las iconos del clima
	 */
	public static final String DIR = "clima_imagenes";
	/**
	 * Reemplaza los espacios con el signo "+"
	 * @return URL sin espacios
	 */
	public static String getUrl()
	{
		return URL.trim().replace(" ", "+");
	}

}
