package com.driver.services.impl;

import com.driver.model.Cab;
import com.driver.repository.CabRepository;
import com.driver.services.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.driver.model.Driver;
import com.driver.repository.DriverRepository;

@Service
public class DriverServiceImpl implements DriverService {

	@Autowired
	DriverRepository driverRepository3;

	@Autowired
	CabRepository cabRepository3;

	@Override
	public void register(String mobile, String password){
		//Save a driver in the database having given details and a cab with ratePerKm as 10 and availability as True by default.
		Driver dr = new Driver();
		dr.setMobile(mobile);
		dr.setPassword(password);
		Cab cb = new Cab();
		cb.setAvailable(true);
		cb.setPerKmRate(10);
		cb.setDriver(dr);
		dr.setCab(cb);
		cabRepository3.save(cb);
		driverRepository3.save(dr);

	}

	@Override
	public void removeDriver(int driverId){
		// Delete driver without using deleteById function
		driverRepository3.deleteById(driverId);

	}

	@Override
	public void updateStatus(int driverId){
		//Set the status of respective car to unavailable
		Driver dr = driverRepository3.findById(driverId).get();
		Cab cb = dr.getCab();
		cb.setAvailable(false);
		cabRepository3.save(cb);
		driverRepository3.save(dr);

	}
}
