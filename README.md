# Sistema de Domésticas

Plataforma para gestionar tareas domésticas en hogares compartidos utilizando arquitectura hexagonal (Hexagonal Architecture).

## Descripción del Proyecto

Esta aplicación permite organizar y distribuir tareas domésticas en hogares compartidos, facilitando la asignación, seguimiento y cumplimiento de responsabilidades entre los miembros del hogar.

### Características Principales

- **Registro de usuarios y grupos familiares**
- **Creación y asignación de tareas domésticas**
- **Definición de fechas límite y prioridades**
- **Registro del estado de cada tarea**
- **Historial de cumplimiento por usuario**
- **Reportes sobre distribución de responsabilidades**

## Justificación de la Arquitectura Hexagonal aplicada al Sistema de Organización de Tareas Domésticas

El enfoque clásico de tres capas (presentación → negocio → datos) organiza el sistema de forma lineal, donde cada capa depende de la inmediatamente inferior. Esto genera un problema estructural: la lógica de negocio termina dependiendo de la tecnología de persistencia, lo que hace que cualquier cambio en la base de datos o en el mecanismo de entrada obligue a modificar el dominio.

La Arquitectura Hexagonal invierte esta relación. El dominio no depende de nadie; son las capas externas (HTTP, JPA, memoria) las que dependen del dominio. Esto es especialmente relevante para este sistema por las reglas de negocio que se definieron: la distribución equilibrada de tareas, el ciclo de estados, el bloqueo semanal de asignación y la gestión de excedentes deben funcionar correctamente independientemente de si los datos se persisten en PostgreSQL, en memoria o en cualquier otra tecnología.

La arquitectura hexagonal implica crear más clases y más niveles de separación que una arquitectura en capas tradicional. Para un sistema CRUD sencillo, esto podría ser innecesario y complicar el desarrollo. Sin embargo, en este proyecto las reglas de negocio son más complejas y además se necesitaba cambiar la persistencia de memoria a PostgreSQL entre sprints. Por eso, el esfuerzo adicional de esta arquitectura se justifica, ya que permite aislar la lógica del negocio y facilitar cambios tecnológicos sin afectar el resto del sistema.

Desde esta perspectiva, la arquitectura se organiza de la siguiente manera:

### Núcleo del dominio

El centro del sistema está compuesto por las entidades Hogar, Tarea y Usuario, ubicadas en domain/model, junto con los enums DificultadTarea, PrioridadTarea y EstadoTarea. Estas clases no tienen ninguna anotación de Spring ni de JPA. 

Las reglas de negocio están encapsuladas directamente en ellas, no en los servicios. 

* Hogar valida que su nombre tenga entre 3 y 50 caracteres, gestiona la lista de miembros y designa automáticamente al administrador.
* Tarea valida que la fecha límite no sea anterior al momento actual.
* EstadoTarea define qué transiciones son válidas mediante el método puedeTransicionarA(), de forma que ningún servicio externo puede saltar estados arbitrariamente.
* AsignacionSemanalTarea encapsula el comportamiento de cambio de estado y liberación de responsable, que son operaciones que ocurren dentro de un ciclo semanal específico.

### Puertos de entrada

Un puerto de entrada es una interfaz que define las acciones que los usuarios o sistemas externos pueden pedirle al núcleo de la aplicación. Cada puerto de entrada representa una funcionalidad o caso de uso del sistema, generalmente relacionado con una historia de usuario.

Los puertos de entrada están ubicados en application/port/in/ y son los siguientes:

* CrearHogarUseCase: Expone la operación de crear un hogar con su administrador. 
* AgregarMiembroUseCase: Expone la operación de agregar un miembro al hogar.
* EliminarMiembroUseCase: Expone la operación de eliminar un miembro y liberar sus tareas.
* CrearTareaUseCase: Expone la creación de una tarea doméstica con validación de duplicados y formato.
* EditarTareaUseCase: Expone la edición de atributos de una tarea con restricción si está asignada o en proceso.
* EliminarTareaUseCase: Expone la eliminación de una tarea con restricción si está asignada.
* CambiarEstadoTareaUseCase: Expone el cambio de estado de una tarea dentro de su ciclo válido.
* AsignarTareaUseCase: Expone la asignación semanal equilibrada de tareas.
* ListarTareasUseCase: Expone el listado de tareas con su estado actual del ciclo vigente.
* FiltrarTareasUseCase: Expone el filtrado combinado por estado, miembro, prioridad, dificultad y nombre.

Los controladores no se conectan directamente con una implementación específica, sino mediante interfaces. Gracias a esto, se puede cambiar la implementación de un servicio sin modificar el controlador. También permite reemplazar el controlador HTTP por otro tipo de comunicación, como mensajes o eventos, sin afectar la lógica del servicio.

### Puertos de salida

