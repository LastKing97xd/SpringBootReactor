package com.bolsadeideas.springboot.reactor.app;

import java.awt.Adjustable;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.bolsadeideas.springboot.reactor.app.models.Comentarios;
import com.bolsadeideas.springboot.reactor.app.models.Usuario;
import com.bolsadeideas.springboot.reactor.app.models.UsuarioComentarios;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class SpringBootReactorApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(SpringBootApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SpringBootReactorApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		ejemploUsuarioComentariosZipWith();
		
	}
	
	public Usuario crearUsuario() {
		
		return new Usuario("John", "Doe");
		
	}
	
	public void ejemploUsuarioComentariosZipWithForma2() {

		Mono<Usuario> usuarioMono= Mono.fromCallable(() -> crearUsuario());
		
		Mono<Comentarios> comentariosUsuarioMono= Mono.fromCallable(()->{
			Comentarios comentarios = new Comentarios();
			comentarios.addComentario("Hola");
			comentarios.addComentario("Hello");
			comentarios.addComentario("Bye");
			return comentarios;
		});
		
		Mono<UsuarioComentarios> usuarioConComentarios = usuarioMono
		.zipWith(comentariosUsuarioMono)
		.map(jose -> {
			Usuario u = jose.getT1();
			Comentarios c = jose.getT2();
			return new UsuarioComentarios(u, c);
		});
		usuarioConComentarios.subscribe(uc -> log.info(uc.toString()));
	}
	
	public void ejemploUsuarioComentariosZipWith() {

		Mono<Usuario> usuarioMono= Mono.fromCallable(() -> crearUsuario());
		
		Mono<Comentarios> comentariosUsuarioMono= Mono.fromCallable(()->{
			Comentarios comentarios = new Comentarios();
			comentarios.addComentario("Hola");
			comentarios.addComentario("Hello");
			comentarios.addComentario("Bye");
			return comentarios;
		});
		
		usuarioMono.zipWith(comentariosUsuarioMono, (usu, comen)-> new UsuarioComentarios(usu, comen)).subscribe(uc -> log.info(uc.toString()));
	}
	
	public void ejemploUsuarioComentariosFlatMap() {

		Mono<Usuario> usuarioMono= Mono.fromCallable(() -> crearUsuario());
		
		Mono<Comentarios> comentariosUsuarioMono= Mono.fromCallable(()->{
			Comentarios comentarios = new Comentarios();
			comentarios.addComentario("Hola");
			comentarios.addComentario("Hello");
			comentarios.addComentario("Bye");
			return comentarios;
		});
		
		usuarioMono.flatMap(usu -> comentariosUsuarioMono.map(comen -> new UsuarioComentarios(usu, comen)))
		.subscribe(uc -> log.info(uc.toString()));
	}
	
	public void ejemploConvertirLista() throws Exception {

		List<Usuario> usulista = new ArrayList<>();
		usulista.add(new Usuario("Andres","Guzman"));
		usulista.add(new Usuario("Pedro","Fulano"));
		usulista.add(new Usuario("Diego","Sultano"));
		usulista.add(new Usuario("Juan","Mengano"));
		usulista.add(new Usuario("Bruce","Willis"));
		usulista.add(new Usuario("Bruce","Lee"));

		Flux.fromIterable(usulista)
		//imprime toda la lista en un dato
		.collectList()
		.subscribe(lista -> {
			lista.forEach(e ->log.info(e.toString()));
		});
		
	}
	
	public void ejemploConvertirString() throws Exception {

		List<Usuario> lista = new ArrayList<>();
		lista.add(new Usuario("Andres","Guzman"));
		lista.add(new Usuario("Pedro","Fulano"));
		lista.add(new Usuario("Diego","Sultano"));
		lista.add(new Usuario("Juan","Mengano"));
		lista.add(new Usuario("Bruce","Willis"));
		lista.add(new Usuario("Bruce","Lee"));

		Flux.fromIterable(lista)
				//para convertir de tipo usuario a String
				.map(nombelemento -> nombelemento.getNombre().toUpperCase().concat(" ")
						.concat(nombelemento.getApellido().toUpperCase()))
				.flatMap(string -> {
					if(string.contains("bruce".toUpperCase())) {
						return Mono.just(string);
					}else {
						return Mono.empty();
					}
				})
				.map(string -> string.split(" ")[0].toLowerCase().concat(" ").concat(string.split(" ")[1]))
				.subscribe(elemento -> log.info(elemento));
	}
	
	public void ejemploFlatMap() throws Exception {

		List<String> lista = new ArrayList<>();
		lista.add("Andres Guzman");
		lista.add("Pedro Fulano");
		lista.add("Diego Sultano");
		lista.add("Juan Mengano");
		lista.add("Bruce Willis");
		lista.add("Bruce Lee");

		Flux.fromIterable(lista)
				.map(nombelemento -> new Usuario(nombelemento.split(" ")[0].toUpperCase(),
						nombelemento.split(" ")[1].toUpperCase()))
				.flatMap(usuario -> {
					if(usuario.getNombre().equalsIgnoreCase("bruce")) {
						return Mono.just(usuario);
					}else {
						return Mono.empty();
					}
				})
				.map(elemento -> {
					String ruda = elemento.getNombre().toLowerCase();
					elemento.setNombre(ruda);
					return elemento;
				})
				.subscribe(elemento -> log.info(elemento.toString()));
	}

	public void ejemploiterable() throws Exception {

		List<String> lista = new ArrayList<>();
		lista.add("Andres Guzman");
		lista.add("Pedro Fulano");
		lista.add("Diego Sultano");
		lista.add("Juan Mengano");
		lista.add("Bruce Willis");
		lista.add("Bruce Lee");

		Flux<String> nombres = Flux.fromIterable(lista);

		// Flux.just("Andres Guzman","Pedro Fulano","Diego Sultano","Juan Mengano","Bruce Willis","Bruce Lee");

		Flux<Usuario> usuarios = nombres
				.map(nombelemento -> new Usuario(nombelemento.split(" ")[0].toUpperCase(),
						nombelemento.split(" ")[1].toUpperCase()))
				//.filter(usuario -> usuario.getNombre().toLowerCase().equals("bruce"))
				.doOnNext(elemento -> {
					

					System.out.println(elemento.getNombre().concat(" ").concat(elemento.getApellido()));
				}).map(elemento -> {
					String ruda = elemento.getNombre().toLowerCase();
					elemento.setNombre(ruda);
					return elemento;
				});

		usuarios.subscribe(elemento -> log.info(elemento.toString()), error -> log.error(error.getMessage()),
				new Runnable() {

					@Override
					public void run() {
						log.info("Se ha completado el Observable");
					}
				});
	}
	
	
	public void ejemploEstudio() throws Exception {

		Flux<String> nombres = Flux.just("Andres Guzman","Pedro Fulano","Diego Sultano","Juan Mengano","Bruce Willis","Bruce Lee");
		 
		Flux<Usuario> usuarios = nombres.map(nombelemento -> new Usuario(nombelemento.split(" ")[0].toUpperCase(),nombelemento.split(" ")[1].toUpperCase()))
				 
		         .doOnNext( elemento -> {
		        	 if(elemento==null){
		        		 throw new RuntimeException("Nombres no pueden ser vacios");
		        	 }
		        	 
		        	 System.out.println(elemento.getNombre().concat(" ").concat(elemento.getApellido()));
		         })
		         .map(elemento -> {
		         	  String ruda = elemento.getNombre().toLowerCase();
		         	  elemento.setNombre(ruda);
				 	  return elemento;
				 	  });
				 	  
			usuarios.subscribe(elemento -> log.info(elemento.toString()),
					error -> log.error(error.getMessage()),
					new Runnable(){
					
						@Override
						public void run(){
							log.info("Se ha completado el Observable");
						}
					});
	}

}

