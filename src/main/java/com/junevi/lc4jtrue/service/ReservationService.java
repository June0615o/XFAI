package com.junevi.lc4jtrue.service;

import com.junevi.lc4jtrue.mapper.ReservationMapper;
import com.junevi.lc4jtrue.pojo.Reservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReservationService {

    @Autowired
    private ReservationMapper reservationMapper;

    public void insert(Reservation  reservation){
        reservationMapper.insert(reservation);
    }

    public Reservation findByPhone(String phone){
        return reservationMapper.findByPhone(phone);
    }
}
