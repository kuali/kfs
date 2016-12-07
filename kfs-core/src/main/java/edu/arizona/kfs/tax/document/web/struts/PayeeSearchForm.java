package edu.arizona.kfs.tax.document.web.struts;

import org.kuali.rice.kns.web.struts.form.KualiForm;

public class PayeeSearchForm extends KualiForm {

	private Integer taxYear;
	private String headerTaxNumber;
	private String vendorNumber;
	private String vendorName;

	public Integer getTaxYear() {
		return taxYear;
	}

	public void setTaxYear(Integer taxYear) {
		this.taxYear = taxYear;
	}

	public String getHeaderTaxNumber() {
		return headerTaxNumber;
	}

	public void setHeaderTaxNumber(String headerTaxNumber) {
		this.headerTaxNumber = headerTaxNumber;
	}

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String detailName) {
		this.vendorName = detailName;
	}

	public String getVendorNumber() {
		return vendorNumber;
	}

	public void setVendorNumber(String vendorNumber) {
		this.vendorNumber = vendorNumber;
	}

}
