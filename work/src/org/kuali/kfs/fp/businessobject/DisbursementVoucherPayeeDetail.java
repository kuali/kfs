/*
 * Copyright 2005-2006 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kuali.kfs.fp.businessobject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.document.service.DisbursementVoucherPayeeService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.location.api.LocationConstants;
import org.kuali.rice.location.framework.country.CountryEbo;
import org.kuali.rice.location.framework.postalcode.PostalCodeEbo;
import org.kuali.rice.location.framework.state.StateEbo;

/**
 * This class is used to represent a disbursement voucher payee detail.
 */
public class DisbursementVoucherPayeeDetail extends PersistableBusinessObjectBase {

    private String documentNumber;
    private String disbVchrPaymentReasonCode;

    private String disbVchrPayeeIdNumber;
    private String disbVchrPayeePersonName;

    private String disbVchrPayeeLine1Addr;
    private String disbVchrPayeeLine2Addr;
    private String disbVchrPayeeCityName;
    private String disbVchrPayeeStateCode;
    private String disbVchrPayeeZipCode;
    private String disbVchrPayeeCountryCode;

    private String disbVchrSpecialHandlingPersonName;
    private String disbVchrSpecialHandlingLine1Addr;
    private String disbVchrSpecialHandlingLine2Addr;
    private String disbVchrSpecialHandlingCityName;
    private String disbVchrSpecialHandlingStateCode;
    private String disbVchrSpecialHandlingZipCode;
    private String disbVchrSpecialHandlingCountryCode;

    private Boolean dvPayeeSubjectPaymentCode;
    private Boolean disbVchrAlienPaymentCode;
    private Boolean disbVchrPayeeEmployeeCode;
    private Boolean disbVchrEmployeePaidOutsidePayrollCode;
    private String disbursementVoucherPayeeTypeCode;

    private PaymentReasonCode disbVchrPaymentReason;

    // The following vendor-associated attributes are for convenience only and are not mapped to OJB or the DB.
    private String disbVchrVendorHeaderIdNumber;
    private String disbVchrVendorDetailAssignedIdNumber;
    private String disbVchrVendorAddressIdNumber;
    private boolean hasMultipleVendorAddresses = false;
    
    private StateEbo disbVchrPayeeState;
    private CountryEbo disbVchrPayeeCountry;    
    private PostalCodeEbo disbVchrPayeePostalZipCode;

    /**
     * Default no-arg constructor.
     */
    public DisbursementVoucherPayeeDetail() {
        super();
    }

    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber
     */
    public String getDocumentNumber() {
        return documentNumber;
    }


    /**
     * Sets the documentNumber attribute.
     * 
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the disbVchrPaymentReasonCode attribute.
     * 
     * @return Returns the disbVchrPaymentReasonCode
     */
    public String getDisbVchrPaymentReasonCode() {
        return disbVchrPaymentReasonCode;
    }


    /**
     * Sets the disbVchrPaymentReasonCode attribute.
     * 
     * @param disbVchrPaymentReasonCode The disbVchrPaymentReasonCode to set.
     */
    public void setDisbVchrPaymentReasonCode(String disbVchrPaymentReasonCode) {
        this.disbVchrPaymentReasonCode = disbVchrPaymentReasonCode;
    }

    /**
     * Gets the disbVchrPayeeIdNumber attribute.
     * 
     * @return Returns the disbVchrVendorIdNumber
     */
    public String getDisbVchrPayeeIdNumber() {
        return disbVchrPayeeIdNumber;
    }


    /**
     * Sets the disbVchrPayeeIdNumber attribute.
     * 
     * @param disbVchrPayeeIdNumber The disbVchrPayeeIdNumber to set.
     */
    public void setDisbVchrPayeeIdNumber(String disbVchrPayeeIdNumber) {
        this.disbVchrPayeeIdNumber = disbVchrPayeeIdNumber;
        // KFSMI-5976 : Blanking out these fields so they are re-derived upon next access 
        disbVchrVendorHeaderIdNumber = null;
        disbVchrVendorDetailAssignedIdNumber = null;
    }

