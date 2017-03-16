package edu.arizona.kfs.module.tax.businessobject;

import java.io.Serializable;
import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

import edu.arizona.kfs.module.tax.TaxPropertyConstants;

public class Payee extends PersistableBusinessObjectBase implements Serializable {
    private static final long serialVersionUID = 1L;

    protected Integer id;
    protected String headerTypeCode;
    protected String headerTaxNumber;
    protected String headerOwnershipCode;
    protected String headerOwnershipCategoryCode;
    protected String vendorName;
    protected String vendorNumber;
    protected String addressTypeCode;
    protected String addressLine1Address;
    protected String addressLine2Address;
    protected String addressCityName;
    protected String addressStateCode;
    protected String addressZipCode;
    protected String addressCountryCode;
    protected Integer taxYear;
    protected Boolean vendorForeignIndicator;
    protected Boolean correctionIndicator;
    private Boolean excludeIndicator;
    private Date vendorFederalWithholdingTaxBeginningDate;
    private Date vendorFederalWithholdingTaxEndDate;
    private Integer vendorHeaderGeneratedIdentifier;
    private Integer vendorDetailAssignedIdentifier;
    protected List<Payment> payments;

    private VendorDetail vendorDetail;

    public Payee() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getHeaderTypeCode() {
        return headerTypeCode;
    }

    public void setHeaderTypeCode(String headerTypeCode) {
        this.headerTypeCode = headerTypeCode;
    }

    public String getHeaderTaxNumber() {
        return headerTaxNumber;
    }

    public void setHeaderTaxNumber(String headerTaxNumber) {
        this.headerTaxNumber = headerTaxNumber;
    }

    public String getHeaderOwnershipCode() {
        return headerOwnershipCode;
    }

    public void setHeaderOwnershipCode(String headerOwnershipCode) {
        this.headerOwnershipCode = headerOwnershipCode;
    }

    public String getHeaderOwnershipCategoryCode() {
        return headerOwnershipCategoryCode;
    }

