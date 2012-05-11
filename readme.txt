Antonio Prada Blanco

* Descripción del desarrollo:
==============================

Se han realizado las funcionalidades básicas pedidas. Como mejoras se han incluido:

* Preguntar al usuario: Cuando faltan datos para poder realizar la consulta, se pregunta al usuario por ella. 

Para ello se comprueban una a una las beliefs del nluAgent. Cuando una de estas comprobaciones falla, se pregunta al usuario y se añade una belief con el dato esperado. La respuesta del usuario se envia al sistema NLU. Si vuelve a fallar la misma comprobacion, se vuelve a enviar al sistema NLU pero modificando la query ya que es posible que el usuario haya constestado directamente a la pregunta (Por ejemplo, ante la pregunta "¿Cual es la ciudad de destino?" la respuesta puede no contener ningun prefijo y Unitex no la entendería).

* Reconocer los nombres de los meses: se ha modificado el archivo java "RenfeScrapper.java" para ello.

Una frase de ejemplo que se puede usar es "Quiero viajar de madrid a barcelona el 20 de junio del 2012". Como ya se ha dicho, si la frase no es completa se pregunta al usuario por ella. Por ello es posible ir introduciendos los datos de uno en uno, por ejemplo empezando con "Quiero viajar" (o cualquier otra frase) y contestando las preguntas devueltas.
