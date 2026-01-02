package com.sepdrive.dto;

import lombok.Data;

@Data
public class LocationInputDTO {
    private String type; // geo, address, poi, coords
    private String address;
    private String poi;
    private Double lat;
    private Double lng;
} 