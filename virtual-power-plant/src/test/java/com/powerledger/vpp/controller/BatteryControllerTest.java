package com.powerledger.vpp.controller;

import com.powerledger.vpp.dto.BatteryDTO;
import com.powerledger.vpp.entity.Battery;
import com.powerledger.vpp.service.BatteryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BatteryControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void registerBatteries_WithEmptyList_ReturnsBadRequest() {
        List<BatteryDTO> emptyList = List.of();

        ResponseEntity<?> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/batteries/register",
                emptyList,
                List.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void registerBatteries_WithNullValues_ReturnsBadRequest() {
        List<BatteryDTO> invalidData = List.of(new BatteryDTO(null, null, 0));

        ResponseEntity<?> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/batteries/register",
                invalidData,
                List.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void findBatteriesByPostcodeRange_WithInvalidRange_ReturnsBadRequest() {
        ResponseEntity<?> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/api/batteries/range?startPostcode=2001&endPostcode=2000",
                Object.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void findBatteriesByPostcodeRange_WithNonNumericPostcode_ReturnsBadRequest() {
        ResponseEntity<?> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/api/batteries/range?startPostcode=abc&endPostcode=def",
                Object.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}