    /**
     * This method...
     * 
     * @return
     */
    public String getDisbVchrVendorHeaderIdNumber() {
        if (this.isVendor()) {
            if (StringUtils.isBlank(disbVchrVendorHeaderIdNumber)) {
                int dashIndex = disbVchrPayeeIdNumber.indexOf('-');
                disbVchrVendorHeaderIdNumber = disbVchrPayeeIdNumber.substring(0, dashIndex);
            }
        }
        else { // Return null if payee is not a vendor
            return null;
        }
        return disbVchrVendorHeaderIdNumber;
    }

    /**
     * This method...
     * 
     * @param disbVchrVendorheaderIdNumber
     */
    public void setDisbVchrVendorHeaderIdNumber(String disbVchrVendorHeaderIdNumber) {
        if (this.isVendor()) {
            this.disbVchrVendorHeaderIdNumber = disbVchrVendorHeaderIdNumber;
        }
    }

    /**
     * Gets the disbVchrVendorIdNumber attribute as an Integer.
     * 
     * @return Returns the disbVchrVendorIdNumber in Integer form. This is the format used on the VendorDetail.
     */
    public Integer getDisbVchrVendorHeaderIdNumberAsInteger() {
        if (getDisbVchrVendorHeaderIdNumber() != null)
            try {
                return new Integer(getDisbVchrVendorHeaderIdNumber());
            }
            catch (NumberFormatException nfe) {
                nfe.printStackTrace();
            }
        return null;
    }

    /**
     * This method...
     * 
     * @return
     */
    public String getDisbVchrVendorDetailAssignedIdNumber() {
        if (this.isVendor()) {
            if (StringUtils.isBlank(disbVchrVendorDetailAssignedIdNumber)) {
                int dashIndex = disbVchrPayeeIdNumber.indexOf('-');
                disbVchrVendorDetailAssignedIdNumber = disbVchrPayeeIdNumber.substring(dashIndex + 1);
            }
        }
        else { // Return null if payee is not a vendor
            return null;
        }
        return disbVchrVendorDetailAssignedIdNumber;
    }

    /**
     * This method...
     * 
     * @param disbVchrVendorDetailAssignedIdNumber
     */
    public void setDisbVchrVendorDetailAssignedIdNumber(String disbVchrVendorDetailAssignedIdNumber) {
        // This field should only be set if the payee type is "V", otherwise, ignore any calls
        if (this.isVendor()) {
            this.disbVchrVendorDetailAssignedIdNumber = disbVchrVendorDetailAssignedIdNumber;
        }
    }

    /**
     * This method...
     * 
     * @return
     */
    public Integer getDisbVchrVendorDetailAssignedIdNumberAsInteger() {
        if (getDisbVchrVendorDetailAssignedIdNumber() != null)
            try {
                return new Integer(getDisbVchrVendorDetailAssignedIdNumber());
            }
            catch (NumberFormatException nfe) {
                nfe.printStackTrace();
            }
        return null;
    }

    /**
     * This method should only be called for retrieving the id associated with the payee if the payee type is equal to "E".
     * Otherwise, this method will return null.
     * 
     * @return The id of the universal user set as the payee on the DV, if the payee type code indicates the payee is an employee.
     *         Otherwise, this method will return null.
     */
    public String getDisbVchrEmployeeIdNumber() {
        if (this.isEmployee()) {
            return disbVchrPayeeIdNumber;
        }
        else { // Return null if payee is not a employee
            return null;
        }
    }

    /**
     * Gets the disbVchrPayeePersonName attribute.
     * 
     * @return Returns the disbVchrPayeePersonName
     */
    public String getDisbVchrPayeePersonName() {
        return disbVchrPayeePersonName;
    }

    /**
     * Sets the disbVchrPayeePersonName attribute.
     * 
     * @param disbVchrPayeePersonName The disbVchrPayeePersonName to set.
     */
    public void setDisbVchrPayeePersonName(String disbVchrPayeePersonName) {
        this.disbVchrPayeePersonName = disbVchrPayeePersonName;
    }

    /**
     * Gets the disbVchrPayeeLine1Addr attribute.
     * 
     * @return Returns the disbVchrPayeeLine1Addr
     */
    public String getDisbVchrPayeeLine1Addr() {
        return disbVchrPayeeLine1Addr;
    }

