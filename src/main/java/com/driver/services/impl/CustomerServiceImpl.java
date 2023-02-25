package com.driver.services.impl;

import com.driver.model.*;
import com.driver.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.driver.repository.CustomerRepository;
import com.driver.repository.DriverRepository;
import com.driver.repository.TripBookingRepository;

import java.util.Collections;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	CustomerRepository customerRepository2;

	@Autowired
	DriverRepository driverRepository2;

	@Autowired
	TripBookingRepository tripBookingRepository2;

	@Override
	public void register(Customer customer) {
		//Save the customer in database
		customerRepository2.save(customer);
	}

	@Override
	public void deleteCustomer(Integer customerId) {
		// Delete customer without using deleteById function
		Customer cr = customerRepository2.findById(customerId).get();
		List<TripBooking> listoftripbooking = cr.getTripBookingList();
		for(TripBooking tr:listoftripbooking){
				tripBookingRepository2.deleteById(tr.getTripBookingId());
		}
		customerRepository2.deleteById(customerId);

	}

	@Override
	public TripBooking bookTrip(int customerId, String fromLocation, String toLocation, int distanceInKm) throws Exception{
		//Book the driver with lowest driverId who is free (cab available variable is Boolean.TRUE). If no driver is available, throw "No cab available!" exception
		//Avoid using SQL query
		List<Driver> driverList = driverRepository2.findAll();
		Collections.sort(driverList,(Driver t,Driver o) -> t.getId()-o.getId());
		boolean tf = true;
		Cab cd = null;
		Driver dv = null;
		for(Driver dr:driverList){
			Cab cb = dr.getCab();
			if(cb.isAvailable()){
				tf=false;
				cd = cb;
				dv = dr;
				break;

			}
		}

		if(tf) throw new Exception("No cab available");
		cd.setAvailable(false);
		TripBooking tripBooking = new TripBooking();
		tripBooking.setFromLocation(fromLocation);
		tripBooking.setCustomer(customerRepository2.findById(customerId).get());
		tripBooking.setToLocation(toLocation);
		tripBooking.setDistanceInKm(distanceInKm);
		tripBooking.setDriver(dv);
		tripBooking.setTripStatus(TripStatus.CONFIRMED);


		List<TripBooking> tripBookingList = dv.getTripBookingList();
		tripBookingList.add(tripBooking);

		return tripBooking;
	}

	@Override
	public void cancelTrip(Integer tripId){
		//Cancel the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking tb = tripBookingRepository2.findById(tripId).get();
		Driver dv = tb.getDriver();
		List<TripBooking> tripBookingList = dv.getTripBookingList();
		Cab cb = dv.getCab();
		cb.setAvailable(true);
		tb.setTripStatus(TripStatus.CANCELED);

		tripBookingList.add(tb);
		dv.setTripBookingList(tripBookingList);
		tripBookingRepository2.save(tb);

	}

	@Override
	public void completeTrip(Integer tripId){
		//Complete the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking tb = tripBookingRepository2.findById(tripId).get();
		Driver dv = tb.getDriver();
		List<TripBooking> tripBookingList = dv.getTripBookingList();


		Cab cb = tb.getDriver().getCab();
		tb.setBill(cb.getPerKmRate()* tb.getDistanceInKm());
		cb.setAvailable(true);
		tb.setTripStatus(TripStatus.COMPLETED);

		tripBookingList.add(tb);
		dv.setTripBookingList(tripBookingList);
		tripBookingRepository2.save(tb);

	}
}
