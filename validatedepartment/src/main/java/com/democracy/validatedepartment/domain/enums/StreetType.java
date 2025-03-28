package com.democracy.validatedepartment.domain.enums;

public enum StreetType {
    CL(1, "CALLE"),
    K(2,"CARRERA"),
    DG(3, "DIAGONAL"),
    CI(4, "CIRCUNVALAR"),
    AV(5, "AVENIDA"),
    VIA(6, "VIA"),
    TR(7, "TRANSVERSAL"),
    AK(8, "AVENIDA_CARRERA"),
    AC(9, "AVENIDA_CALLE"),
    RU(10, "RUTA"),
    CA(11, "CARRETERA"),
    CM(12, "CAMINO"),
    BR(13, "BOULEVARD"),
    JR(14, "JIRON");


    private int id;
    private String description;


    private StreetType(int id, String description) {
        this.id = id;
        this.description = description;
    }


    public int getId() {
        return id;
    }


    public String getDescription() {
        return description;
    }



}