    /**
     * Sets the disbVchrPayeeLine1Addr attribute.
     * 
     * @param disbVchrPayeeLine1Addr The disbVchrPayeeLine1Addr to set.
     */
    public void setDisbVchrPayeeLine1Addr(String disbVchrPayeeLine1Addr) {
        this.disbVchrPayeeLine1Addr = disbVchrPayeeLine1Addr;
    }

    /**
     * Gets the disbVchrPayeeLine2Addr attribute.
     * 
     * @return Returns the disbVchrPayeeLine2Addr
     */
    public String getDisbVchrPayeeLine2Addr() {
        return disbVchrPayeeLine2Addr;
    }

    /**
     * Sets the disbVchrPayeeLine2Addr attribute.
     * 
     * @param disbVchrPayeeLine2Addr The disbVchrPayeeLine2Addr to set.
     */
    public void setDisbVchrPayeeLine2Addr(String disbVchrPayeeLine2Addr) {
        this.disbVchrPayeeLine2Addr = disbVchrPayeeLine2Addr;
    }

    /**
     * Gets the disbVchrPayeeCityName attribute.
     * 
     * @return Returns the disbVchrPayeeCityName
     */
    public String getDisbVchrPayeeCityName() {
        return disbVchrPayeeCityName;
    }


    /**
     * Sets the disbVchrPayeeCityName attribute.
     * 
     * @param disbVchrPayeeCityName The disbVchrPayeeCityName to set.
     */
    public void setDisbVchrPayeeCityName(String disbVchrPayeeCityName) {
        this.disbVchrPayeeCityName = disbVchrPayeeCityName;
    }

    /**
     * Gets the disbVchrPayeeStateCode attribute.
     * 
     * @return Returns the disbVchrPayeeStateCode
     */
    public String getDisbVchrPayeeStateCode() {
        return disbVchrPayeeStateCode;
    }


    /**
     * Sets the disbVchrPayeeStateCode attribute.
     * 
     * @param disbVchrPayeeStateCode The disbVchrPayeeStateCode to set.
     */
    public void setDisbVchrPayeeStateCode(String disbVchrPayeeStateCode) {
        this.disbVchrPayeeStateCode = disbVchrPayeeStateCode;
    }

    /**
     * Gets the disbVchrPayeeZipCode attribute.
     * 
     * @return Returns the disbVchrPayeeZipCode
     */
    public String getDisbVchrPayeeZipCode() {
        return disbVchrPayeeZipCode;
    }


    /**
     * Sets the disbVchrPayeeZipCode attribute.
     * 
     * @param disbVchrPayeeZipCode The disbVchrPayeeZipCode to set.
     */
    public void setDisbVchrPayeeZipCode(String disbVchrPayeeZipCode) {
        this.disbVchrPayeeZipCode = disbVchrPayeeZipCode;
    }

    /**
     * Gets the disbVchrPayeeCountryCode attribute.
     * 
     * @return Returns the disbVchrPayeeCountryCode
     */
    public String getDisbVchrPayeeCountryCode() {
        return disbVchrPayeeCountryCode;
    }


    /**
     * Sets the disbVchrPayeeCountryCode attribute.
     * 
     * @param disbVchrPayeeCountryCode The disbVchrPayeeCountryCode to set.
     */
    public void setDisbVchrPayeeCountryCode(String disbVchrPayeeCountryCode) {
        this.disbVchrPayeeCountryCode = disbVchrPayeeCountryCode;
    }

    /**
     * Gets the disbVchrSpecialHandlingPersonName attribute.
     * 
     * @return Returns the disbVchrSpecialHandlingPersonName
     */
    public String getDisbVchrSpecialHandlingPersonName() {
        return disbVchrSpecialHandlingPersonName;
    }


    /**
     * Sets the disbVchrSpecialHandlingPersonName attribute.
     * 
     * @param disbVchrSpecialHandlingPersonName The disbVchrSpecialHandlingPersonName to set.
     */
    public void setDisbVchrSpecialHandlingPersonName(String disbVchrSpecialHandlingPersonName) {
        this.disbVchrSpecialHandlingPersonName = disbVchrSpecialHandlingPersonName;
    }

