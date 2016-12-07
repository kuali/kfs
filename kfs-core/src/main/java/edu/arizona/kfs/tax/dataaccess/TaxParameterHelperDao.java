package edu.arizona.kfs.tax.dataaccess;

import java.util.List;

/**
 * This DAO is for acquiring parameter information from the database. These methods had to be moved from TaxReporting1099Dao because they are needed in KFS-CORE.
 * 
 * @author akost
 */

public interface TaxParameterHelperDao {

	/**
	 * This method retrieves object codes based on the type.
	 * 
	 * @param type
	 * @param values
	 * @return
	 */
	public List<String> getObjectCodes(String type, List<String> values);
}
