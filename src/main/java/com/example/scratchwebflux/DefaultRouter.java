package com.example.scratchwebflux;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.EmitFailureHandler;
import reactor.core.publisher.Sinks.Many;
import reactor.core.scheduler.Schedulers;

@Component

public class DefaultRouter {

  private final Sinks.Many<Person> source;
  private final DefaultService defaultService;

  Logger logger = LoggerFactory.getLogger(DefaultRouter.class);

  public DefaultRouter(Many<Person> source, DefaultService defaultService) {
    this.source = source;
    this.defaultService = defaultService;
  }

  @Bean
  public RouterFunction<ServerResponse> personRoute() {
    return
        route(POST("/hello").and(accept(APPLICATION_JSON)), this::hello)
            .andRoute(POST("/person").and(accept(APPLICATION_JSON)), this::save)
            .andRoute(GET("/person").and(accept(APPLICATION_JSON)), this::find)
            .andRoute(GET("/person2").and(accept(APPLICATION_JSON)), this::rewrite);
  }

  public Mono<ServerResponse> hello(ServerRequest request) {
    logger.info("dsdd?");

    return request.bodyToMono(Person.class)
        .subscribeOn(Schedulers.boundedElastic())
        .flatMap(defaultService::save)
        .doOnNext(p -> {
          source.emitNext(p, EmitFailureHandler.FAIL_FAST);
          logger.info("" + p.getId());
        })
        .flatMap(
            s -> ServerResponse.ok().contentType(APPLICATION_JSON)
                .body(BodyInserters.fromValue(s)));
  }

  public Mono<ServerResponse> save(ServerRequest req) {
    return req.bodyToMono(Person.class)
        .flatMap(defaultService::findAndSave)
        .flatMap(s -> ServerResponse.ok().contentType(APPLICATION_JSON)
            .body(BodyInserters.fromValue(s)));
  }

  public Mono<ServerResponse> find(ServerRequest req) {
    return Mono.just(req.queryParam("id").get())
        .flatMap(i -> defaultService.find(Integer.parseInt(i)))
        .flatMap(s -> ServerResponse.ok().contentType(APPLICATION_JSON)
            .body(BodyInserters.fromValue(s)));
  }

  public Mono<ServerResponse> rewrite(ServerRequest req) {
    return Mono.just(req.queryParam("id").get())
        .flatMap(i -> defaultService.rewrite(Integer.parseInt(i)))
        .flatMap(s -> ServerResponse.ok().contentType(APPLICATION_JSON)
            .body(BodyInserters.fromValue(s)));
  }
}
