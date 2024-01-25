package com.example.cashcard.tutorials;

import org.springframework.web.bind.annotation.*;

import org.springframework.http.ResponseEntity;

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

}
