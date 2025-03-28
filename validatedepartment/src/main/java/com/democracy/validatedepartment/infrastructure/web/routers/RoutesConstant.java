package com.democracy.validatedepartment.infrastructure.web.routers;

public enum RoutesConstant {
    HUMAN_RESOURCES("/humanresources");


    private String path;



    private RoutesConstant(String path){
        this.path = path;
    }


    public String getPath(){
        return this.path;
    }

}
