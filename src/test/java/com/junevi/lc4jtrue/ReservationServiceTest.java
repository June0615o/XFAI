package com.junevi.lc4jtrue;

import com.junevi.lc4jtrue.pojo.Reservation;
import com.junevi.lc4jtrue.service.ReservationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
public class ReservationServiceTest {

    @Autowired
    private ReservationService reservationService;

    @Test
    void testInsert(){
        reservationService.insert(new Reservation(1L,"Junevi", "男","13602592270", LocalDateTime.now(),"广东",580));
    }

    @Test
    void testFindByPhone(){
        String phone = "13602592270";
        Reservation reservation = reservationService.findByPhone(phone);
        System.out.println(reservation);
    }
}