    /**
     * Gets the disbVchrSpecialHandlingLine1Addr attribute.
     * 
     * @return Returns the disbVchrSpecialHandlingLine1Addr
     */
    public String getDisbVchrSpecialHandlingLine1Addr() {
        return disbVchrSpecialHandlingLine1Addr;
    }


    /**
     * Sets the disbVchrSpecialHandlingLine1Addr attribute.
     * 
     * @param disbVchrSpecialHandlingLine1Addr The disbVchrSpecialHandlingLine1Addr to set.
     */
    public void setDisbVchrSpecialHandlingLine1Addr(String disbVchrSpecialHandlingLine1Addr) {
        this.disbVchrSpecialHandlingLine1Addr = disbVchrSpecialHandlingLine1Addr;
    }

    /**
     * Gets the disbVchrSpecialHandlingLine2Addr attribute.
     * 
     * @return Returns the disbVchrSpecialHandlingLine2Addr
     */
    public String getDisbVchrSpecialHandlingLine2Addr() {
        return disbVchrSpecialHandlingLine2Addr;
    }


    /**
     * Sets the disbVchrSpecialHandlingLine2Addr attribute.
     * 
     * @param disbVchrSpecialHandlingLine2Addr The disbVchrSpecialHandlingLine2Addr to set.
     */
    public void setDisbVchrSpecialHandlingLine2Addr(String disbVchrSpecialHandlingLine2Addr) {
        this.disbVchrSpecialHandlingLine2Addr = disbVchrSpecialHandlingLine2Addr;
    }

    /**
     * Gets the disbVchrSpecialHandlingCityName attribute.
     * 
     * @return Returns the disbVchrSpecialHandlingCityName
     */
    public String getDisbVchrSpecialHandlingCityName() {
        return disbVchrSpecialHandlingCityName;
    }


    /**
     * Sets the disbVchrSpecialHandlingCityName attribute.
     * 
     * @param disbVchrSpecialHandlingCityName The disbVchrSpecialHandlingCityName to set.
     */
    public void setDisbVchrSpecialHandlingCityName(String disbVchrSpecialHandlingCityName) {
        this.disbVchrSpecialHandlingCityName = disbVchrSpecialHandlingCityName;
    }

    /**
     * Gets the disbVchrSpecialHandlingStateCode attribute.
     * 
     * @return Returns the disbVchrSpecialHandlingStateCode
     */
    public String getDisbVchrSpecialHandlingStateCode() {
        return disbVchrSpecialHandlingStateCode;
    }


    /**
     * Sets the disbVchrSpecialHandlingStateCode attribute.
     * 
     * @param disbVchrSpecialHandlingStateCode The disbVchrSpecialHandlingStateCode to set.
     */
    public void setDisbVchrSpecialHandlingStateCode(String disbVchrSpecialHandlingStateCode) {
        this.disbVchrSpecialHandlingStateCode = disbVchrSpecialHandlingStateCode;
    }

    /**
     * Gets the disbVchrSpecialHandlingZipCode attribute.
     * 
     * @return Returns the disbVchrSpecialHandlingZipCode
     */
    public String getDisbVchrSpecialHandlingZipCode() {
        return disbVchrSpecialHandlingZipCode;
    }


    /**
     * Sets the disbVchrSpecialHandlingZipCode attribute.
     * 
     * @param disbVchrSpecialHandlingZipCode The disbVchrSpecialHandlingZipCode to set.
     */
    public void setDisbVchrSpecialHandlingZipCode(String disbVchrSpecialHandlingZipCode) {
        this.disbVchrSpecialHandlingZipCode = disbVchrSpecialHandlingZipCode;
    }

    /**
     * Gets the disbVchrSpecialHandlingCountryCode attribute.
     * 
     * @return Returns the disbVchrSpecialHandlingCountryCode
     */
    public String getDisbVchrSpecialHandlingCountryCode() {
        return disbVchrSpecialHandlingCountryCode;
    }


    /**
     * Sets the disbVchrSpecialHandlingCountryCode attribute.
     * 
     * @param disbVchrSpecialHandlingCountryCode The disbVchrSpecialHandlingCountryCode to set.
     */
    public void setDisbVchrSpecialHandlingCountryCode(String disbVchrSpecialHandlingCountryCode) {
        this.disbVchrSpecialHandlingCountryCode = disbVchrSpecialHandlingCountryCode;
    }

