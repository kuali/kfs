package edu.arizona.kfs.module.tax.service;

import java.sql.Timestamp;
import java.util.Map;
import java.util.Set;

import org.kuali.rice.core.api.util.type.KualiDecimal;

import edu.arizona.kfs.module.tax.businessobject.Payer;

/**
 * This Service is for acquiring parameter information. These methods had to be extracted from TaxReporting1099Service and TaxHelper because they are
 * needed in KFS-CORE.
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
     * Retrieves a Set of PaymentTypes (as Strings) based on the value and constraint code of the 1099_VENDOR_OWNER_CODES parameter.
     * 
     * @return
     */
    public Set<String> getVendorOwnershipCodes();

    /**
     * Determines if Vendor Ownership Codes are allowed based on the constraint code of the 1099_VENDOR_OWNER_CODES parameter.
     * 
     * @return
     */
    public boolean isVendorOwnershipCodesAllow();

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
    public Timestamp getPaymentStartDate();

    /**
     * Retrieves a Timestamp based on the value of the 1099_PAYMENT_PERIOD_END parameter.
     * 
     * @return
     */
    public Timestamp getPaymentEndDate();

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

    /**
     * Retrieves the value of the 1099_REP_DATA_LOAD_IND parameter
     */
    public boolean getReplaceData();

    /**
     * Retrieves the default Payer by the 1099_PAYER_TRANSCD parameter
     * 
     * @return
     */
    public Payer getDefaultPayer();
}
