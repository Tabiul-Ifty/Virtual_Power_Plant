package com.powerledger.vpp;

import com.powerledger.vpp.dto.BatteryDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PerformanceAndDataDrivenTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private List<BatteryDTO> generateBatteries(int count) {
        List<BatteryDTO> batteries = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            batteries.add(new BatteryDTO(
                    "Battery" + i,
                    String.format("%04d", 2000 + i % 1000),
                    1000 + i
            ));
        }
        return batteries;
    }

    private List<BatteryDTO> generateLargeBatterySet(int count) {
        return generateBatteries(count);
    }

    @Test
    void highConcurrentBatteryRegistration() {
        int numThreads = 100;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < numThreads; i++) {
            futures.add(executor.submit(() -> {
                List<BatteryDTO> batteries = generateBatteries(10);
                return restTemplate.postForEntity("/api/batteries/register", batteries, List.class);
            }));
        }

        executor.shutdown();
    }

    @Test
    void largeDatasetQuery() {
        // Pre-populate with 10,000 batteries
        List<BatteryDTO> largeBatchBatteries = generateLargeBatterySet(10000);
        restTemplate.postForEntity("/api/batteries/register", largeBatchBatteries, List.class);

        StopWatch timer = new StopWatch();
        timer.start();
        ResponseEntity<?> response = restTemplate.getForEntity(
                "/api/batteries/range?startPostcode=2000&endPostcode=2999", Object.class);
        timer.stop();

        assertTrue(timer.getTotalTimeMillis() < 1000); // Response under 1 second
    }

    @ParameterizedTest
    @CsvSource({
            "1000, 1500, 1",    // Normal range
            "1, 9999, 100",      // Full range
            "2000, 2000, 1",     // Single postcode
            "9998, 9999, 0"      // Empty range
    })
    void postcodeRangeTests(String start, String end, int expectedCount) {
        ResponseEntity<?> response = restTemplate.getForEntity(
                String.format("/api/batteries/range?startPostcode=%s&endPostcode=%s",
                        start, end), Object.class);
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals(expectedCount, ((List<?>) body.get("batteryNames")).size());
    }

    @ParameterizedTest
    @ValueSource(longs = {0, 1, 100, 1000, 999999})
    void batteryCapacityTests(long wattCapacity) {
        BatteryDTO battery = new BatteryDTO("Test", "2000", wattCapacity);
        ResponseEntity<?> response = restTemplate.postForEntity(
                "/api/batteries/register",
                List.of(battery),
                List.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void responseTimeUnderLoad() {
        // Populate initial data
        restTemplate.postForEntity("/api/batteries/register",
                generateBatteries(1000), List.class);

        StopWatch timer = new StopWatch();
        timer.start();

        // Make 100 concurrent requests
        ExecutorService executor = Executors.newFixedThreadPool(100);
        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            futures.add(executor.submit(() ->
                    restTemplate.getForEntity("/api/batteries/range?startPostcode=2000&endPostcode=2999",
                            Object.class)));
        }

        timer.stop();
        executor.shutdown();

        assertTrue(timer.getTotalTimeMillis() < 5000); // All requests complete under 5 seconds
    }
}