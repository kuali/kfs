package edu.arizona.kfs.module.ld.service;

import java.util.List;

public interface EreSweepParameterService {
	
	/**
	 * Returns value of parameter divided into sections
	 * 
	 * @return String[]
	 */
	public String[] getSubFundGroupParameters();
	
	/**
	 * Returns value of parameter divided into sections
	 * 
	 * @return String[]
	 */
	public String[] getObjectSubTypesParameters();
	
	/**
	 * Returns value parameter
	 * 
	 * @return String
	 */
	public String getFringeBudgetSweepIndParameters();
	
	/**
	 * Returns value of parameter
	 * 
	 * @return List<string>
	 */
	public List<String> getFiscalPeriodsToExclude();
	
	/**
	 * Returns value of parameter
	 * 
	 * @return List<String>
	 */
	public List<String> getBalanceTypesParameters();
	
}
