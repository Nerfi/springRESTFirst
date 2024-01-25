package com.example.cashcard;

import org.apache.coyote.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
//gracias al comando de abajo tenemos todas las notaciones para los endpoint
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import com.example.cashcard.CashCardRepository;
import java.security.Principal;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cashcards")
public class CashCardController {
    private final CashCardRepository cashCardRepository;

    private CashCardController(CashCardRepository cashCardRepository) {
        this.cashCardRepository = cashCardRepository;
    }


    @GetMapping("/{requestedId}")
    private ResponseEntity<CashCard> findById(@PathVariable Long requestedId, Principal principal) {
        //Optional<CashCard> cashCardOptional = Optional.ofNullable(cashCardRepository.findByIdAndOwner(requestedId, principal.getName()));
        //hacemos esto por el tutorial , pero tenemos que leer mas sobre Optional y si se debe o no eliminar
        CashCard cashCard = findCashCard(requestedId, principal);
//        if (cashCardOptional.isPresent()) {
//            return ResponseEntity.ok(cashCardOptional.get());
//        } else {
//            return ResponseEntity.notFound().build();
//        }

        if(cashCard != null) {
            return  ResponseEntity.ok(cashCard);
        } else {
            return ResponseEntity.notFound().build();
        }
    }



    @GetMapping
    private ResponseEntity<List<CashCard>> findAll(Pageable pageable, Principal principal) {
        Page<CashCard> page = cashCardRepository.findByOwner(principal.getName(),
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        // añadimos la linea de abajo para , si no nos viene el sorting del usuario
                        // implementamos uno nosotros ASC y que se sortee por amount
                        // ademas getSortOr nos da valores por defecto para page, size y sort
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "amount"))
                ));
        return ResponseEntity.ok(page.getContent());
    }

    @PostMapping
    private ResponseEntity<Void> createCashCard(@RequestBody CashCard newCashCardRequest, UriComponentsBuilder ucb, Principal principal){
      // @RequestBody newCashCardRequest contiene el cuerpo de la request
        // es decir el objeto que queremos guardar en la bbd

        //creating a new cashcard con el usuario que tiene por temas de seguridad: Spring security
        CashCard cashCardWithOwner = new CashCard(null, newCashCardRequest.amount(), principal.getName());
        CashCard savedCashCard = cashCardRepository.save(cashCardWithOwner);

       // This is constructing a URI to the newly created CashCard. This is the URI that the caller can then use to GET the newly-created CashCard.

       URI locationOfNewCashCard = ucb
               .path("cashcards/{id}")
               .buildAndExpand(savedCashCard.id())
               .toUri();
       // return ResponseEntity.created(locationOfNewCashCard).build();
       return ResponseEntity.created(locationOfNewCashCard).build();
    }

    // actualizacion
    //principal nos lo da de forma automatica Spring security y nos dice quien esta logueado

    @PutMapping("/{requestedId}")
    private ResponseEntity<Void> putCashCard(@PathVariable Long requestedId, @RequestBody CashCard cashCardUpdate, Principal principal) {
        CashCard cashCard = findCashCard(requestedId, principal);

        // si hemos encontrado un card
        if (cashCard != null) {
            CashCard updatedCashCard = new CashCard(cashCard.id(), cashCardUpdate.amount(), principal.getName());
            cashCardRepository.save(updatedCashCard);
            //devolvemos no content ya que al hacer PUt no hay nada que devolver
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    // delete
    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deleteCashCard(@PathVariable Long id, Principal principal){
        // añadimos Principal para asegurarnos que borramos solo cashCards que son nuestras

        if(cashCardRepository.existsByIdAndOwner(id, principal.getName())) {
            cashCardRepository.deleteById(id);
            // no content es lo mismo que 204, ver HTTP status para mejor idea
            return ResponseEntity.noContent().build();

        }


        return  ResponseEntity.notFound().build();
    }


    // helper method to retrieve a cashcard owner, DRY

    private CashCard findCashCard(Long requestedId, Principal principal) {
        return cashCardRepository.findByIdAndOwner(requestedId, principal.getName());
    }




}