    /**
     * Gets the disbVchrPayeeEmployeeCode attribute.
     * 
     * @return Returns true if the vendor associated with this DV is an employee of the institution.
     */
    public boolean isDisbVchrPayeeEmployeeCode() {
        if (ObjectUtils.isNull(disbVchrPayeeEmployeeCode)) {
            if (this.isEmployee()) {
                disbVchrPayeeEmployeeCode = true;
            }
            else if (this.isVendor()) {
                try {
                    disbVchrPayeeEmployeeCode = SpringContext.getBean(VendorService.class).isVendorInstitutionEmployee(getDisbVchrVendorHeaderIdNumberAsInteger());
                    this.setDisbVchrEmployeePaidOutsidePayrollCode(disbVchrPayeeEmployeeCode);
                }
                catch (Exception ex) {
                    disbVchrPayeeEmployeeCode = false;
                    ex.printStackTrace();
                }
            }
        }
        return disbVchrPayeeEmployeeCode;
    }


    /**
     * Sets the disbVchrPayeeEmployeeCode attribute.
     * 
     * @param disbVchrPayeeEmployeeCode The disbVchrPayeeEmployeeCode to set.
     */
    public void setDisbVchrPayeeEmployeeCode(boolean disbVchrPayeeEmployeeCode) {
        this.disbVchrPayeeEmployeeCode = disbVchrPayeeEmployeeCode;
    }

    /**
     * Gets the disbVchrAlienPaymentCode attribute.
     * 
     * @return Returns the disbVchrAlienPaymentCode
     */
    public boolean isDisbVchrAlienPaymentCode() {
        if (StringUtils.isNotBlank(this.getDisbVchrEmployeeIdNumber()) && this.isVendor()) {
            try {
                disbVchrAlienPaymentCode = SpringContext.getBean(VendorService.class).isVendorForeign(getDisbVchrVendorHeaderIdNumberAsInteger());
            }
            catch (Exception ex) {
                disbVchrAlienPaymentCode = false;
                ex.printStackTrace();
            }
        }

        return ObjectUtils.isNull(disbVchrAlienPaymentCode) ? false : disbVchrAlienPaymentCode;
    }


    /**
     * Sets the disbVchrAlienPaymentCode attribute.
     * 
     * @param disbVchrAlienPaymentCode The disbVchrAlienPaymentCode to set.
     */
    public void setDisbVchrAlienPaymentCode(boolean disbVchrAlienPaymentCode) {
        this.disbVchrAlienPaymentCode = disbVchrAlienPaymentCode;
    }

    /**
     * Gets the dvPayeeSubjectPayment attribute.
     * 
     * @return Returns the dvPayeeSubjectPayment
     */
    public boolean isDvPayeeSubjectPaymentCode() {
        if (ObjectUtils.isNull(dvPayeeSubjectPaymentCode) && (getDisbVchrVendorHeaderIdNumberAsInteger() != null)) {
            dvPayeeSubjectPaymentCode = SpringContext.getBean(VendorService.class).isSubjectPaymentVendor(getDisbVchrVendorHeaderIdNumberAsInteger());
        }
        return dvPayeeSubjectPaymentCode;
    }

    /**
     * Sets the dvPayeeSubjectPayment attribute.
     * 
     * @param dvPayeeSubjectPayment The dvPayeeSubjectPayment to set.
     */
    public void setDvPayeeSubjectPaymentCode(boolean dvPayeeSubjectPaymentCode) {
        this.dvPayeeSubjectPaymentCode = dvPayeeSubjectPaymentCode;
    }

    /**
     * Gets the disbVchrEmployeePaidOutsidePayrollCode attribute.
     * 
     * @return Returns the disbVchrEmployeePaidOutsidePayrollCode.
     */
    public boolean isDisbVchrEmployeePaidOutsidePayrollCode() {
        return disbVchrEmployeePaidOutsidePayrollCode;
    }

