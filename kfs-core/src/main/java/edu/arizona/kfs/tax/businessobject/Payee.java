package edu.arizona.kfs.tax.businessobject;

import java.io.Serializable;
import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class Payee extends PersistableBusinessObjectBase implements Serializable {
	private static final long serialVersionUID = 1L;
	
	protected Integer id; // PAYEE_ID
	protected String headerTypeCode; // VNDR_TYP_CD
	protected String headerTaxNumber; // VNDR_TAX_NBR
	protected String headerOwnershipCode; // VNDR_OWNR_CD
	protected String headerOwnershipCategoryCode; // VNDR_OWNR_CTGRY_CD
	protected String vendorName; // DTL_VNDR_NM
	protected String vendorNumber; // VNDR_NUM
	protected String addressTypeCode; // VNDR_ADDR_TYPE_CD
	protected String addressLine1Address; // VNDR_LN1_ADDR
	protected String addressLine2Address; // VNDR_LN2_ADDR
	protected String addressCityName; // VNDR_CTY_NM
	protected String addressStateCode; // VNDR_ST_CD
	protected String addressZipCode; // VNDR_ZIP_CD
	protected String addressCountryCode; // VNDR_CNTRY_CD
	protected Integer taxYear; // TAX_YEAR
	protected Boolean vendorForeignIndicator; // VNDR_FRGN_IND
	protected Boolean correctionIndicator; // CORRECTION_IND
	private Boolean excludeIndicator; // EXCLUDE_IND
	private Date vendorFederalWithholdingTaxBeginningDate; // VNDR_FWT_BEG_DT
	private Date vendorFederalWithholdingTaxEndDate; // VNDR_FWT_END_DT
	private Integer vendorHeaderGeneratedIdentifier; // VNDR_HDR_GNRTD_ID
	private Integer vendorDetailAssignedIdentifier; // VNDR_DTL_ASND_ID

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

	protected LinkedHashMap<Object, Object> toStringMapper() {
		LinkedHashMap<Object, Object> map = new LinkedHashMap<Object, Object>();

		map.put("id", getId());
		map.put("headerTypeCode", getHeaderTypeCode());
		map.put("headerTaxNumber", getHeaderTaxNumber());
		map.put("headerOwnershipCode", getHeaderOwnershipCode());
		map.put("headerOwnershipCategoryCode", getHeaderOwnershipCategoryCode());
		map.put("vendorName", getVendorName());
		map.put("addressTypeCode", getAddressTypeCode());
		map.put("addressLine1Address", getAddressLine1Address());
		map.put("addressLine2Address", getAddressLine2Address());
		map.put("addressCityName", getAddressCityName());
		map.put("addressStateCode", getAddressStateCode());
		map.put("addressZipCode", getAddressZipCode());
		map.put("addressCountryCode", getAddressCountryCode());
		map.put("taxYear", getTaxYear());

		return map;
	}
}
