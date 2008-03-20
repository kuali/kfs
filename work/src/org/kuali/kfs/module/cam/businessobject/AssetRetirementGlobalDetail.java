package org.kuali.module.cams.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.bo.Country;
import org.kuali.kfs.bo.State;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */

public class AssetRetirementGlobalDetail extends PersistableBusinessObjectBase {

	private String documentNumber;
	private Long capitalAssetNumber;
	private String retirementChartOfAccountsCode;
	private String retirementAccountNumber;
	private String retirementContactName;
	private String retirementInstitutionName;
	private String retirementStreetAddress;
	private String retirementCityName;
	private String retirementStateCode;
	private String retirementZipCode;
	private String retirementCountryCode;
	private String retirementPhoneNumber;
	private KualiDecimal estimatedSellingPrice;
	private KualiDecimal salePrice;
	private String cashReceiptFinancialDocumentNumber;
	private KualiDecimal handlingFeeAmount;
	private KualiDecimal preventiveMaintenanceAmount;
	private String buyerDescription;
	private String paidCaseNumber;

    private DocumentHeader document;
    private Asset asset;
    private Account retirementAccount;
    private Chart retirementChartOfAccounts;
    private DocumentHeader cashReceiptFinancialDocument;
    private State retirementState;
    private Country retirementCountry;
    
	/**
	 * Default constructor.
	 */
	public AssetRetirementGlobalDetail() {

	}

	/**
	 * Gets the documentNumber attribute.
	 * 
	 * @return Returns the documentNumber
	 * 
	 */
	public String getDocumentNumber() { 
		return documentNumber;
	}

