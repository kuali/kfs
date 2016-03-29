package edu.arizona.kfs.vnd.businessobject;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.log4j.Logger;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.bo.PersistableBusinessObjectExtensionBase;

/**
 * UAF-66 / MOD-PA7000-02 US Export Compliance
 *
 * @author Adam Kost <kosta@email.arizona.edu>
 */

public class VendorDetailExtension extends PersistableBusinessObjectExtensionBase {

    @SuppressWarnings("unused")
    private static Logger LOG = Logger.getLogger(VendorDetailExtension.class);
    private Integer vendorDetailAssignedIdentifier;
    private Integer vendorHeaderGeneratedIdentifier;
    private boolean exportControlsFlag;

    // Unused Database fields
    private String conflictOfInterest;
    private String azSalesTaxLicense;
    private String tucSalesTaxLicense;
    private String defaultB2BPaymentMethodCode;

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

    public String getConflictOfInterest() {
        return conflictOfInterest;
    }

    public void setConflictOfInterest(String conflictOfInterest) {
        this.conflictOfInterest = conflictOfInterest;
    }

    public String getAzSalesTaxLicense() {
        return azSalesTaxLicense;
    }

    public void setAzSalesTaxLicense(String azSalesTaxLicense) {
        this.azSalesTaxLicense = azSalesTaxLicense;
    }

    public String getTucSalesTaxLicense() {
        return tucSalesTaxLicense;
    }

    public void setTucSalesTaxLicense(String tucSalesTaxLicense) {
        this.tucSalesTaxLicense = tucSalesTaxLicense;
    }

    public boolean isExportControlsFlag() {
        return exportControlsFlag;
    }

    public void setExportControlsFlag(boolean exportControlsFlag) {
        this.exportControlsFlag = exportControlsFlag;
    }

    public String getDefaultB2BPaymentMethodCode() {
        return defaultB2BPaymentMethodCode;
    }

    public void setDefaultB2BPaymentMethodCode(String defaultB2BPaymentMethodCode) {
        this.defaultB2BPaymentMethodCode = defaultB2BPaymentMethodCode;
    }
    
    public boolean isEqualForRouting(Object toCompare) {
        if ((ObjectUtils.isNull(toCompare)) || !(toCompare instanceof VendorDetailExtension)) {
            return false;
        } else {
            VendorDetailExtension extension = (VendorDetailExtension) toCompare;
            return new EqualsBuilder()
                    .append(getConflictOfInterest(), extension.getConflictOfInterest())
                    .append(getAzSalesTaxLicense(), extension.getAzSalesTaxLicense())
                    .append(getTucSalesTaxLicense(), extension.getTucSalesTaxLicense())
                    .append(isExportControlsFlag(), extension.isExportControlsFlag())
                    .append(getDefaultB2BPaymentMethodCode(), extension.getDefaultB2BPaymentMethodCode())
                    .isEquals();
        }        
    }
}