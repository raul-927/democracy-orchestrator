package com.democracy.validatedepartment.infrastructure.web.routers;

import com.democracy.validatedepartment.infrastructure.web.constants.RoutesConstant;
import com.democracy.validatedepartment.infrastructure.web.handlers.OrderHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

//@Configuration
public class OrderRouter {
    private static final String VALIDATE_DEPARTMENT = RoutesConstant.VALIDATE_DEPARTMENT;
    private static final String ALL = RoutesConstant.ALL;

    //@Bean
    public RouterFunction<ServerResponse> routeOrder(OrderHandler handler){
        return RouterFunctions
                .route(
                        POST(VALIDATE_DEPARTMENT + ALL), handler::selectAllOrders);
    }
}