	/**
	 * Sets the documentNumber attribute.
	 * 
	 * @param documentNumber The documentNumber to set.
	 * 
	 */
	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}


	/**
	 * Gets the capitalAssetNumber attribute.
	 * 
	 * @return Returns the capitalAssetNumber
	 * 
	 */
	public Long getCapitalAssetNumber() { 
		return capitalAssetNumber;
	}

	/**
	 * Sets the capitalAssetNumber attribute.
	 * 
	 * @param capitalAssetNumber The capitalAssetNumber to set.
	 * 
	 */
	public void setCapitalAssetNumber(Long capitalAssetNumber) {
		this.capitalAssetNumber = capitalAssetNumber;
	}


	/**
	 * Gets the retirementChartOfAccountsCode attribute.
	 * 
	 * @return Returns the retirementChartOfAccountsCode
	 * 
	 */
	public String getRetirementChartOfAccountsCode() { 
		return retirementChartOfAccountsCode;
	}

	/**
	 * Sets the retirementChartOfAccountsCode attribute.
	 * 
	 * @param retirementChartOfAccountsCode The retirementChartOfAccountsCode to set.
	 * 
	 */
	public void setRetirementChartOfAccountsCode(String retirementChartOfAccountsCode) {
		this.retirementChartOfAccountsCode = retirementChartOfAccountsCode;
	}


	/**
	 * Gets the retirementAccountNumber attribute.
	 * 
	 * @return Returns the retirementAccountNumber
	 * 
	 */
	public String getRetirementAccountNumber() { 
		return retirementAccountNumber;
	}

	/**
	 * Sets the retirementAccountNumber attribute.
	 * 
	 * @param retirementAccountNumber The retirementAccountNumber to set.
	 * 
	 */
	public void setRetirementAccountNumber(String retirementAccountNumber) {
		this.retirementAccountNumber = retirementAccountNumber;
	}


	/**
	 * Gets the retirementContactName attribute.
	 * 
	 * @return Returns the retirementContactName
	 * 
	 */
	public String getRetirementContactName() { 
		return retirementContactName;
	}

	/**
	 * Sets the retirementContactName attribute.
	 * 
	 * @param retirementContactName The retirementContactName to set.
	 * 
	 */
	public void setRetirementContactName(String retirementContactName) {
		this.retirementContactName = retirementContactName;
	}


	/**
	 * Gets the retirementInstitutionName attribute.
	 * 
	 * @return Returns the retirementInstitutionName
	 * 
	 */
	public String getRetirementInstitutionName() { 
		return retirementInstitutionName;
	}

	/**
	 * Sets the retirementInstitutionName attribute.
	 * 
	 * @param retirementInstitutionName The retirementInstitutionName to set.
	 * 
	 */
	public void setRetirementInstitutionName(String retirementInstitutionName) {
		this.retirementInstitutionName = retirementInstitutionName;
	}


	/**
	 * Gets the retirementStreetAddress attribute.
	 * 
	 * @return Returns the retirementStreetAddress
	 * 
	 */
	public String getRetirementStreetAddress() { 
		return retirementStreetAddress;
	}

	/**
	 * Sets the retirementStreetAddress attribute.
	 * 
	 * @param retirementStreetAddress The retirementStreetAddress to set.
	 * 
	 */
	public void setRetirementStreetAddress(String retirementStreetAddress) {
		this.retirementStreetAddress = retirementStreetAddress;
	}


	/**
	 * Gets the retirementCityName attribute.
	 * 
	 * @return Returns the retirementCityName
	 * 
	 */
	public String getRetirementCityName() { 
		return retirementCityName;
	}

	/**
	 * Sets the retirementCityName attribute.
	 * 
	 * @param retirementCityName The retirementCityName to set.
	 * 
	 */
	public void setRetirementCityName(String retirementCityName) {
		this.retirementCityName = retirementCityName;
	}


	/**
	 * Gets the retirementStateCode attribute.
	 * 
	 * @return Returns the retirementStateCode
	 * 
	 */
	public String getRetirementStateCode() { 
		return retirementStateCode;
	}

	/**
	 * Sets the retirementStateCode attribute.
	 * 
	 * @param retirementStateCode The retirementStateCode to set.
	 * 
	 */
	public void setRetirementStateCode(String retirementStateCode) {
		this.retirementStateCode = retirementStateCode;
	}


	/**
	 * Gets the retirementZipCode attribute.
	 * 
	 * @return Returns the retirementZipCode
	 * 
	 */
	public String getRetirementZipCode() { 
		return retirementZipCode;
	}

	/**
	 * Sets the retirementZipCode attribute.
	 * 
	 * @param retirementZipCode The retirementZipCode to set.
	 * 
	 */
	public void setRetirementZipCode(String retirementZipCode) {
		this.retirementZipCode = retirementZipCode;
	}


	/**
	 * Gets the retirementCountryCode attribute.
	 * 
	 * @return Returns the retirementCountryCode
	 * 
	 */
	public String getRetirementCountryCode() { 
		return retirementCountryCode;
	}

	/**
	 * Sets the retirementCountryCode attribute.
	 * 
	 * @param retirementCountryCode The retirementCountryCode to set.
	 * 
	 */
	public void setRetirementCountryCode(String retirementCountryCode) {
		this.retirementCountryCode = retirementCountryCode;
	}


	/**
	 * Gets the retirementPhoneNumber attribute.
	 * 
	 * @return Returns the retirementPhoneNumber
	 * 
	 */
	public String getRetirementPhoneNumber() { 
		return retirementPhoneNumber;
	}

	/**
	 * Sets the retirementPhoneNumber attribute.
	 * 
	 * @param retirementPhoneNumber The retirementPhoneNumber to set.
	 * 
	 */
	public void setRetirementPhoneNumber(String retirementPhoneNumber) {
		this.retirementPhoneNumber = retirementPhoneNumber;
	}


	/**
	 * Gets the estimatedSellingPrice attribute.
	 * 
	 * @return Returns the estimatedSellingPrice
	 * 
	 */
	public KualiDecimal getEstimatedSellingPrice() { 
		return estimatedSellingPrice;
	}

	/**
	 * Sets the estimatedSellingPrice attribute.
	 * 
	 * @param estimatedSellingPrice The estimatedSellingPrice to set.
	 * 
	 */
	public void setEstimatedSellingPrice(KualiDecimal estimatedSellingPrice) {
		this.estimatedSellingPrice = estimatedSellingPrice;
	}


	/**
	 * Gets the salePrice attribute.
	 * 
	 * @return Returns the salePrice
	 * 
	 */
	public KualiDecimal getSalePrice() { 
		return salePrice;
	}

	/**
	 * Sets the salePrice attribute.
	 * 
	 * @param salePrice The salePrice to set.
	 * 
	 */
	public void setSalePrice(KualiDecimal salePrice) {
		this.salePrice = salePrice;
	}


	/**
	 * Gets the cashReceiptFinancialDocumentNumber attribute.
	 * 
	 * @return Returns the cashReceiptFinancialDocumentNumber
	 * 
	 */
	public String getCashReceiptFinancialDocumentNumber() { 
		return cashReceiptFinancialDocumentNumber;
	}

	/**
	 * Sets the cashReceiptFinancialDocumentNumber attribute.
	 * 
	 * @param cashReceiptFinancialDocumentNumber The cashReceiptFinancialDocumentNumber to set.
	 * 
	 */
	public void setCashReceiptFinancialDocumentNumber(String cashReceiptFinancialDocumentNumber) {
		this.cashReceiptFinancialDocumentNumber = cashReceiptFinancialDocumentNumber;
	}


	/**
	 * Gets the handlingFeeAmount attribute.
	 * 
	 * @return Returns the handlingFeeAmount
	 * 
	 */
	public KualiDecimal getHandlingFeeAmount() { 
		return handlingFeeAmount;
	}

	/**
	 * Sets the handlingFeeAmount attribute.
	 * 
	 * @param handlingFeeAmount The handlingFeeAmount to set.
	 * 
	 */
	public void setHandlingFeeAmount(KualiDecimal handlingFeeAmount) {
		this.handlingFeeAmount = handlingFeeAmount;
	}


	/**
	 * Gets the preventiveMaintenanceAmount attribute.
	 * 
	 * @return Returns the preventiveMaintenanceAmount
	 * 
	 */
	public KualiDecimal getPreventiveMaintenanceAmount() { 
		return preventiveMaintenanceAmount;
	}

	/**
	 * Sets the preventiveMaintenanceAmount attribute.
	 * 
	 * @param preventiveMaintenanceAmount The preventiveMaintenanceAmount to set.
	 * 
	 */
	public void setPreventiveMaintenanceAmount(KualiDecimal preventiveMaintenanceAmount) {
		this.preventiveMaintenanceAmount = preventiveMaintenanceAmount;
	}


	/**
	 * Gets the buyerDescription attribute.
	 * 
	 * @return Returns the buyerDescription
	 * 
	 */
	public String getBuyerDescription() { 
		return buyerDescription;
	}

	/**
	 * Sets the buyerDescription attribute.
	 * 
	 * @param buyerDescription The buyerDescription to set.
	 * 
	 */
	public void setBuyerDescription(String buyerDescription) {
		this.buyerDescription = buyerDescription;
	}


	/**
	 * Gets the paidCaseNumber attribute.
	 * 
	 * @return Returns the paidCaseNumber
	 * 
	 */
	public String getPaidCaseNumber() { 
		return paidCaseNumber;
	}

	/**
	 * Sets the paidCaseNumber attribute.
	 * 
	 * @param paidCaseNumber The paidCaseNumber to set.
	 * 
	 */
	public void setPaidCaseNumber(String paidCaseNumber) {
		this.paidCaseNumber = paidCaseNumber;
	}

	/**
	 * Gets the retirementChartOfAccounts attribute.
	 * 
	 * @return Returns the retirementChartOfAccounts
	 * 
	 */
	public Chart getRetirementChartOfAccounts() { 
		return retirementChartOfAccounts;
	}

	/**
	 * Sets the retirementChartOfAccounts attribute.
	 * 
	 * @param retirementChartOfAccounts The retirementChartOfAccounts to set.
	 * @deprecated
	 */
	public void setRetirementChartOfAccounts(Chart retirementChartOfAccounts) {
		this.retirementChartOfAccounts = retirementChartOfAccounts;
	}

	/**
	 * Gets the retirementAccount attribute.
	 * 
	 * @return Returns the retirementAccount
	 * 
	 */
	public Account getRetirementAccount() { 
		return retirementAccount;
	}

	/**
	 * Sets the retirementAccount attribute.
	 * 
	 * @param retirementAccount The retirementAccount to set.
	 * @deprecated
	 */
	public void setRetirementAccount(Account retirementAccount) {
		this.retirementAccount = retirementAccount;
	}

	/**
     * Gets the asset attribute. 
     * @return Returns the asset.
     */
    public Asset getAsset() {
        return asset;
    }

    /**
     * Sets the asset attribute value.
     * @param asset The asset to set.
     * @deprecated
     */
    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    /**
     * Gets the cashReceiptFinancialDocument attribute. 
     * @return Returns the cashReceiptFinancialDocument.
     */
    public DocumentHeader getCashReceiptFinancialDocument() {
        return cashReceiptFinancialDocument;
    }

    /**
     * Sets the cashReceiptFinancialDocument attribute value.
     * @param cashReceiptFinancialDocument The cashReceiptFinancialDocument to set.
     * @deprecated
     */
    public void setCashReceiptFinancialDocument(DocumentHeader cashReceiptFinancialDocument) {
        this.cashReceiptFinancialDocument = cashReceiptFinancialDocument;
    }

    /**
     * Gets the document attribute. 
     * @return Returns the document.
     */
    public DocumentHeader getDocument() {
        return document;
    }

    /**
     * Sets the document attribute value.
     * @param document The document to set.
     * @deprecated
     */
    public void setDocument(DocumentHeader document) {
        this.document = document;
    }

    /**
     * Gets the retirementCountry attribute. 
     * @return Returns the retirementCountry.
     */
    public Country getRetirementCountry() {
        return retirementCountry;
    }

    /**
     * Sets the retirementCountry attribute value.
     * @param retirementCountry The retirementCountry to set.
     * @deprecated
     */
    public void setRetirementCountry(Country retirementCountry) {
        this.retirementCountry = retirementCountry;
    }

    /**
     * Gets the retirementState attribute. 
     * @return Returns the retirementState.
     */
    public State getRetirementState() {
        return retirementState;
    }

    /**
     * Sets the retirementState attribute value.
     * @param retirementState The retirementState to set.
     * @deprecated
     */
    public void setRetirementState(State retirementState) {
        this.retirementState = retirementState;
    }

    /**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("documentNumber", this.documentNumber);
        if (this.capitalAssetNumber != null) {
            m.put("capitalAssetNumber", this.capitalAssetNumber.toString());
        }
	    return m;
    }
}