    /**
     * Gets the disbVchrEmployeePaidOutsidePayrollCode attribute.
     * 
     * @return Returns the disbVchrEmployeePaidOutsidePayrollCode.
     */
    public boolean getDisbVchrEmployeePaidOutsidePayrollCode() {
        return disbVchrEmployeePaidOutsidePayrollCode;
    }

    /**
     * Sets the disbVchrEmployeePaidOutsidePayrollCode attribute value.
     * 
     * @param disbVchrEmployeePaidOutsidePayrollCode The disbVchrEmployeePaidOutsidePayrollCode to set.
     */
    public void setDisbVchrEmployeePaidOutsidePayrollCode(boolean disbVchrEmployeePaidOutsidePayrollCode) {
        this.disbVchrEmployeePaidOutsidePayrollCode = disbVchrEmployeePaidOutsidePayrollCode;
    }

    /**
     * Gets the disbVchrPaymentReason attribute.
     * 
     * @return Returns the disbVchrPaymentReason
     */
    public PaymentReasonCode getDisbVchrPaymentReason() {
        return disbVchrPaymentReason;
    }


    /**
     * Sets the disbVchrPaymentReason attribute.
     * 
     * @param disbVchrPaymentReason The disbVchrPaymentReason to set.
     * @deprecated
     */
    public void setDisbVchrPaymentReason(PaymentReasonCode disbVchrPaymentReason) {
        this.disbVchrPaymentReason = disbVchrPaymentReason;
    }

    /**
     * @return Returns the disbursementVoucherPayeeTypeCode.
     */
    public String getDisbursementVoucherPayeeTypeCode() {
        return disbursementVoucherPayeeTypeCode;
    }

    /**
     * @param disbursementVoucherPayeeTypeCode The disbursementVoucherPayeeTypeCode to set.
     */
    public void setDisbursementVoucherPayeeTypeCode(String disbursementVoucherPayeeTypeCode) {
        this.disbursementVoucherPayeeTypeCode = disbursementVoucherPayeeTypeCode;
    }

    /**
     * @return Returns the payee type name
     */
    public String getDisbursementVoucherPayeeTypeName() {
        DisbursementVoucherPayeeService payeeService = SpringContext.getBean(DisbursementVoucherPayeeService.class);

        return payeeService.getPayeeTypeDescription(disbursementVoucherPayeeTypeCode);
    }

    /**
     * This method is a dummy method defined for OJB.
     * 
     * @param name
     */
    public void setDisbursementVoucherPayeeTypeName(String name) {
    }

    /**
     * Returns the name associated with the payment reason name
     * 
     * @return
     */
    public String getDisbVchrPaymentReasonName() {
        this.refreshReferenceObject(KFSPropertyConstants.DISB_VCHR_PAYMENT_REASON);
        return this.getDisbVchrPaymentReason().getCodeAndDescription();
    }

    /**
     * This method is a dummy method defined for OJB.
     * 
     * @param name
     */
    public void setDisbVchrPaymentReasonName(String name) {
    }

    /**
     * Gets the disbVchrVendorAddressIdNumber attribute.
     * 
     * @return Returns the disbVchrVendorAddressIdNumber.
     */
    public String getDisbVchrVendorAddressIdNumber() {
        return disbVchrVendorAddressIdNumber;
    }

    /**
     * Gets the disbVchrVendorAddressIdNumber attribute.
     * 
     * @return Returns the disbVchrVendorAddressIdNumber.
     */
    public Integer getDisbVchrVendorAddressIdNumberAsInteger() {
        if (getDisbVchrVendorAddressIdNumber() != null)
            try {
                return new Integer(getDisbVchrVendorAddressIdNumber());
            }
            catch (NumberFormatException nfe) {
                nfe.printStackTrace();
            }
        return null;
    }

    /**
     * Sets the disbVchrVendorAddressIdNumber attribute value.
     * 
     * @param disbVchrVendorAddressIdNumber The disbVchrVendorAddressIdNumber to set.
     */
    public void setDisbVchrVendorAddressIdNumber(String disbVchrVendorAddressIdNumber) {
        this.disbVchrVendorAddressIdNumber = disbVchrVendorAddressIdNumber;
    }

