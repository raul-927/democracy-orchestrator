package com.democracy.democracy_orchestrator.infrastructure.web.routers;

import com.democracy.democracy_orchestrator.infrastructure.web.constants.RoutesConstant;
import com.democracy.democracy_orchestrator.infrastructure.web.handlers.DepartmentHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import org.springframework.web.reactive.function.server.RouterFunction;
//import org.springframework.web.reactive.function.server.RouterFunctions;
//import org.springframework.web.reactive.function.server.ServerResponse;

//import static org.springframework.web.reactive.function.server.RequestPredicates.*;


@Configuration
public class DepartmentRouter {
    private static final String HUMAN_RESOURCES = RoutesConstant.HUMAN_RESOURCES;
    private static final String DEPARTMENT_PATH = RoutesConstant.DEPARTMENT;
    private static final String PATH_SELECT_ALL =RoutesConstant.SELECT_ALL;
    private static final String DEMOCRACY_ORCHESTRATOR = RoutesConstant.DEMOCRACY_ORCHESTRATOR;
    //private static final String PATH_SELECT = RoutesConstant.PATH_SELECT;
    //private static final String PATH_SAVE = RoutesConstant.PATH_SAVE;
    //private static final String PATH_INSERT = RoutesConstant.PATH_INSERT;
    //private static final String PATH_UPDATE = RoutesConstant.PATH_UPDATE;
    //private static final String PATH_COUNT=RoutesConstant.PATH_COUNT;

    //@Bean
   //public RouterFunction<ServerResponse> routerDepartment(DepartmentHandler handler){
        //return RouterFunctions
             //   .route(
            //            GET(DEMOCRACY_ORCHESTRATOR + DEPARTMENT_PATH + PATH_SELECT_ALL), handler::selectAllDepartment);
                /*.andRoute(
                        POST(HUMAN_RESORCES + DEPARTMENT_PATH +  PATH_SAVE), handler::createDepartment)
                .andRoute(
                        POST(HUMAN_RESORCES + DEPARTMENT_PATH +PATH_INSERT), handler::createMultipleDepartment)
                .andRoute(
                        PUT(HUMAN_RESORCES + DEPARTMENT_PATH +  PATH_UPDATE), handler::updateDepartment)
                .andRoute(
                        GET(HUMAN_RESORCES + DEPARTMENT_PATH +  PATH_COUNT), handler::selectCount)
                .andRoute(
                        GET(PATH_MAIN + DEPARTMENT_PATH + PATH_SELECT_ALL), handler::selectAllDepartment);*/
 //   }
}
