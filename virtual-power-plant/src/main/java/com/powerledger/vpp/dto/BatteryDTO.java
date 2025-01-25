package com.powerledger.vpp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatteryDTO {
    private String name;
    private String postcode;
    private long wattCapacity;
}
