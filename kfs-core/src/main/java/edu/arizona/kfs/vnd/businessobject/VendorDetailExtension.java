package edu.arizona.kfs.vnd.businessobject;

import org.kuali.rice.krad.bo.PersistableBusinessObjectExtensionBase;

public class VendorDetailExtension extends PersistableBusinessObjectExtensionBase {

	private String conflictOfInterest;
	private Integer vendorDetailAssignedIdentifier;
	private Integer vendorHeaderGeneratedIdentifier;
	private boolean exportControlsFlag;
	private String azSalesTaxLicense;
	private String tucSalesTaxLicense;
	private String defaultB2BPaymentMethodCode;

	public String getConflictOfInterest() {
		return conflictOfInterest;
	}

	public void setConflictOfInterest(String conflictOfInterest) {
		this.conflictOfInterest = conflictOfInterest;
	}
	
	public Integer getVendorDetailAssignedIdentifier() {
		return vendorDetailAssignedIdentifier;
	}

	public void setVendorDetailAssignedIdentifier(
			Integer vendorDetailAssignedIdentifier) {
		this.vendorDetailAssignedIdentifier = vendorDetailAssignedIdentifier;
	}

	public Integer getVendorHeaderGeneratedIdentifier() {
		return vendorHeaderGeneratedIdentifier;
	}

	public void setVendorHeaderGeneratedIdentifier(
			Integer vendorHeaderGeneratedIdentifier) {
		this.vendorHeaderGeneratedIdentifier = vendorHeaderGeneratedIdentifier;
	}

	public boolean isExportControlsFlag() {
		return exportControlsFlag;
	}

	public void setExportControlsFlag(boolean exportControlsFlag) {
		this.exportControlsFlag = exportControlsFlag;
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

	public String getDefaultB2BPaymentMethodCode() {
		return defaultB2BPaymentMethodCode;
	}

	public void setDefaultB2BPaymentMethodCode(String defaultB2BPaymentMethodCode) {
		this.defaultB2BPaymentMethodCode = defaultB2BPaymentMethodCode;
	}
	
}
