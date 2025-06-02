package ru.dulfi.gatewayservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum PetColor {
    BLACK,
    WHITE,
    GRAY,
    BROWN,
    ORANGE;
} 