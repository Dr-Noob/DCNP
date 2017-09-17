# DCNP(Distributed Computing Network Protocol)

El protocolo DCNP permite distribuir el trabajo necesario para llevar a cabo una tarea entre tantos ordenadores como se desee

# Visión general

El protocolo DCNP está especificado(actualmente en su versión 1.10) en /documentacion/dcnp.pdf. Este repositorio, además de incluir su especificación, implementa dicho protocolo en Java, además de tres módulos(también en java) para este protocolo

### Para empezar

El protocolo se compone de cliente y servidor; el servidor conducirá a los clientes, repartiendo el trabajo a realizar. Pero, ¿qué trabajo? 
DCNP es un protocolo que permite acoplar módulos independientes a él. De esta forma, cualquiera puede programar un nuevo módulo(como se explica a continuación) y poder hacer uso del protocolo mediante dicho módulo.

Puedes probar de forma rápida el protocolo con módulos ya hechos, tal y como se detalla a continuación,o compilarlo desde el código fuente(disponible en /src/)(El directorio /src/modulos/ contiene el codigo de los modulos que se han hecho para DCNP)

### Prerequisitos

¿Qué necesitas instalar? Lo necesario para ejecutar java, esto es, el JDK. Si quieres compilar el código, necesitarás también el SDK(y, opcionalmente, un IDE, como Eclipse)

### Probando el protocolo

1-Descarga el JAR cliente(/jar/Node.jar) y el del servidor(/jar/Conductor.jar).
2-Descarga los JAR del módulo que prefieras(por ejemplo, el de las N-Reinas) Después descarga los módulos. Descarga tanto el solver(/jar/modulos/nreinas/nreinas_solver.jar) como el problem(/jar/modulos/nreinas/nreinas_problem.jar)
3-Copia ambos módulos a un directorio donde se vaya a ejecutar el Servidor y crea el archivo 'dcnp.txt' (tal y como se describe en el documento de programación de módulos para DCNP); Por ejemplo:

```
name = N-Reinas
problem = problem.jar
solver = solver.jar
problem_args = 16
problem_args = 16
```

Ambos JAR deben estar en la misma carpeta. Los argumentos especifican, en este caso, que N=16 para el problema de las N-Reinas.

4-Ejecuta el servidor(Conductor.jar) pasándole como argumento el directorio donde estén los módulos y el archivo 'dcnp.txt'(-ip la_ip -folder el_directorio)
5-Ejecuta el cliente(Node.jar) en tantos ordenadores como quieras(al menos uno) y especifica la IP del Servidor mediante los argumentos del JAR(-ip ip_servidor) y el directorio donde se guardará el módulo solver(no importa cual sea)(-folder tmp)
6-En el servidor, ejecuta la orden 'start'
7-El protocolo comenzará y, una vez acabado, se mostrará el resultado en el servidor. En cualquier momento puedes detener el cómputo, desde el servidor, mediante la orden 'exit'

### ¿Y si quiero hacer mi propio módulo?

Estás de suerte. He hecho un pdf para gente como tú(/documentacion/programación_dcnp), donde está todo lo que necesitas saber. Te recomiendo también ver los que ya están hechos

