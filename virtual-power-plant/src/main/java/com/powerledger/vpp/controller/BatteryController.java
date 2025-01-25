package com.powerledger.vpp.controller;

import com.powerledger.vpp.dto.BatteryDTO;
import com.powerledger.vpp.dto.BatteryStatsDTO;
import com.powerledger.vpp.entity.Battery;
import com.powerledger.vpp.service.BatteryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/batteries")
@RequiredArgsConstructor // Lombok annotation for constructor injection
public class BatteryController {
    private final BatteryService batteryService;

    // Constructor injection will be automatically created by Lombok

    @PostMapping("/register")
    public ResponseEntity<?> registerBatteries(@RequestBody List<BatteryDTO> batteryDTOs) {
        if (batteryDTOs.isEmpty()) {
            return ResponseEntity.badRequest().body("Battery list cannot be empty");
        }

        try {
            List<Battery> savedBatteries = batteryService.saveBatteries(batteryDTOs);
            return ResponseEntity.ok(savedBatteries);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/range")
    public ResponseEntity<?> findBatteriesByPostcodeRange(
            @RequestParam String startPostcode,
            @RequestParam String endPostcode) {
        try {
            List<Battery> batteries = batteryService.findByPostcodeRange(startPostcode, endPostcode);
            return ResponseEntity.ok(Map.of(
                    "batteryNames", batteries.stream().map(Battery::getName).sorted().collect(Collectors.toList()),
                    "stats", batteryService.calculateStats(batteries)
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}