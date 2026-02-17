package com.democracy.democracy_orchestrator.infrastructure.web.routers;


import com.democracy.democracy_orchestrator.infrastructure.web.handlers.PostulantHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static com.democracy.democracy_orchestrator.infrastructure.web.constants.RoutesConstant.*;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;

//@Configuration
public class PostulantRouter {
    //@Bean
    public RouterFunction<ServerResponse> routerPostulant(PostulantHandler handler){
        String returnUrl = DEMOCRACY_ORCHESTRATOR.concat(INVESTIGATION).concat(SELECT);
        return RouterFunctions
           .route(
                   POST(returnUrl), handler::selectPostulant);
    }
}
