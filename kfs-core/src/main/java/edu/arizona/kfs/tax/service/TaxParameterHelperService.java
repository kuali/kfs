package edu.arizona.kfs.tax.service;

import java.sql.Timestamp;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.vnd.businessobject.VendorHeader;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * This Service is for acquiring parameter information. These methods had to be extracted from TaxReporting1099Service and TaxHelper because they are needed in KFS-CORE.
 * 
 * @author akost
 */
public interface TaxParameterHelperService {

	/**
	 * Retrieves a Map of Override Payment Type Codes based on the value of the 1099_EXTRACT_OVERRIDE_PMT_TYPE_CODE parameter.
	 * 
	 * @return
	 */
	public Map<String, String> getOverridePaymentTypeCodeMap();

	/**
	 * Retrives a map of Object Codes based on the value of the 1099_OBJECT_CODES parameter.
	 * 
	 * @return
	 */
	public Map<String, String> getObjectCodeMap();

	/**
	 * Retrieves a Set of Overriding Object Codes (as Strings) based on the value of the 1099_OBJECT_CODES_OVERRIDING_RESTRICTIONS parameter.
	 * 
	 * @return
	 */
	public Set<String> getOverridingObjectCodes();

	/**
	 * Retrieves the Extract Type based on the value of the 1099_EXTRACT_TYPE parameter.
	 * 
	 * @return
	 */
	public String getExtractType();

	/**
	 * Retrieves a Set of valid Extract Codes (as Strings) based on the extractType and overriding Object Codes:
	 * extractType OBJECT uses the overriding Object Codes from 1099_EXTRACT_OBJECT_CODES parameter.
	 * extractType CONS uses the overriding Object Codes from 1099_EXTRACT_CONS_CODES parameter.
	 * extractType LEVEL uses the overriding Object Codes from 1099_EXTRACT_LEVEL_CODES parameter.
	 * 
	 * @return
	 */
	public Set<String> getExtractCodes(String extractType, Set<String> overridingObjCodes);

	/**
	 * Retrieves the Override Payment Type based on the Vendor Ownership Code and Ownership Category Code.
	 * 
	 * @return
	 */
	public String getOverridePaymentType(VendorHeader vendor, Map<String, String> pmtTypeCodes);

	/**
	 * Retrieves a Set of PaymentTypes (as Strings) based on the value and constraint code of the 1099_VENDOR_OWNER_CODES parameter.
	 * 
	 * @return
	 */
	public Set<String> getVendorOwnershipCodes() throws Exception;

	/**
	 * Determines if Vendor Ownership Codes are allowed based on the constraint code of the 1099_VENDOR_OWNER_CODES parameter.
	 * 
	 * @return
	 */
	public boolean isVendorOwnershipCodesAllow() throws Exception;

	/**
	 * Retrieves a Set of Address Type Codes (as Strings) based on the value of the 1099_PAYEE_ADDR_TYPE_CODES parameter.
	 * 
	 * @return
	 */
	public Set<String> getAddressTypeCodes();

	/**
	 * Retrieves an int based on the value of the 1099_REPORTING_PERIOD parameter.
	 * 
	 * @return
	 */
	public int getTaxYear();

	/**
	 * Retrieves a double based on the value of the 1099_TOTAL_TAX_AMOUNT parameter.
	 * 
	 * @return
	 */
	public double getIncomeThreshold();

	/**
	 * Retrieves a Timestamp based on the value of the 1099_PAYMENT_PERIOD_START parameter.
	 * 
	 * @return
	 */
	public Timestamp getPaymentStartDate() throws Exception;

	/**
	 * Retrieves a Timestamp based on the value of the 1099_PAYMENT_PERIOD_END parameter.
	 * 
	 * @return
	 */
	public Timestamp getPaymentEndDate() throws Exception;

	/**
	 * Retrieves a double based on the value of 1099_TOTAL_TAX_AMOUNT parameter.
	 * 
	 * @return
	 */
	public double getTaxThreshholdAmount();

	/**
	 * Generates a Map of values (PaymentType, DollarValue> based on the 1099_TAX_AMOUNT_BY_PAYMENT_TYPE parameter.
	 * 
	 * @return
	 */
	public Map<String, KualiDecimal> getTaxAmountByPaymentType();
}