/*
private static final Logger log = LoggerFactory.getLogger(nombre.class);
 
Flux<String> nombres = Flux.just("Andres","Pedro","Diego","Juan")
     .doOnNext( (elemento -> System.out.println(elemento));
nombres.subscribe(elemento -> log.info(elemento));

//

Flux<String> nombres = Flux.just("Andres","","Diego","Juan")
     .doOnNext( elemento -> {
     if(e.isEmpty()){
     	throw new RuntimeException("Nombres no pueden ser vacios");
	 }
     System.out.println(e)
     });
nombres.subscribe(elemento -> log.info(elemento),
		error -> log.error(error.getMessage()));

//

Flux<String> nombres = Flux.just("Andres","","Diego","Juan")
     .doOnNext( elemento -> {
     if(e.isEmpty()){
     	throw new RuntimeException("Nombres no pueden ser vacios");
	 }
     System.out.println(e)
     });
nombres.subscribe(elemento -> log.info(elemento),
		error -> log.error(error.getMessage()),
		new Runnable(){
		
			@Override
			public void run(){
				log.info("Se ha completado el Observable");
			}
		});

//

Flux<String> nombres = Flux.just("Andres","","Diego","Juan")
	 .map(nombre -> {
	 	  return nombre.toUpperCase();
	 	  })
     .doOnNext( elemento -> {
     if(elemento.isEmpty()){
     	throw new RuntimeException("Nombres no pueden ser vacios");
	 }
     System.out.println(elemento)
     })
     .map(nombre -> {
	 	  return nombre.toLowerCase();
	 	  });
nombres.subscribe(elemento -> log.info(elemento),
		error -> log.error(error.getMessage()),
		new Runnable(){
		
			@Override
			public void run(){
				log.info("Se ha completado el Observable");
			}
		});

//

Flux<Usuario> nombres = Flux.just("Andres","","Diego","Juan")
	 .map(nombelemento -> new Usuario(nombelemento.toUpperCase(),null))
     .doOnNext( elemento -> {
     if(elemento==null){
     	throw new RuntimeException("Nombres no pueden ser vacios");
	 }
     System.out.println(elemento.getNombre());
     })
     .map(elemento -> {
     	  String ruda = elemento.getNombre().toLowerCase();
     	  elemento.setNombre(ruda);
	 	  return elemento;
	 	  });
	 	  
nombres.subscribe(elemento -> log.info(elemento.getNombre()),
		error -> log.error(error.getMessage()),
		new Runnable(){
		
			@Override
			public void run(){
				log.info("Se ha completado el Observable");
			}
		});

//

Flux<String> nombres = Flux.just("Andres Guzman","Pedro Fulano","Diego Sultano","Juan Mengano","Bruce Willis","Bruce Lee");
			 
	Flux<Usuario> usuarios = nombres.map(nombelemento -> new Usuario(nombelemento.split(" ")[0].toUpperCase(),nombelemento.split(" ")[1].toUpperCase()))
			 .filter(usuario -> usuario.getNombre().toLowerCase().equals("bruce"))
	         .doOnNext( elemento -> {
	        	 if(elemento==null){
	        		 throw new RuntimeException("Nombres no pueden ser vacios");
	        	 }
	        	 
	        	 System.out.println(elemento.getNombre().concat(" ").concat(elemento.getApellido()));
	         })
	         .map(elemento -> {
	         	  String ruda = elemento.getNombre().toLowerCase();
	         	  elemento.setNombre(ruda);
			 	  return elemento;
			 	  });
			 	  
		usuarios.subscribe(elemento -> log.info(elemento.toString()),
				error -> log.error(error.getMessage()),
				new Runnable(){
				
					@Override
					public void run(){
						log.info("Se ha completado el Observable");
					}
				});

*/
