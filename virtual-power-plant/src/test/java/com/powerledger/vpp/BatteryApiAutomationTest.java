package com.powerledger.vpp;

import io.restassured.http.ContentType;
import com.powerledger.vpp.dto.BatteryDTO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BatteryApiAutomationTest {
    @LocalServerPort
    private int port;

    @Test
    void testRegisterSingleBattery() {
        BatteryDTO battery = new BatteryDTO("Test Battery", "2000", 1000);

        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(battery)
                .when()
                .post("/api/batteries/register")
                .then()
                .statusCode(200)
                .body("[0].name", equalTo("Test Battery"));
    }

    @Test
    void testRegisterMultipleBatteries() {
        List<BatteryDTO> batteries = List.of(
                new BatteryDTO("Battery1", "2000", 1000),
                new BatteryDTO("Battery2", "2001", 2000)
        );

        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(batteries)
                .when()
                .post("/api/batteries/register")
                .then()
                .statusCode(200)
                .body("size()", is(2));
    }

    @Test
    void testInvalidBatteryRegistration() {
        BatteryDTO invalidBattery = new BatteryDTO(null, "2000", -1000);

        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(invalidBattery)
                .when()
                .post("/api/batteries/register")
                .then()
                .statusCode(400);
    }

    @Test
    void testPostcodeRangeQuery() {
        // First register some batteries
        List<BatteryDTO> batteries = List.of(
                new BatteryDTO("Battery1", "2000", 1000),
                new BatteryDTO("Battery2", "2001", 2000)
        );

        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(batteries)
                .post("/api/batteries/register");

        // Then query by postcode range
        given()
                .port(port)
                .queryParam("startPostcode", "2000")
                .queryParam("endPostcode", "2001")
                .when()
                .get("/api/batteries/range")
                .then()
                .statusCode(200)
                .body("batteryNames", hasSize(2))
                .body("stats.totalBatteries", equalTo(2))
                .body("stats.totalWattCapacity", equalTo(3000));
    }

    @Test
    void testInvalidPostcodeRange() {
        given()
                .port(port)
                .queryParam("startPostcode", "invalid")
                .queryParam("endPostcode", "2001")
                .when()
                .get("/api/batteries/range")
                .then()
                .statusCode(400);
    }

    @Test
    void testEmptyPostcodeRange() {
        given()
                .port(port)
                .queryParam("startPostcode", "9999")
                .queryParam("endPostcode", "9999")
                .when()
                .get("/api/batteries/range")
                .then()
                .statusCode(200)
                .body("batteryNames", empty())
                .body("stats.totalBatteries", equalTo(0));
    }

    @Test
    void testLargeDatasetPerformance() {
        List<BatteryDTO> largeBatterySet = generateLargeBatterySet(1000);

        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(largeBatterySet)
                .when()
                .post("/api/batteries/register")
                .then()
                .statusCode(200)
                .time(lessThan(3000L)); // Response within 3 seconds
    }

    private List<BatteryDTO> generateLargeBatterySet(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> new BatteryDTO(
                        "Battery" + i,
                        String.format("%04d", 2000 + i % 1000),
                        1000 + i
                ))
                .collect(Collectors.toList());
    }
}