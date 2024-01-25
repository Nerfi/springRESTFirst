package com.example.cashcard.tutorials;

import org.apache.coyote.Response;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/tutorials")
public class TutorialsController {

    // tenemos siempre que avilitar el repositorio para poder llevar a cabo los metodos

    private final TutorialsRepository tutorialsRepository;

    public TutorialsController(TutorialsRepository tutorialsRepository) {
        this.tutorialsRepository = tutorialsRepository;
    }

    @GetMapping
    private ResponseEntity<Iterable<Tutorials>> findAll() {

        Iterable<Tutorials> tut = tutorialsRepository.findAll();

        // comprobamos que NO este vacio este metodo
        if(!tut.iterator().hasNext()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(tut);
    }

    @GetMapping("/{requestedId}")
    private ResponseEntity<Optional<Tutorials>> findById(@PathVariable Long requestedId) {
            // el nombre Tutorials esta mal pero bueno, es solo para pruebas
        Optional<Tutorials> singleTutorial = tutorialsRepository.findById(requestedId);

        //check in case there is not tutorial
        if(singleTutorial.isPresent()) {
            return ResponseEntity.ok(singleTutorial);
        }
    // returnign 404 htt status code
        return ResponseEntity.notFound().build();
    }

    // post

    @PostMapping
    private ResponseEntity<Void> createTutorial(@RequestBody Tutorials tut, UriComponentsBuilder ucb, Principal principal) {
        // Request body tiene le objeto que con los datos que vamos a guardar en bbdd
        Tutorials tutorial = new Tutorials(null, tut.title(), tut.description(), true, principal.getName());
        Tutorials tutorialSaved = tutorialsRepository.save(tutorial);
        // como buena practica en un POST si todo ha ido bien tenemos que devoler un 201 CREATED con el URI de donde se encuentra la resource create
        // por eso hemos llamado a UriComponentBuilder
        URI locationOFNewTutorial = ucb
                .path("tutorials/{id}")
                .buildAndExpand(tutorialSaved.id())
                .toUri();

        return ResponseEntity.created(locationOFNewTutorial).build();
    }

    // PUT aka update
    @PutMapping("/{tutorialId}")
    private ResponseEntity<Void> putTutorial(@PathVariable Long tutorialId, @RequestBody Tutorials tutorialUpdate, Principal principal) {
        // 1 - buscamos que tutorial queremos actualizar y si es propietario del tutorial
        Tutorials tutorialToUpdate = tutorialsRepository.findByIdAndOwner(tutorialId, principal.getName());

        if (tutorialUpdate != null) {
            //1- si tutorialToUpdate esta presente, procedemos a actualizar el tutorial devuelto
            Tutorials tutorialUpdated = new Tutorials(tutorialToUpdate.id(), tutorialUpdate.title(), tutorialUpdate.description(), true, principal.getName());
            // 2- procedemos a guardar el actualizado tutorial
            tutorialsRepository.save(tutorialUpdated);
            //devolvemos un 204 ok
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();

    }

    //DELETE

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deleteTutorial(@PathVariable Long id, Principal principal) {
        // el codigo comentado de abajo lo que hace es elimanar el contenido sea cual sea su creado, no tiene implementado authorization
//        tutorialsRepository.deleteById(id);
//
//        return ResponseEntity.noContent().build();

        // ahora implementamos authorization en este controlador
        // añadimos el principal para asegurarnos de que este es el dueño ya que el custom method findByIdAndOwner nos devolvera un boolean
        //para decirnos si este es el dueño o no del recurso que intentamos eliminar
        if(tutorialsRepository.existsByIdAndOwner(id, principal.getName() )) {
            tutorialsRepository.deleteById(id);
            return  ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    // FIND TUTORIAL BY TITLE
    @GetMapping("/find")
    private ResponseEntity<List<Tutorials>> findByTitle(@RequestParam("title") String title) {
        List<Tutorials> tuts = tutorialsRepository.findByTitleContains(title);

        if( tuts.isEmpty() ) {

            return  ResponseEntity.notFound().build();
        }


        return ResponseEntity.ok(tuts);
    }



}