    /**
     * Gets the hasMultipleVendorAddresses attribute.
     * 
     * @return Returns the hasMultipleVendorAddresses.
     */
    public Boolean getHasMultipleVendorAddresses() {
        return hasMultipleVendorAddresses;
    }

    /**
     * Sets the hasMultipleVendorAddresses attribute value.
     * 
     * @param hasMultipleVendorAddresses The hasMultipleVendorAddresses to set.
     */
    public void setHasMultipleVendorAddresses(boolean hasMultipleVendorAddresses) {
        this.hasMultipleVendorAddresses = hasMultipleVendorAddresses;
    }
    
    /**
     * Gets the disbVchrPayeeState attribute.
     *
     * @return Returns the disbVchrPayeeState.
     */
    public StateEbo getDisbVchrPayeeState() {
        if ( StringUtils.isBlank(disbVchrPayeeStateCode) || StringUtils.isBlank(KFSConstants.COUNTRY_CODE_UNITED_STATES ) ) {
            disbVchrPayeeState = null;
        } else {
            if ( disbVchrPayeeState == null || !StringUtils.equals( disbVchrPayeeState.getCode(),disbVchrPayeeStateCode) || !StringUtils.equals(disbVchrPayeeState.getCountryCode(), KFSConstants.COUNTRY_CODE_UNITED_STATES ) ) {
                ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(StateEbo.class);
                if ( moduleService != null ) {
                    Map<String,Object> keys = new HashMap<String, Object>(2);
                    keys.put(LocationConstants.PrimaryKeyConstants.COUNTRY_CODE, KFSConstants.COUNTRY_CODE_UNITED_STATES);/*RICE20_REFACTORME*/
                    keys.put(LocationConstants.PrimaryKeyConstants.CODE, disbVchrPayeeStateCode);
                    disbVchrPayeeState = moduleService.getExternalizableBusinessObject(StateEbo.class, keys);
                } else {
                    throw new RuntimeException( "CONFIGURATION ERROR: No responsible module found for EBO class.  Unable to proceed." );
                }
            }
        }
        
        return disbVchrPayeeState;
    }
    
    /**
     * Sets the disbVchrPayeeState attribute value.
     * @param disbVchrPayeeState The disbVchrPayeeState to set.
     */
    public void setDisbVchrPayeeState(StateEbo disbVchrPayeeState) {
        this.disbVchrPayeeState = disbVchrPayeeState;
    }

    /**
     * Gets the disbVchrPayeeCountry attribute.
     *
     * @return Returns the disbVchrPayeeCountry.
     */
    public CountryEbo getDisbVchrPayeeCountry() {
        if ( StringUtils.isBlank(disbVchrPayeeCountryCode) ) {
            disbVchrPayeeCountry = null;
        } else {
            if ( disbVchrPayeeCountry == null || !StringUtils.equals( disbVchrPayeeCountry.getCode(),disbVchrPayeeCountryCode) ) {
                ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(CountryEbo.class);
                if ( moduleService != null ) {
                    Map<String,Object> keys = new HashMap<String, Object>(1);
                    keys.put(LocationConstants.PrimaryKeyConstants.CODE, disbVchrPayeeCountryCode);
                    disbVchrPayeeCountry = moduleService.getExternalizableBusinessObject(CountryEbo.class, keys);
                } else {
                    throw new RuntimeException( "CONFIGURATION ERROR: No responsible module found for EBO class.  Unable to proceed." );
                }
            }
        }
        return disbVchrPayeeCountry;
    }
    
