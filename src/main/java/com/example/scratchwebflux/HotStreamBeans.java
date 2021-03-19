package com.example.scratchwebflux;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

@Service
public class HotStreamBeans {
  private final DefaultService defaultService;

  public HotStreamBeans(DefaultService defaultService) {
    this.defaultService = defaultService;
  }

  @Bean
  public Sinks.Many<Person> source() {
    return Sinks.<Person>many().multicast().directBestEffort();
  }

  @Bean
  public Flux<Person> sinkStream(Sinks.Many<Person> source) {
    Flux<Person> hotFlux = source.asFlux().publishOn(Schedulers.boundedElastic())
        .doOnNext(p -> {
              try {
                Thread.sleep(10000L);
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
            }
        ).doOnNext(p -> p.setName("edited in hotstream"))
        .flatMap(p->defaultService.save(p));
    hotFlux.subscribe();
    return hotFlux;
  }
}
