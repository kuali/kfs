package edu.arizona.kfs.module.ld.service;

import java.util.Date;

public interface EreSweepService {

	/**
	 * Starts job process
	 * 
	 * @param jobRunDate
	 */
	public void processEreSweep(Date jobRunDate);

}