Un puerto de salida es una interfaz que indica qué necesita el dominio del exterior para funcionar, sin depender de una implementación específica. El dominio solo define las reglas o contratos, mientras que la infraestructura se encarga de implementarlos.

Los puertos de salida se ubican en diferentes partes según su función.

Los puertos de persistencia se encuentran en domain/port/out/ porque hacen parte de las reglas y necesidades que el dominio requiere para poder funcionar correctamente:

* HogarRepository: Define las operaciones de persistencia de Hogar (guardar, buscar por id, buscar por correo de usuario). El dominio nunca sabe si detrás hay PostgreSQL o memoria.
* TareaRepository: Define las operaciones de persistencia de Tarea (guardar, actualizar, eliminar, buscar por id, listar, filtrar y verificar duplicados por semana).
* AsignacionSemanalRepository: Define las operaciones sobre los ciclos semanales (guardar asignaciones, obtener la última, listar tareas de un ciclo, buscar la asignación activa de una tarea, liberar tareas de un usuario y obtener excedentes de ciclos anteriores).
  
Los puertos de comunicación entre módulos están en application/port/out/ porque son abstracciones que el módulo de tareas necesita para comunicarse con el módulo de hogares sin acoplarse directamente a él:

* ObtenerMiembrosHogarPort: Permite que AsignarTareaService obtenga los identificadores de los miembros de un hogar sin acceder directamente a HogarRepository. Su implementación, ObtenerMiembrosHogarAdapter, se encuentra en hogares/infrastructure porque ese es el módulo que tiene acceso a HogarRepository.
* LiberarTareasPort: Permite que EliminarMiembroService libere las tareas de un usuario eliminado sin conocer AsignacionSemanalRepository directamente. Su implementación vive en tareas/infrastructure.
  
Esta separación asegura que los módulos no dependan directamente unos de otros. La comunicación entre ellos se realiza mediante interfaces definidas en application/port/out/, evitando conexiones directas entre las capas de infraestructura de diferentes módulos.

### Capa de aplicación

Los servicios se encargan de coordinar la lógica del dominio, pero no de definir reglas de negocio. Su función es organizar el proceso: obtener entidades del repositorio, ejecutar métodos del dominio y guardar los cambios realizados.

Por ejemplo, AsignarTareaService implementa:

* La distribución equilibrada de tareas según el peso de dificultad (ALTA = 3, MEDIA = 2, BAJA = 1).
* La priorización de tareas excedentes del ciclo anterior.
* El bloqueo semanal de reasignación.
  
Todo esto delegando las validaciones individuales al dominio.

Los Commands (CrearHogarCommand, CrearTareaCommand, EditarTareaCommand, etc.) son objetos inmutables que transportan los datos de entrada desde el controlador hasta el servicio. Garantizan que el controlador no pase objetos de infraestructura al interior del sistema.

Los DTOs de respuesta (como TareaListadoDTO, AsignacionSemanalResponse o MiembroDTO) son objetos que se usan para mostrar la información que el sistema envía hacia afuera. Pertenecen a la capa de aplicación porque su función es definir qué datos se van a devolver, sin preocuparse por cómo se envían (por ejemplo, si es JSON, XML u otro formato) ni por detalles técnicos de transporte.

El TareaListadoAssembler es el encargado de convertir y unir datos de Tarea y AsignacionSemanalTarea para formar un TareaListadoDTO. Su función principal es evitar que esa misma lógica de conversión se repita en varios servicios, manteniéndola en un solo lugar para que el código sea más organizado y fácil de mantener.

### Adaptadores de entrada

Los HogarController y TareaController son los encargados de recibir las solicitudes HTTP. Su trabajo es tomar los datos que llegan, convertirlos en Commands y enviarlos al caso de uso correspondiente. No contienen lógica de negocio, solo coordinan el flujo de la petición.

Los DTOs de entrada (como CrearHogarRequest o CrearTareaRequest) incluyen reglas básicas de validación usando Jakarta (por ejemplo @NotBlank o @NotNull). Estas validaciones sirven como una primera revisión para asegurar que los datos sean válidos antes de que lleguen a la lógica del sistema.

### Adaptadores de salida

Las implementaciones de repositorios (como JpaHogarRepository, JpaTareaRepository y JpaAsignacionSemanalRepository) son las que conectan el sistema con la base de datos usando JPA. Estas clases cumplen los puertos de salida del dominio, es decir, permiten guardar y consultar información sin que el dominio dependa directamente de la base de datos.

Para esto usan entidades de persistencia como HogarEntity, UsuarioEntity, TareaEntity, etc. Estas clases sí tienen anotaciones de JPA como @Entity, @Table y @Column porque pertenecen a la capa de infraestructura, no al dominio.

Por otro lado, los mappers (HogarMapper, TareaMapper) se encargan de transformar datos entre dos modelos: las entidades JPA (usadas para la base de datos) y los objetos del dominio (usados en la lógica del sistema).

