WorldWeatherOnlineAndroidLibrary
================================

¡Bienvenidos a la libreria WorldWeatherOnlineAndroidLibrary!

Esta libreria tiene como fin poder añadir a nuestro layout un pequeño widget que consta de una imagen que
representara un icono del clima más informacion sobre la temperatura actual, máxima y mínima prevista para el día actual.


¿Qué puedo modificar?
================================

Primero abrimos el archivo Config.java y modificamos las varibles a nuestro gusto. 

-> CIUDAD : ciudad que deseamos obtener los datos

-> TAG : etiqueta para debug

-> API : api-key del servicio WorldWeatherOnline

-> DIR : directorio donde almacenaremos los iconos del clima

-> HORAS: diferencia de horas necesaria para realizar la proxima llamada a la API

 
¿Como funciona?
================================

* La libreria ejecuta una tarea asincrona para no bloquear el hilo principal de la aplicacion

* Verifica si necesito descargar nuevos datos: comprueba cual fue la hora de nuestra ultima descarga y si la diferencia
entre esa fecha y la fecha actual es mayor o igual a Config.HORAS se ejecuta la llama a la API

* Si tenemos autorizacion para descargar nuestros datos: 

	> Se ejecuta la llamada a la API
	> Se almacenan los datos de temperatura actual, máxima y mínima prevista para el día actual. 
	> Verificamos si el codigo del clima lo tenemos almacenado en una tabla. Si lo tenemos buscamos la imagen. 
	  Si es la primera vez que descargamos ese codigo, se descarga la imagen, se almacena en nuestra tarjeta de memoria
	  y se asigna al imageView
	  
* Si NO tenemos autorizacion para descargar nuestros datos:

	> Se revisa los datos almacenados en las preferencias y se muestran
	> Se busca y se asigna la imagen del clima correspondiente
	
Modalidades de uso
================================

a) Como widget en nuestro layout: solo basta agregar los permisos correspondientes e insertar el wiget en nuestro layout

<com.coffeeandcookies.worldweatheronlineandroidlibrary.WigetClima 
		android:id="@+id/wigetClima1" 
		android:layout_width="wrap_content" 
		android:layout_height="wrap_content">
</com.coffeeandcookies.worldweatheronlineandroidlibrary.WigetClima>

b) Como una llamada asincrona: debemos indicar los textView y el imageView donde se mostraran los datos

	    new ControladoraClima(getContext(), 
	                          imageView, 
	                          text_temperaturaActual, 
	                          text_temperaturaMinima, 
	                          text_temperaturaMaxima)
	                          .execute();