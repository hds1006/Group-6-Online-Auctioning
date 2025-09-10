package edu.sru.cpsc.webshopping.service;

import lombok.Data;

@Data
public class TrackingReponse {
    private String status;
    private String estimatedDelivery;
    private String currentLocation;
}