### ¿Qué modulos están disponibles actualmente?
* N-Reinas: Resuelve el problema de las N-Reinas, un problema derivado del [problema de las ocho reinas](https://es.wikipedia.org/wiki/Problema_de_las_ocho_reinas). Para ejecutarlo correctamente, es necesario pasar como argumentos de los .jar(esto se hace en el fichero dcnp.txt)la N del problema.
* Crack MD5: Crackea cualquier palabra, probando combinaciones y calculando el hash MD5. Para ejecutarlo correctamente, es necesario pasar como argumentos el hash que se quiere crackear como argumentos de los .jar
* Cálculo de números primos. Calcula todos los números primos(tantos como se desee, hasta que se ejecute la orden 'exit' en el servidor)

### Lista de comandos y argumentos

* Conductor.jar

Argumentos:

| Argumento                | Explicación               |
|:------------------------:|:-------------------------:|
| -folder X                | OBLIGATORIO: Especifica el directorio donde está el archivo de configuración 'dcnp.txt'        |
| -debug (quiet/verbose)   | OPCIONAL: Cambia el modo debug. Por defecto, está activado el modo 'quiet' que no mostrará mensajes de debug, mientras que el modo 'verbose' sí lo hará |
| -names required          | OPCIONAL: Especifica si los nodos deben identificarse cuando se conecten al servidor. Esto hará que el servidor muestre los nombres de cada nodo |
| -help                    | Muestra la ayuda |

Comandos:

| Comando        | Explicación          |
|:--------------:|:--------------------:|
| -help          | Muestra la ayuda |
| -stats         | Muestra las estadísticas sobre los nodos conectados |
| -start         | Comienza el cómputo del problema. Si un nodo se conecta después de haber ejecutado este comando, empezará a trabajar nada mas conectarse |
| -exit          | Termina el servidor y le pide a los nodos que hagan lo mismo |

* Node.jar

Argumentos:

| Argumento               | Explicación                |
|:-----------------------:|:--------------------------:|
| -debug (quiet/verbose)  | OBLIGATORIO: Especifica la ip del servidor
| -names required         | OBLIGATORIO: Especifica el directorio donde se guardará el fichero .jar(solver) |
| -port X                 | OPCIONAL: Especifica el puerto del servidor al que el se conectará el nodo. Por defecto es el 4450 |
| -name X                 | OBLIGATORIO/OPCIONAL: Dependiendo de cómo este configurado el servidor, puede ser o no obligatorio. Especifica el nombre con el que se identificará este nodo |
| -help                   | Muestra la ayuda |

Comandos:

| Comando        | Explicación          |
|:--------------:|:--------------------:|
| -help          | Muestra la ayuda |
| -stats         | Muestra las estadísticas sobre el trabajo realizado |
| -exit          | Termina el nodo tan pronto como sea posible |

### Estructura del proyecto

    ├── documentacion
    │   ├── dcnp.pdf
    │   └── programacion_dcnp.pdf
    ├── jar
    │   ├── Conductor.jar
    │   ├── modulos
    │   │   ├── hash
    │   │   │   ├── hash_problem.jar
    │   │   │   └── hash_solver.jar
    │   │   ├── nreinas
    │   │   │   ├── nreinas_problem.jar
    │   │   │   └── nreinas_solver.jar
    │   │   └── primos
    │   │       ├── primos_problem.jar
    │   │       └── primos_solver.jar
    │   └── Node.jar
    ├── lib
    │   └── libDcnp.jar
    ├── README.md
    └── src
        ├── app
        │   ├── Conductor.java
        │   └── Node.java
        ├── conductor
        │   ├── ConductorArgumentsParser.java
        │   ├── ConductorProblem.java
        │   ├── ConductorServer.java
        │   ├── ConductorShell.java
        │   ├── ConductorThread.java
        │   ├── Folder.java
        │   ├── NodeInfo.java
        │   └── NodesDatabase.java
        ├── dcnp
        │   ├── Problem.java
        │   └── Solver.java
        ├── message
        │   ├── ConductorProblem
        │   │   ├── Message.java
        │   │   ├── MessageNewOut.java
        │   │   ├── MessageNextIn.java
        │   │   ├── MessageNextInReq.java
        │   │   └── MessageSolution.java
        │   ├── NodeConductor
        │   │   ├── MessageAddNode.java
        │   │   ├── MessageBye.java
        │   │   ├── MessageHasStarted.java
        │   │   ├── Message.java
        │   │   ├── MessageNameError.java
        │   │   ├── MessageNewIn.java
        │   │   ├── MessageNewOutBye.java
        │   │   ├── MessageNewOut.java
        │   │   ├── MessageProblemModule.java
        │   │   └── MessageStart.java
        │   └── NodeSolver
        │       ├── MessageBye.java
        │       ├── Message.java
        │       ├── MessageNewIn.java
        │       └── MessageNextOut.java
        ├── modulos
        │   ├── Modulo_Buscar_Hash
        │   │   ├── app
        │   │   │   ├── Problem.java
        │   │   │   └── Solver.java
        │   │   ├── problem
        │   │   │   └── MD5.java
        │   │   └── solver
        │   │       ├── HThread.java
        │   │       └── MD5.java
        │   ├── Modulo_N-Reinas
        │   │   ├── app
        │   │   │   ├── Problem.java
        │   │   │   └── Solver.java
        │   │   ├── problem
        │   │   │   └── Queens.java
        │   │   └── solver
        │   │       ├── QThread.java
        │   │       └── Queens.java
        │   └── Modulo_Numeros_Primos
        │       ├── MainProblem.java
        │       ├── MainSolver.java
        │       ├── PrimeProblem.java
        │       ├── PrimeSolver.java
        │       └── ThreadCalc.java
        ├── node
        │   ├── NodeArgumentsParser.java
        │   ├── NodeClient.java
        │   ├── NodeCompute.java
        │   └── NodeShell.java
        └── utils
            ├── Shell.java
            ├── Statistics.java
            └── Utils.java


   

## Autores

* **Pablo Martínez Sánchez** - *Todo, excepto el módulo de cálculo de numeros primos*
* **Gonzalo Caparrós Laiz** - *Módulo de cálculo de numeros primos*