Esto ayuda a mantener separadas las responsabilidades: por un lado está la lógica del negocio y por otro la forma en que los datos se guardan o se leen desde la base de datos.

### Configuración 

El BeanConfig es la clase donde se conectan las interfaces con sus implementaciones.

Su función es aplicar el principio de inversión de dependencias, es decir, en lugar de que las clases internas creen directamente otras clases concretas, se define aquí qué implementación debe usarse.

Por eso, es el único lugar del sistema donde se crean objetos concretos. Así, ninguna capa del sistema depende directamente de otra implementación, lo que mantiene el código más flexible y desacoplado.


## Tecnologías Utilizadas

- **Java 21**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **PostgreSQL**
- **Maven**

## Configuración

1. **Base de Datos**: Configurar PostgreSQL y actualizar `application.properties` con las credenciales correctas.

2. **Dependencias**: Ejecutar `mvn install` para instalar las dependencias.

3. **Ejecución**: Ejecutar `mvn spring-boot:run` para iniciar la aplicación.

## Endpoints API

### Usuarios
- `POST /api/users` - Crear usuario
- `GET /api/users/{id}` - Obtener usuario por ID
- `GET /api/users` - Obtener todos los usuarios
- `PUT /api/users/{id}` - Actualizar usuario
- `DELETE /api/users/{id}` - Eliminar usuario

### Grupos
- `POST /api/hogares` - Crear grupo
- `GET /api/hogares/{id}` - Obtener grupo por ID
- `GET /api/hogares` - Obtener todos los grupos
- `PUT /api/hogares/{id}` - Actualizar grupo
- `DELETE /api/hogares/{id}` - Eliminar grupo

### Tareas
- `POST /api/tareas` - Crear tarea
- `GET /api/tareas/{id}` - Obtener tarea por ID
- `GET /api/tareasgroup/{groupId}` - Obtener tareas por grupo
- `GET /api/tareas/user/{userId}` - Obtener tareas por usuario
- `PUT /api/tareas/{id}` - Actualizar tarea
- `PATCH /api/tareas/{id}/status` - Actualizar estado de tarea
- `DELETE /api/tareas/{id}` - Eliminar tarea
- `POST /api/tareas/hogares/{hogarId}/asignacion-semanal` - Asignar tareas

## Estructura del Proyecto

```
src/
└── main/
    └── java/com/tareasdomesticas/
            ├── hogar-service/
            │
            ├── HogarServiceApplication.java
            │
            ├── common
            │   └── domain
            │       └── model
            │           ├── Usuario.java
            │           └── RolUsuario.java
            ├── hogares
            │   ├── domain
            │   │   ├── model
            │   │   │   └── Hogar.java
            │   │   └── port
            │   │       └── out
            │   │           └── HogarRepository.java
            │   
            │   ├── application
            │   │   ├── port
            │   │   │   └── in
            │   │   │       └── CrearHogarUseCase.java   
            │   │   └── service
            │   │       └── CrearHogarService.java
            │
            │   └── infrastructure
            │       └── adapter
            │           ├── in
            |           |   └── dto
            |           |   |    └── CrearHogarRequest.java
            |           |   |    └── CrearHogarResponse.java
            │           │   └── HogarController.java
            │           └── out
            │               └── InMemoryHogarRepository.java            
            ├── tareas
            │   ├── domain
            │   │   ├── model
            │   │   │   └── Tarea.java
            |   |   |   ├── DificultadTarea.java
            |   |   |   ├── PrioridadTarea.java
            |   |   |   └── EstadoTarea.java
            │   │   └── port
            │   │       └── out
            │   │           └── TareaRepository.java
            │
            │   ├── application
            │   │   ├── dto
            |   |   |   ├── AsignacionSemanalResponse.java    
            │   │   │   └── TareaAsignadaDTO.java
            |   |   |   └── TareaExcedenteDTO.java
            |   |   |   └── TarealistadoDTO.java
            |   |   |   └── UsuarioAsignadoDTO.java
            │   │   ├── port
            │   │   │   └── in
            │   │   │       ├── CrearTareaUseCase.java      
            │   │   │       └── AsignarTareaUseCase.java
            │   │   │       └── ListarTareasUseCase.java
            │   │   └── service
            │   │       ├── CrearTareaService.java
            │   │       └── AsignarTareaService.java
            │   │       └── ListarTareasService.java
            │
            │   └── infrastructure
            │       └── adapter
            │           ├── in
            |           |   └── dto
            |           |   |    └── CrearTareaRequest.java
            |           |   |    └── CrearTareaResponse.java
            │           │   └── TareaController.java
            │           └── out
            │               └── InMemoryTareaRepository.java  
            └── config
                └── BeanConfig.java

```

## Próximos Pasos

- Implementar autenticación y autorización
- Agregar frontend web
- Implementar reportes y estadísticas
- Agregar notificaciones
- Tests unitarios e integración
