package com.example.cashcard.tutorials;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

// añadir mas adelante implementacion para Page and Pageable

public interface TutorialsRepository extends CrudRepository<Tutorials, Long> {
    // impementando un metodo para solo eliminar los tutoriales que te pertenezcan como creador
    boolean existsByIdAndOwner(Long id , String owner);
    // method para poder hacer update del objeto
    Tutorials findByIdAndOwner(Long id, String owner);

    //method to find by title
    List<Tutorials> findByTitleContains(String title);

     // por esta razon hemos cambiado Like por Contains: https://www.baeldung.com/spring-jpa-like-queries


}


// do not forget to read this on https://medium.com/@dudkamv/understanding-the-distinctions-spring-jdbc-vs-spring-data-jpa-ecc1e8039e31