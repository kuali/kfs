package edu.arizona.kfs.sys.service;

import java.util.List;
import java.util.Map;

import org.kuali.kfs.vnd.businessobject.VendorHeader;

import edu.arizona.kfs.sys.businessobject.IncomeType;

/**
 * This service provides common functionality for the IncomeTypeHandlers
 *
 * @author kosta@email.arizona.edu
 */
public interface IncomeTypeHandlerService {

    /**
     * This method returns the Object Codes that are reportable on a 1099.
     */
    public List<String> getExtractCodes();

    /**
     * This method returns the payment type code for the associated vendor.
     */
    public String getOverridePaymentType(VendorHeader vendorHeader);

    /**
     * This method retrieves a Map where the key is the object code and the value is default payment type code.
     */
    public Map<String, String> getObjectCodeMap();

    /**
     * This method retrieves a Map where the key is the Income Type Code and the value is the associated IncomeType object.
     */
    public Map<String, IncomeType> getIncomeTypeMap();
}
