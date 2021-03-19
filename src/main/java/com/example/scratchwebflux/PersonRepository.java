package com.example.scratchwebflux;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PersonRepository extends R2dbcRepository<Person, Integer>{
  Mono<Person> findById(Integer id);

}
