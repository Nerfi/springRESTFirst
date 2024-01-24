package com.example.cashcard.tutorials;



import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.List;

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

}