    /**
     * Sets the disbVchrPayeeCountry attribute value.
     * @param disbVchrPayeeCountry The disbVchrPayeeCountry to set.
     */
    public void setDisbVchrPayeeCountry(CountryEbo disbVchrPayeeCountry) {
        this.disbVchrPayeeCountry = disbVchrPayeeCountry;
    }

    
    /**
     * Gets the disbVchrPayeePostalZipCode attribute.
     *
     * @return Returns the disbVchrPayeePostalZipCode.
     */
    public PostalCodeEbo getDisbVchrPayeePostalZipCode() {
        if ( StringUtils.isBlank(disbVchrPayeeZipCode) || StringUtils.isBlank(KFSConstants.COUNTRY_CODE_UNITED_STATES ) ) {
            disbVchrPayeePostalZipCode = null;
        } else {
            if ( disbVchrPayeePostalZipCode == null || !StringUtils.equals( disbVchrPayeePostalZipCode.getCode(), disbVchrPayeeZipCode) || !StringUtils.equals(disbVchrPayeePostalZipCode.getCountryCode(), KFSConstants.COUNTRY_CODE_UNITED_STATES ) ) {
                ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(PostalCodeEbo.class);
                if ( moduleService != null ) {
                    Map<String,Object> keys = new HashMap<String, Object>(2);
                    keys.put(LocationConstants.PrimaryKeyConstants.COUNTRY_CODE, KFSConstants.COUNTRY_CODE_UNITED_STATES);/*RICE20_REFACTORME*/
                    keys.put(LocationConstants.PrimaryKeyConstants.CODE, disbVchrPayeeZipCode);
                    disbVchrPayeePostalZipCode = moduleService.getExternalizableBusinessObject(PostalCodeEbo.class, keys);
                } else {
                    throw new RuntimeException( "CONFIGURATION ERROR: No responsible module found for EBO class.  Unable to proceed." );
                }
            }
        }
        
        return disbVchrPayeePostalZipCode;
    }
    
    /**
     * Sets the disbVchrPayeePostalZipCode attribute value.
     * @param disbVchrPayeePostalZipCode The disbVchrPayeePostalZipCode to set.
     */
    public void setDisbVchrPayeePostalZipCode(PostalCodeEbo disbVchrPayeePostalZipCode) {
        this.disbVchrPayeePostalZipCode = disbVchrPayeePostalZipCode;
    }
    

    /**
     * Checks the payee type code for vendor type
     */
    public boolean isVendor() {
        return SpringContext.getBean(DisbursementVoucherPayeeService.class).isVendor(this);
    }

    /**
     * Checks the payee type code for employee type
     */
    public boolean isEmployee() {
        return SpringContext.getBean(DisbursementVoucherPayeeService.class).isEmployee(this);
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        return m;
    }

    /**
     * This method...
     * 
     * @param compareDetail
     * @return
     */
    public boolean hasSameAddress(DisbursementVoucherPayeeDetail compareDetail) {
        boolean isEqual = true;

        isEqual &= nullSafeEquals(this.getDisbVchrPayeeLine1Addr(), compareDetail.getDisbVchrPayeeLine1Addr());
        isEqual &= nullSafeEquals(this.getDisbVchrPayeeLine2Addr(), compareDetail.getDisbVchrPayeeLine2Addr());
        isEqual &= nullSafeEquals(this.getDisbVchrPayeeCityName(), compareDetail.getDisbVchrPayeeCityName());
        isEqual &= nullSafeEquals(this.getDisbVchrPayeeStateCode(), compareDetail.getDisbVchrPayeeStateCode());
        isEqual &= nullSafeEquals(this.getDisbVchrPayeeZipCode(), compareDetail.getDisbVchrPayeeZipCode());
        isEqual &= nullSafeEquals(this.getDisbVchrPayeeCountryCode(), compareDetail.getDisbVchrPayeeCountryCode());

        return isEqual;
    }
    
    /**
     * Returns the equality of the two given objects, automatically handling when one or both of the objects is null.
     * 
     * @param obj1
     * @param obj2
     * 
     * @return true if both objects are null or both are equal
     */
    private boolean nullSafeEquals(Object obj1, Object obj2) {
        if (obj1 != null && obj2 != null) {
            return obj1.equals(obj2);
        }
        else {
            return (obj1 == obj2);
        }
    }
    
    /**
     * This method creates a string representation of the address assigned to this payee.
     * 
     * @return
     */
    public String getAddressAsString() {
        StringBuffer address = new StringBuffer();

        address.append(this.getDisbVchrPayeeLine1Addr()).append(", ");
        address.append(this.getDisbVchrPayeeLine2Addr()).append(", ");
        address.append(this.getDisbVchrPayeeCityName()).append(", ");
        address.append(this.getDisbVchrPayeeStateCode()).append(" ");
        address.append(this.getDisbVchrPayeeZipCode()).append(", ");
        address.append(this.getDisbVchrPayeeCountryCode());

        return address.toString();
    }
}
