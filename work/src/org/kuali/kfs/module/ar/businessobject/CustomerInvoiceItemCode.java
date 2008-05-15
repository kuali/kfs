package org.kuali.module.ar.bo;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.ObjectCodeCurrent;
import org.kuali.module.chart.bo.Org;
import org.kuali.module.chart.bo.ProjectCode;
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.module.chart.bo.SubObjCdCurrent;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class CustomerInvoiceItemCode extends PersistableBusinessObjectBase {

	private String chartOfAccountsCode;
	private String organizationCode;
	private String invoiceItemCode;
	private String invoiceItemDescription;
	private String relatedStockNumber;
	private String defaultInvoiceChartOfAccountsCode;
	private String defaultInvoiceAccountNumber;
	private String defaultInvoiceSubAccountNumber;
	private String defaultInvoiceFinancialObjectCode;
	private String defaultInvoiceFinancialSubObjectCode;
	private String defaultInvoiceProjectCode;
	private String defaultInvoiceOrganizationReferenceIdentifier;
	private KualiDecimal itemDefaultPrice;
	private String defaultUnitOfMeasureCode;
	private BigDecimal itemDefaultQuantity;
	private boolean active;

    private Chart chartOfAccounts;
	private Org organization;
	private Account defaultInvoiceAccount;
	private SubAccount defaultInvoiceSubAccount;
	private Chart defaultInvoiceChartOfAccounts;
    private ObjectCodeCurrent defaultInvoiceFinancialObject;
    private SubObjCdCurrent defaultInvoiceFinancialSubObject;
	private ProjectCode defaultInvoiceProject;

	/**
	 * Default constructor.
	 */
	public CustomerInvoiceItemCode() {

	}

	/**
	 * Gets the chartOfAccountsCode attribute.
	 * 
	 * @return Returns the chartOfAccountsCode
	 * 
	 */
	public String getChartOfAccountsCode() { 
		return chartOfAccountsCode;
	}

	/**
	 * Sets the chartOfAccountsCode attribute.
	 * 
	 * @param chartOfAccountsCode The chartOfAccountsCode to set.
	 * 
	 */
	public void setChartOfAccountsCode(String chartOfAccountsCode) {
		this.chartOfAccountsCode = chartOfAccountsCode;
	}


	/**
	 * Gets the organizationCode attribute.
	 * 
	 * @return Returns the organizationCode
	 * 
	 */
	public String getOrganizationCode() { 
		return organizationCode;
	}

	/**
	 * Sets the organizationCode attribute.
	 * 
	 * @param organizationCode The organizationCode to set.
	 * 
	 */
	public void setOrganizationCode(String organizationCode) {
		this.organizationCode = organizationCode;
	}


	/**
	 * Gets the invoiceItemCode attribute.
	 * 
	 * @return Returns the invoiceItemCode
	 * 
	 */
	public String getInvoiceItemCode() { 
		return invoiceItemCode;
	}

	/**
	 * Sets the invoiceItemCode attribute.
	 * 
	 * @param invoiceItemCode The invoiceItemCode to set.
	 * 
	 */
	public void setInvoiceItemCode(String invoiceItemCode) {
		this.invoiceItemCode = invoiceItemCode;
	}


	/**
	 * Gets the invoiceItemDescription attribute.
	 * 
	 * @return Returns the invoiceItemDescription
	 * 
	 */
	public String getInvoiceItemDescription() { 
		return invoiceItemDescription;
	}

	/**
	 * Sets the invoiceItemDescription attribute.
	 * 
	 * @param invoiceItemDescription The invoiceItemDescription to set.
	 * 
	 */
	public void setInvoiceItemDescription(String invoiceItemDescription) {
		this.invoiceItemDescription = invoiceItemDescription;
	}


	/**
	 * Gets the relatedStockNumber attribute.
	 * 
	 * @return Returns the relatedStockNumber
	 * 
	 */
	public String getRelatedStockNumber() { 
		return relatedStockNumber;
	}

	/**
	 * Sets the relatedStockNumber attribute.
	 * 
	 * @param relatedStockNumber The relatedStockNumber to set.
	 * 
	 */
	public void setRelatedStockNumber(String relatedStockNumber) {
		this.relatedStockNumber = relatedStockNumber;
	}


	/**
	 * Gets the defaultInvoiceChartOfAccountsCode attribute.
	 * 
	 * @return Returns the defaultInvoiceChartOfAccountsCode
	 * 
	 */
	public String getDefaultInvoiceChartOfAccountsCode() { 
		return defaultInvoiceChartOfAccountsCode;
	}

	/**
	 * Sets the defaultInvoiceChartOfAccountsCode attribute.
	 * 
	 * @param defaultInvoiceChartOfAccountsCode The defaultInvoiceChartOfAccountsCode to set.
	 * 
	 */
	public void setDefaultInvoiceChartOfAccountsCode(String defaultInvoiceChartOfAccountsCode) {
		this.defaultInvoiceChartOfAccountsCode = defaultInvoiceChartOfAccountsCode;
	}


	/**
	 * Gets the defaultInvoiceAccountNumber attribute.
	 * 
	 * @return Returns the defaultInvoiceAccountNumber
	 * 
	 */
	public String getDefaultInvoiceAccountNumber() { 
		return defaultInvoiceAccountNumber;
	}

	/**
	 * Sets the defaultInvoiceAccountNumber attribute.
	 * 
	 * @param defaultInvoiceAccountNumber The defaultInvoiceAccountNumber to set.
	 * 
	 */
	public void setDefaultInvoiceAccountNumber(String defaultInvoiceAccountNumber) {
		this.defaultInvoiceAccountNumber = defaultInvoiceAccountNumber;
	}


	/**
	 * Gets the defaultInvoiceSubAccountNumber attribute.
	 * 
	 * @return Returns the defaultInvoiceSubAccountNumber
	 * 
	 */
	public String getDefaultInvoiceSubAccountNumber() { 
		return defaultInvoiceSubAccountNumber;
	}

	/**
	 * Sets the defaultInvoiceSubAccountNumber attribute.
	 * 
	 * @param defaultInvoiceSubAccountNumber The defaultInvoiceSubAccountNumber to set.
	 * 
	 */
	public void setDefaultInvoiceSubAccountNumber(String defaultInvoiceSubAccountNumber) {
		this.defaultInvoiceSubAccountNumber = defaultInvoiceSubAccountNumber;
	}


	/**
	 * Gets the defaultInvoiceFinancialObjectCode attribute.
	 * 
	 * @return Returns the defaultInvoiceFinancialObjectCode
	 * 
	 */
	public String getDefaultInvoiceFinancialObjectCode() { 
		return defaultInvoiceFinancialObjectCode;
	}

	/**
	 * Sets the defaultInvoiceFinancialObjectCode attribute.
	 * 
	 * @param defaultInvoiceFinancialObjectCode The defaultInvoiceFinancialObjectCode to set.
	 * 
	 */
	public void setDefaultInvoiceFinancialObjectCode(String defaultInvoiceFinancialObjectCode) {
		this.defaultInvoiceFinancialObjectCode = defaultInvoiceFinancialObjectCode;
	}


	/**
	 * Gets the defaultInvoiceFinancialSubObjectCode attribute.
	 * 
	 * @return Returns the defaultInvoiceFinancialSubObjectCode
	 * 
	 */
	public String getDefaultInvoiceFinancialSubObjectCode() { 
		return defaultInvoiceFinancialSubObjectCode;
	}

	/**
	 * Sets the defaultInvoiceFinancialSubObjectCode attribute.
	 * 
	 * @param defaultInvoiceFinancialSubObjectCode The defaultInvoiceFinancialSubObjectCode to set.
	 * 
	 */
	public void setDefaultInvoiceFinancialSubObjectCode(String defaultInvoiceFinancialSubObjectCode) {
		this.defaultInvoiceFinancialSubObjectCode = defaultInvoiceFinancialSubObjectCode;
	}


	/**
	 * Gets the defaultInvoiceProjectCode attribute.
	 * 
	 * @return Returns the defaultInvoiceProjectCode
	 * 
	 */
	public String getDefaultInvoiceProjectCode() { 
		return defaultInvoiceProjectCode;
	}

	/**
	 * Sets the defaultInvoiceProjectCode attribute.
	 * 
	 * @param defaultInvoiceProjectCode The defaultInvoiceProjectCode to set.
	 * 
	 */
	public void setDefaultInvoiceProjectCode(String defaultInvoiceProjectCode) {
		this.defaultInvoiceProjectCode = defaultInvoiceProjectCode;
	}


	/**
	 * Gets the defaultInvoiceOrganizationReferenceIdentifier attribute.
	 * 
	 * @return Returns the defaultInvoiceOrganizationReferenceIdentifier
	 * 
	 */
	public String getDefaultInvoiceOrganizationReferenceIdentifier() { 
		return defaultInvoiceOrganizationReferenceIdentifier;
	}

	/**
	 * Sets the defaultInvoiceOrganizationReferenceIdentifier attribute.
	 * 
	 * @param defaultInvoiceOrganizationReferenceIdentifier The defaultInvoiceOrganizationReferenceIdentifier to set.
	 * 
	 */
	public void setDefaultInvoiceOrganizationReferenceIdentifier(String defaultInvoiceOrganizationReferenceIdentifier) {
		this.defaultInvoiceOrganizationReferenceIdentifier = defaultInvoiceOrganizationReferenceIdentifier;
	}


	/**
	 * Gets the itemDefaultPrice attribute.
	 * 
	 * @return Returns the itemDefaultPrice
	 * 
	 */
	public KualiDecimal getItemDefaultPrice() { 
		return itemDefaultPrice;
	}

	/**
	 * Sets the itemDefaultPrice attribute.
	 * 
	 * @param itemDefaultPrice The itemDefaultPrice to set.
	 * 
	 */
	public void setItemDefaultPrice(KualiDecimal itemDefaultPrice) {
		this.itemDefaultPrice = itemDefaultPrice;
	}


	/**
	 * Gets the defaultUnitOfMeasureCode attribute.
	 * 
	 * @return Returns the defaultUnitOfMeasureCode
	 * 
	 */
	public String getDefaultUnitOfMeasureCode() { 
		return defaultUnitOfMeasureCode;
	}

	/**
	 * Sets the defaultUnitOfMeasureCode attribute.
	 * 
	 * @param defaultUnitOfMeasureCode The defaultUnitOfMeasureCode to set.
	 * 
	 */
	public void setDefaultUnitOfMeasureCode(String defaultUnitOfMeasureCode) {
		this.defaultUnitOfMeasureCode = defaultUnitOfMeasureCode;
	}


	/**
	 * Gets the itemDefaultQuantity attribute.
	 * 
	 * @return Returns the itemDefaultQuantity
	 * 
	 */
	public BigDecimal getItemDefaultQuantity() { 
		return itemDefaultQuantity;
	}

	/**
	 * Sets the itemDefaultQuantity attribute.
	 * 
	 * @param itemDefaultQuantity The itemDefaultQuantity to set.
	 * 
	 */
	public void setItemDefaultQuantity(BigDecimal itemDefaultQuantity) {
		this.itemDefaultQuantity = itemDefaultQuantity;
	}


	/**
	 * Gets the active attribute.
	 * 
	 * @return Returns the active
	 * 
	 */
	public boolean isActive() { 
		return active;
	}

	/**
	 * Sets the active attribute.
	 * 
	 * @param active The active to set.
	 * 
	 */
	public void setActive(boolean active) {
		this.active = active;
	}


	/**
	 * Gets the chartOfAccounts attribute.
	 * 
	 * @return Returns the chartOfAccounts
	 * 
	 */
	public Chart getChartOfAccounts() { 
		return chartOfAccounts;
	}

	/**
	 * Sets the chartOfAccounts attribute.
	 * 
	 * @param chartOfAccounts The chartOfAccounts to set.
	 * @deprecated
	 */
	public void setChartOfAccounts(Chart chartOfAccounts) {
		this.chartOfAccounts = chartOfAccounts;
	}

	/**
	 * Gets the organization attribute.
	 * 
	 * @return Returns the organization
	 * 
	 */
	public Org getOrganization() { 
		return organization;
	}

	/**
	 * Sets the organization attribute.
	 * 
	 * @param organization The organization to set.
	 * @deprecated
	 */
	public void setOrganization(Org organization) {
		this.organization = organization;
	}

	/**
	 * Gets the defaultInvoiceAccount attribute.
	 * 
	 * @return Returns the defaultInvoiceAccount
	 * 
	 */
	public Account getDefaultInvoiceAccount() { 
		return defaultInvoiceAccount;
	}

	/**
	 * Sets the defaultInvoiceAccount attribute.
	 * 
	 * @param defaultInvoiceAccount The defaultInvoiceAccount to set.
	 * @deprecated
	 */
	public void setDefaultInvoiceAccount(Account defaultInvoiceAccount) {
		this.defaultInvoiceAccount = defaultInvoiceAccount;
	}

	/**
	 * Gets the defaultInvoiceSubAccount attribute.
	 * 
	 * @return Returns the defaultInvoiceSubAccount
	 * 
	 */
	public SubAccount getDefaultInvoiceSubAccount() { 
		return defaultInvoiceSubAccount;
	}

	/**
	 * Sets the defaultInvoiceSubAccount attribute.
	 * 
	 * @param defaultInvoiceSubAccount The defaultInvoiceSubAccount to set.
	 * @deprecated
	 */
	public void setDefaultInvoiceSubAccount(SubAccount defaultInvoiceSubAccount) {
		this.defaultInvoiceSubAccount = defaultInvoiceSubAccount;
	}

	/**
	 * Gets the defaultInvoiceChartOfAccounts attribute.
	 * 
	 * @return Returns the defaultInvoiceChartOfAccounts
	 * 
	 */
	public Chart getDefaultInvoiceChartOfAccounts() { 
		return defaultInvoiceChartOfAccounts;
	}

	/**
	 * Sets the defaultInvoiceChartOfAccounts attribute.
	 * 
	 * @param defaultInvoiceChartOfAccounts The defaultInvoiceChartOfAccounts to set.
	 * @deprecated
	 */
	public void setDefaultInvoiceChartOfAccounts(Chart defaultInvoiceChartOfAccounts) {
		this.defaultInvoiceChartOfAccounts = defaultInvoiceChartOfAccounts;
	}

    /**
     * Gets the defaultInvoiceFinancialObject attribute. 
     * @return Returns the defaultInvoiceFinancialObject.
     */
    public ObjectCodeCurrent getDefaultInvoiceFinancialObject() {
        return defaultInvoiceFinancialObject;
    }

    /**
     * Sets the defaultInvoiceFinancialObject attribute value.
     * @param defaultInvoiceFinancialObject The defaultInvoiceFinancialObject to set.
     * @deprecated
     */
    public void setDefaultInvoiceFinancialObject(ObjectCodeCurrent defaultInvoiceFinancialObject) {
        this.defaultInvoiceFinancialObject = defaultInvoiceFinancialObject;
    }    
    
	/**
	 * Gets the defaultInvoiceFinancialSubObject attribute.
	 * 
	 * @return Returns the defaultInvoiceFinancialSubObject
	 * 
	 */
	public SubObjCdCurrent getDefaultInvoiceFinancialSubObject() { 
		return defaultInvoiceFinancialSubObject;
	}

	/**
	 * Sets the defaultInvoiceFinancialSubObject attribute.
	 * 
	 * @param defaultInvoiceFinancialSubObject The defaultInvoiceFinancialSubObject to set.
	 * @deprecated
	 */
	public void setDefaultInvoiceFinancialSubObject(SubObjCdCurrent defaultInvoiceFinancialSubObject) {
		this.defaultInvoiceFinancialSubObject = defaultInvoiceFinancialSubObject;
	}

	/**
	 * Gets the defaultInvoiceProject attribute.
	 * 
	 * @return Returns the defaultInvoiceProject
	 * 
	 */
	public ProjectCode getDefaultInvoiceProject() { 
		return defaultInvoiceProject;
	}

	/**
	 * Sets the defaultInvoiceProject attribute.
	 * 
	 * @param defaultInvoiceProject The defaultInvoiceProject to set.
	 * @deprecated
	 */
	public void setDefaultInvoiceProject(ProjectCode defaultInvoiceProject) {
		this.defaultInvoiceProject = defaultInvoiceProject;
	}

	/**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("organizationCode", this.organizationCode);
        m.put("invoiceItemCode", this.invoiceItemCode);
	    return m;
    }

}
