package com.example.scratchwebflux;

import java.util.UUID;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class DefaultService {

  private final PersonRepository personRepository;

  public DefaultService(PersonRepository personRepository) {
    this.personRepository = personRepository;
  }

  public Mono<Person> find(Integer id){
    return personRepository.findById(id);
  }
  public Mono<Person> save(Person p){
    return personRepository.save(p);
  }

  public Mono<Person> rewrite(Integer id){
    return personRepository.findById(id) // for transaction test
        .map(p->{
          p.setName("dddd");
          return p;
        })
        .log()
        .publishOn(Schedulers.boundedElastic())
        .flatMap(personRepository::save)
        .log()
        .doOnNext(ps->ps.setName(UUID.randomUUID().toString()))
        .flatMap(personRepository::save)
        .log()
        .transformDeferred(ps->{
          if(System.currentTimeMillis() % 2 == 0){
            throw new RuntimeException("error");
          }
          return ps;
        })
    ;
  }
  public Mono<Person> findAndSave(Person person){
    return Mono.from(personRepository.save(person))
        .doOnNext(p-> p.setName("go home"))
        .doOnNext(personRepository::save);
  }
}
