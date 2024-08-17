Posteal App

-Introducción
Esta es Postealapp, una aplicación tipo Instagram donde podrás subir tus fotos, seguir a tus amigos y ver sus últimas publicaciones, dándoles amigas feedback con likes y comentarios.

-Funcionalidades 
   ·Disclaimer importante: la base lógica de esta aplicación se ha basado en la aplicación planteada en este curso: (). De esta se ha conservado parte del sistema de autenticación y la lógica de las screens del feed y la de búsqueda. El resto de la lógica ha sido reimplementada (todo lo relacionado con el single post, comentarios, seguidores, etc.). Por lo demás, como gran parte de la UI, todos los tests, pequeños cambios en la arquitectura (capa de negocio) y funcionalidades adicionales como el modo oscuro o el borrado de un post, son completamente de mi autoría.

   ·Funcionamiento: 
   En primer lugar, en la aplicación podrás crearte una cuenta o iniciar sesión en el caso de que ya tengas una. Una vez dentro, aparecerás en la pantalla de tu feed, en la que la primera vez que la veas aparecerán los últimos posts publicados en toda la aplicación, pero cuando sigas a otros usuarios solamente aparecerán sus posts.
   Al seleccionar un post podremos indicar que nos gusta haciendo un doble click o dejar algún comentario.
   Otra opción será la pantalla de búsqueda de posts. Si en ella escribes algún término que pertenezca a la descripción de un post, éste aparecerá por pantalla.
   Por último, en la página de tus posts aparte de poder acceder a tus publicaciones para ver el feedback de tus amigos (likes y comentarios), podrás editar tu perfil. Ahí puedes cambiar tus datos personales, cerrar sesión y alternar entre los modos claro y oscuro.

   ·Detalles técnicos:
   -> Se ha seguido la arquitectura MVVM (poner aquí lo de las capas)
   -> El backend se ha implementado con Firebase, concretamente los servicios de almacenamiento, el Firestore y la autenticación.
   -> La inyección de dependencia se ha realizado con Hilt.
   -> La navegación se ha gestionado con con NavController
   -> La UI se ha implmentado con Jetpack Compose y con la inclusión de temas y tipografías específicos.
   -> (Incluir aquí lo del testeo).
   -> (Incluir aquí lo de las apis, versiones y chuminadas varias).
   
