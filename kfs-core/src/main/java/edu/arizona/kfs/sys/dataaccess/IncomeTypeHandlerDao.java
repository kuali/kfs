package edu.arizona.kfs.sys.dataaccess;

import java.util.List;

/**
 * This DAO is for acquiring information from the database for use by the IncomeTypeHandlerService.
 *
 * @author kosta@email.arizona.edu
 */

public interface IncomeTypeHandlerDao {

    /**
     * This method retrieves object codes that match the listed financialObjectLevelCode.
     * 
     * @param values
     *            list of financialObjectLevelCode values
     * @return
     */
    public List<String> getObjectCodesByObjectLevelCodes(List<String> values);

}
