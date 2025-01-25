package com.powerledger.vpp.repository;

import com.powerledger.vpp.entity.Battery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BatteryRepository extends JpaRepository<Battery, Long> {
    @Query("SELECT b FROM Battery b WHERE b.postcode BETWEEN :startPostcode AND :endPostcode ORDER BY b.name")
    List<Battery> findByPostcodeRange(String startPostcode, String endPostcode);
}