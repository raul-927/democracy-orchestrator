package com.democracy.democracy_orchestrator.infrastructure.web.routers;


import com.democracy.democracy_orchestrator.infrastructure.web.handlers.PostulantHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static com.democracy.democracy_orchestrator.infrastructure.web.constants.RoutesConstant.*;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;


public class PostulantRouter {



    public RouterFunction<ServerResponse> routerDepartment(PostulantHandler handler){
        return RouterFunctions
           .route(
                   POST(DEMOCRACY_ORCHESTRATOR + INVESTIGATION + SELECT), handler::selectPostulant);
    }
}
