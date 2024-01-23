package com.example.cashcard;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

//spring security
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
//

//siempre que querramos implementar la paginacion debemos extender a la clase: PagingAndSortingRepository ademas de la de CRUD

public interface CashCardRepository extends  CrudRepository<CashCard, Long>, PagingAndSortingRepository<CashCard, Long> {
    // part of spring security tut
    // aqui filtramos los datos que nos devuelve la bbdd por el owner, es decir quien lo creo
     CashCard findByIdAndOwner(Long id, String owner);
     Page<CashCard> findByOwner(String owner, PageRequest pageRequest);

     boolean existsByIdAndOwner(Long id, String owner);

    // en los dos metodos de arriab spring data es la que se encargara de
    // la implementacion de las querysSQL para los metodos que hemos definido
    // docs para escribir nuestra propias querys
    // https://docs.spring.io/spring-data/relational/reference/repositories/query-methods-details.html
}





// para la auth:  https://www.adictosaltrabajo.com/2020/05/21/introduccion-a-spring-security/