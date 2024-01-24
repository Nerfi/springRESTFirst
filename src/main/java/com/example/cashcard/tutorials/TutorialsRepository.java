package com.example.cashcard.tutorials;

import org.springframework.data.repository.CrudRepository;

// añadir mas adelante implementacion para Page and Pageable

public interface TutorialsRepository extends CrudRepository<Tutorials, Long> {
}
