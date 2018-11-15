package com.example.demo;

import org.springframework.data.repository.CrudRepository;

public interface BullRepository extends CrudRepository<Bull, Long>{

    Iterable<Bull> findAllByStatusContainingIgnoreCase(String status);
}