    public void setHeaderOwnershipCategoryCode(String headerOwnershipCategoryCode) {
        this.headerOwnershipCategoryCode = headerOwnershipCategoryCode;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getVendorNumber() {
        return vendorNumber;
    }

    public void setVendorNumber(String vendorNumber) {
        this.vendorNumber = vendorNumber;
    }

    public String getAddressTypeCode() {
        return addressTypeCode;
    }

    public void setAddressTypeCode(String addressTypeCode) {
        this.addressTypeCode = addressTypeCode;
    }

    public String getAddressLine1Address() {
        return addressLine1Address;
    }

    public void setAddressLine1Address(String addressLine1Address) {
        this.addressLine1Address = addressLine1Address;
    }

    public String getAddressLine2Address() {
        return addressLine2Address;
    }

    public void setAddressLine2Address(String addressLine2Address) {
        this.addressLine2Address = addressLine2Address;
    }

    public String getAddressCityName() {
        return addressCityName;
    }

    public void setAddressCityName(String addressCityName) {
        this.addressCityName = addressCityName;
    }

    public String getAddressStateCode() {
        return addressStateCode;
    }

    public void setAddressStateCode(String addressStateCode) {
        this.addressStateCode = addressStateCode;
    }

    public String getAddressZipCode() {
        return addressZipCode;
    }

    public void setAddressZipCode(String addressZipCode) {
        this.addressZipCode = addressZipCode;
    }

    public String getAddressCountryCode() {
        return addressCountryCode;
    }

    public void setAddressCountryCode(String addressCountryCode) {
        this.addressCountryCode = addressCountryCode;
    }

    public Integer getTaxYear() {
        return taxYear;
    }

    public void setTaxYear(Integer taxYear) {
        this.taxYear = taxYear;
    }

    public Boolean getVendorForeignIndicator() {
        return vendorForeignIndicator;
    }

    public void setVendorForeignIndicator(Boolean vendorForeignIndicator) {
        this.vendorForeignIndicator = vendorForeignIndicator;
    }

    public Boolean getCorrectionIndicator() {
        return correctionIndicator;
    }

    public void setCorrectionIndicator(Boolean correctionIndicator) {
        this.correctionIndicator = correctionIndicator;
    }

    public Boolean getExcludeIndicator() {
        return excludeIndicator;
    }

    public void setExcludeIndicator(Boolean excludeIndicator) {
        this.excludeIndicator = excludeIndicator;
    }

    public Date getVendorFederalWithholdingTaxBeginningDate() {
        return vendorFederalWithholdingTaxBeginningDate;
    }

    public void setVendorFederalWithholdingTaxBeginningDate(Date vendorFederalWithholdingTaxBeginningDate) {
        this.vendorFederalWithholdingTaxBeginningDate = vendorFederalWithholdingTaxBeginningDate;
    }

    public Date getVendorFederalWithholdingTaxEndDate() {
        return vendorFederalWithholdingTaxEndDate;
    }

    public void setVendorFederalWithholdingTaxEndDate(Date vendorFederalWithholdingTaxEndDate) {
        this.vendorFederalWithholdingTaxEndDate = vendorFederalWithholdingTaxEndDate;
    }

    public Integer getVendorHeaderGeneratedIdentifier() {
        return vendorHeaderGeneratedIdentifier;
    }

    public void setVendorHeaderGeneratedIdentifier(Integer vendorHeaderGeneratedIdentifier) {
        this.vendorHeaderGeneratedIdentifier = vendorHeaderGeneratedIdentifier;
    }

    public Integer getVendorDetailAssignedIdentifier() {
        return vendorDetailAssignedIdentifier;
    }

    public void setVendorDetailAssignedIdentifier(Integer vendorDetailAssignedIdentifier) {
        this.vendorDetailAssignedIdentifier = vendorDetailAssignedIdentifier;
    }

    public VendorDetail getVendorDetail() {
        return vendorDetail;
    }

    public void setVendorDetail(VendorDetail vendorDetail) {
        this.vendorDetail = vendorDetail;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }

    public LinkedHashMap<String, String> toStringMapper() {
        LinkedHashMap<String, String> retval = new LinkedHashMap<String, String>();
        retval.put(TaxPropertyConstants.PayeeFields.PAYEE_ID, getId().toString());
        retval.put(TaxPropertyConstants.PayeeFields.HEADER_TYPE_CODE, getHeaderTypeCode());
        retval.put(TaxPropertyConstants.PayeeFields.HEADER_TAX_NUMBER, getHeaderTaxNumber());
        retval.put(TaxPropertyConstants.PayeeFields.HEADER_OWNERSHIP_CODE, getHeaderOwnershipCode());
        retval.put(TaxPropertyConstants.PayeeFields.HEADER_OWNERSHIP_CATEGORY_CODE, getHeaderOwnershipCategoryCode());
        retval.put(TaxPropertyConstants.PayeeFields.VENDOR_NAME, getVendorName());
        retval.put(TaxPropertyConstants.PayeeFields.ADDRESS_TYPE_CODE, getAddressTypeCode());
        retval.put(TaxPropertyConstants.PayeeFields.ADDRESS_LINE1_ADDRESS, getAddressLine1Address());
        retval.put(TaxPropertyConstants.PayeeFields.ADDRESS_LINE2_ADDRESS, getAddressLine2Address());
        retval.put(TaxPropertyConstants.PayeeFields.ADDRESS_CITY_NAME, getAddressCityName());
        retval.put(TaxPropertyConstants.PayeeFields.ADDRESS_STATE_CODE, getAddressStateCode());
        retval.put(TaxPropertyConstants.PayeeFields.ADDRESS_ZIP_CODE, getAddressZipCode());
        retval.put(TaxPropertyConstants.PayeeFields.ADDRESS_COUNTRY_CODE, getAddressCountryCode());
        retval.put(TaxPropertyConstants.PayeeFields.TAX_YEAR, getTaxYear().toString());
        return retval;
    }

    public KualiDecimal getTaxAmount(String type, Integer taxYear) {
        if (type == null) {
            return KualiDecimal.ZERO;
        }
        KualiDecimal taxAmount = KualiDecimal.ZERO;
        List<Payment> list = getPayments();
        if (list != null) {
            for (Payment payment : list) {
                if (!payment.getExcludeIndicator() && type.equals(payment.getPaymentTypeCode()) && taxYear.equals(payment.getPayee().getTaxYear())) {
                    taxAmount = taxAmount.add(payment.getAcctNetAmount());
                }
            }
        }

        return taxAmount;
    }

    public KualiDecimal getTaxAmount(Integer taxYear) {
        KualiDecimal taxAmount = KualiDecimal.ZERO;
        List<Payment> list = getPayments();
        if (list != null) {
            for (Payment payment : list) {
                if (!payment.getExcludeIndicator() && taxYear.equals(payment.getPayee().getTaxYear())) {
                    taxAmount = taxAmount.add(payment.getAcctNetAmount());
                }
            }
        }
        return taxAmount;
    }

    public KualiDecimal getTaxAmount() {
        KualiDecimal taxAmount = KualiDecimal.ZERO;
        List<Payment> list = getPayments();
        if (list != null) {
            for (Payment payment : list) {
                if (!payment.getExcludeIndicator()) {
                    taxAmount = taxAmount.add(payment.getAcctNetAmount());
                }
            }
        }
        return taxAmount;
    }
}
