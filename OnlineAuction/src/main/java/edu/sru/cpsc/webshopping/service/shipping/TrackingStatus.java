package edu.sru.cpsc.webshopping.service.shipping;

import lombok.Data;

@Data
public class TrackingStatus {
    private String status;
    private String statusDetails;
    private String location;
    private String statusDate;
}
