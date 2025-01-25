package com.powerledger.vpp.service;

import com.powerledger.vpp.dto.BatteryDTO;
import com.powerledger.vpp.dto.BatteryStatsDTO;
import com.powerledger.vpp.entity.Battery;
import com.powerledger.vpp.repository.BatteryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BatteryService {
    private final BatteryRepository batteryRepository;

    public List<Battery> saveBatteries(List<BatteryDTO> batteryDTOs) {
        List<Battery> batteries = batteryDTOs.stream()
                .map(dto -> new Battery(null, dto.getName(), dto.getPostcode(), dto.getWattCapacity()))
                .collect(Collectors.toList());
        return batteryRepository.saveAll(batteries);
    }

    public List<Battery> findByPostcodeRange(String startPostcode, String endPostcode) {
        return batteryRepository.findByPostcodeRange(startPostcode, endPostcode);
    }

    public BatteryStatsDTO calculateStats(List<Battery> batteries) {
        long totalWattCapacity = batteries.stream()
                .mapToLong(Battery::getWattCapacity)
                .sum();
        double averageWattCapacity = batteries.stream()
                .mapToLong(Battery::getWattCapacity)
                .average()
                .orElse(0);
        return new BatteryStatsDTO(batteries.size(), totalWattCapacity, averageWattCapacity);
    }
}