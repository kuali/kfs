package edu.arizona.kfs.sys.identity;

import org.kuali.kfs.coa.businessobject.FundGroup;
import org.kuali.kfs.coa.businessobject.ObjectSubType;

public class KfsKimAttributes extends org.kuali.kfs.sys.identity.KfsKimAttributes {

	public static final String OBJECT_SUB_TYPE_CODE = "financialObjectSubTypeCode";
	public static final String FUND_GROUP_CODE = "fundGroupCode";
	
	protected String financialObjectSubTypeCode;
	protected String fundGroupCode;
	
	protected ObjectSubType objectSubType;
	protected FundGroup fundGroup;

	public String getFinancialObjectSubTypeCode() {
		return financialObjectSubTypeCode;
	}

	public void setFinancialObjectSubTypeCode(String financialObjectSubTypeCode) {
		this.financialObjectSubTypeCode = financialObjectSubTypeCode;
	}

	public ObjectSubType getObjectSubType() {
		return objectSubType;
	}

	public void setObjectSubType(ObjectSubType objectSubType) {
		this.objectSubType = objectSubType;
	}

	public String getFundGroupCode() {
		return fundGroupCode;
	}

	public void setFundGroupCode(String fundGroupCode) {
		this.fundGroupCode = fundGroupCode;
	}

	public FundGroup getFundGroup() {
		return fundGroup;
	}

	public void setFundGroup(FundGroup fundGroup) {
		this.fundGroup = fundGroup;
	}
}
