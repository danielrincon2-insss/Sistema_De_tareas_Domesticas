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

El estilo arquitectónico seleccionado para este sistema es la Arquitectura Hexagonal (Ports & Adapters). Esta elección responde directamente a las necesidades del dominio: un sistema colaborativo de gestión de tareas domésticas con reglas de negocio bien definidas, que deben mantenerse completamente aisladas de los detalles técnicos como la persistencia de datos o la interfaz de comunicación.

Desde esta perspectiva, la arquitectura se organiza de la siguiente manera:

### Núcleo del dominio

El centro del sistema está compuesto por las entidades *Hogar*, *Tarea* y *Usuario*, ubicadas en el paquete *domain/model*, junto con los enums *DificultadTarea*, *PrioridadTarea* y *EstadoTarea*. Estas clases no dependen de ningún framework externo; no tienen anotaciones de Spring ni de JPA.

Las reglas de negocio están encapsuladas directamente en ellas:

* Hogar valida que su nombre tenga entre 3 y 50 caracteres y asigna automáticamente un administrador al crearse.
* Tarea valida que la fecha límite no sea anterior al momento actual y controla su propio ciclo de estados mediante métodos como *asignarA()*, *esPendiente()* y *marcarComoExcedente()*.

### Puertos de entrada

Los casos de uso *CrearHogarUseCase*, *CrearTareaUseCase* y *AsignarTareaUseCase* son interfaces ubicadas en *application/port/in/*, que definen exactamente qué puede hacer el sistema desde el exterior.

Ningún controlador accede directamente a una implementación concreta; siempre lo hace a través de estas interfaces. Esto permite que la lógica de negocio sea independiente del mecanismo de entrada y fácilmente reemplazable.

### Puertos de salida

*HogarRepository* y *TareaRepository* son interfaces ubicadas en *domain/port/out/*, que definen cómo el dominio necesita persistir sus datos, sin conocer cómo se implementa dicha persistencia.

Actualmente, las implementaciones son en memoria (*InMemoryHogarRepository*, *InMemoryTareaRepository*), pero en el Sprint 2 se reemplazarán por implementaciones con JPA sin modificar una sola línea del dominio ni de los servicios de aplicación.

### Capa de aplicación

Los servicios *CrearHogarService*, *CrearTareaService* y *AsignarTareaService* coordinan la lógica de dominio.

Por ejemplo, AsignarTareaService implementa:

* La distribución equilibrada de tareas según el peso de dificultad (ALTA = 3, MEDIA = 2, BAJA = 1).
* La priorización de tareas excedentes del ciclo anterior.
* El bloqueo semanal de reasignación.

Todo esto se realiza sin conocer si los datos provienen de una base de datos, una API externa o memoria.

Los DTOs de respuesta (*AsignacionSemanalResponse*, *TareaAsignadaDTO*, *TareaExcedenteDTO*, *TareaListadoDTO*, *UsuarioAsignadoDTO*) también pertenecen a esta capa, ya que representan la información que el sistema devuelve al ejecutar un caso de uso. Definen qué datos se entregan al exterior sin depender de cómo se envían ni de tecnologías específicas.

### Adaptadores de entrada

*HogarController* y *TareaController* son adaptadores REST ubicados en *infrastructure/adapter/in/*.

Se encargan de:

* Recibir peticiones HTTP.
* Construir los objetos necesarios del dominio.
* Delegar la ejecución al caso de uso correspondiente.

Los DTOs de entrada (*CrearHogarRequest*, *CrearTareaRequest*) traducen el formato JSON al lenguaje del dominio. Ningún controlador contiene lógica de negocio.

### Adaptadores de salida

*InMemoryHogarRepository* e *InMemoryTareaRepository* implementan los puertos de salida del dominio.

### Configuración 

*BeanConfig* configura la arquitectura, conectando las implementaciones concretas con sus respectivas interfaces, siguiendo el principio de inversión de dependencias.

Ninguna capa interna instancia directamente una implementación concreta de otra capa.


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
