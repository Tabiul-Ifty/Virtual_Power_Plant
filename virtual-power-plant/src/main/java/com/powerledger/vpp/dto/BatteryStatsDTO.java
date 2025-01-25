package com.powerledger.vpp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatteryStatsDTO {
    private int totalBatteries;
    private long totalWattCapacity;
    private double averageWattCapacity;
}