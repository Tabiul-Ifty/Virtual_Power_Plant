package com.powerledger.vpp.repository;

import com.powerledger.vpp.entity.Battery;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BatteryRepositoryTest {
    @Autowired
    private BatteryRepository batteryRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByPostcodeRange_shouldReturnBatteriesInRange() {
        Battery battery1 = new Battery(null, "Battery A", "2000", 1000);
        Battery battery2 = new Battery(null, "Battery B", "2001", 2000);
        Battery battery3 = new Battery(null, "Battery C", "2002", 3000);

        entityManager.persist(battery1);
        entityManager.persist(battery2);
        entityManager.persist(battery3);
        entityManager.flush();

        List<Battery> batteries = batteryRepository.findByPostcodeRange("2000", "2001");

        assertEquals(2, batteries.size());
        assertEquals("Battery A", batteries.get(0).getName());
        assertEquals("Battery B", batteries.get(1).getName());
    }
}