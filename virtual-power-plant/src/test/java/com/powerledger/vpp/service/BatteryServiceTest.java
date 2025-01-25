//package com.powerledger.vpp.service;
//
//import com.powerledger.vpp.dto.BatteryDTO;
//import com.powerledger.vpp.dto.BatteryStatsDTO;
//import com.powerledger.vpp.entity.Battery;
//import com.powerledger.vpp.repository.BatteryRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import java.util.List;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class BatteryServiceTest {
//    @Mock
//    private BatteryRepository batteryRepository;
//
//    private BatteryService batteryService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        batteryService = new BatteryService(batteryRepository);
//    }
//
//    @Test
//    void saveBatteries_shouldSaveBatteriesSuccessfully() {
//        List<BatteryDTO> batteryDTOs = List.of(
//                new BatteryDTO("Battery1", "2000", 1000),
//                new BatteryDTO("Battery2", "2001", 2000)
//        );
//
//        when(batteryRepository.saveAll(anyList())).thenReturn(List.of(
//                new Battery(1L, "Battery1", "2000", 1000),
//                new Battery(2L, "Battery2", "2001", 2000)
//        ));
//
//        List<Battery> savedBatteries = batteryService.saveBatteries(batteryDTOs);
//
//        assertNotNull(savedBatteries);
//        assertEquals(2, savedBatteries.size());
//        verify(batteryRepository).saveAll(anyList());
//    }
//
//    @Test
//    void calculateStats_shouldCalculateCorrectly() {
//        List<Battery> batteries = List.of(
//                new Battery(1L, "Battery1", "2000", 1000),
//                new Battery(2L, "Battery2", "2001", 2000)
//        );
//
//        BatteryStatsDTO stats = batteryService.calculateStats(batteries);
//
//        assertEquals(2, stats.getTotalBatteries());
//        assertEquals(3000, stats.getTotalWattCapacity());
//        assertEquals(1500.0, stats.getAverageWattCapacity());
//    }
//}

package com.powerledger.vpp.service;

import com.powerledger.vpp.dto.BatteryDTO;
import com.powerledger.vpp.dto.BatteryStatsDTO;
import com.powerledger.vpp.entity.Battery;
import com.powerledger.vpp.repository.BatteryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.List;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BatteryServiceTest {
    @Mock
    private BatteryRepository batteryRepository;
    private BatteryService batteryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        batteryService = new BatteryService(batteryRepository);
    }

    @Test
    void saveBatteries_shouldSaveBatteriesSuccessfully() {
        List<BatteryDTO> batteryDTOs = List.of(
                new BatteryDTO("Battery1", "2000", 1000),
                new BatteryDTO("Battery2", "2001", 2000)
        );

        List<Battery> expectedBatteries = List.of(
                new Battery(1L, "Battery1", "2000", 1000),
                new Battery(2L, "Battery2", "2001", 2000)
        );

        when(batteryRepository.saveAll(anyList())).thenReturn(expectedBatteries);

        List<Battery> savedBatteries = batteryService.saveBatteries(batteryDTOs);

        assertNotNull(savedBatteries);
        assertEquals(2, savedBatteries.size());
        verify(batteryRepository).saveAll(anyList());
    }

    @Test
    void saveBatteries_shouldHandleEmptyList() {
        List<BatteryDTO> emptyList = Collections.emptyList();
        when(batteryRepository.saveAll(anyList())).thenReturn(Collections.emptyList());

        List<Battery> result = batteryService.saveBatteries(emptyList);

        assertTrue(result.isEmpty());
        verify(batteryRepository).saveAll(result);
    }

    @Test
    void findByPostcodeRange_shouldReturnBatteriesInRange() {
        List<Battery> expectedBatteries = List.of(
                new Battery(1L, "Battery1", "2000", 1000),
                new Battery(2L, "Battery2", "2001", 2000)
        );

        when(batteryRepository.findByPostcodeRange("2000", "2001"))
                .thenReturn(expectedBatteries);

        List<Battery> result = batteryService.findByPostcodeRange("2000", "2001");

        assertEquals(2, result.size());
        verify(batteryRepository).findByPostcodeRange("2000", "2001");
    }

    @Test
    void calculateStats_shouldCalculateCorrectly() {
        List<Battery> batteries = List.of(
                new Battery(1L, "Battery1", "2000", 1000),
                new Battery(2L, "Battery2", "2001", 2000)
        );

        BatteryStatsDTO stats = batteryService.calculateStats(batteries);

        assertEquals(2, stats.getTotalBatteries());
        assertEquals(3000, stats.getTotalWattCapacity());
        assertEquals(1500.0, stats.getAverageWattCapacity());
    }

    @Test
    void calculateStats_shouldHandleEmptyList() {
        List<Battery> emptyList = Collections.emptyList();

        BatteryStatsDTO stats = batteryService.calculateStats(emptyList);

        assertEquals(0, stats.getTotalBatteries());
        assertEquals(0, stats.getTotalWattCapacity());
        assertEquals(0.0, stats.getAverageWattCapacity());
    }
}