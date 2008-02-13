package org.kuali.module.cams.bo;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.service.UniversalUserService;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.Org;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class Pretag extends PersistableBusinessObjectBase {

	private String purchaseOrderNumber;
	private Long lineItemNumber;
	private BigDecimal quantityInvoiced;
	private String capitalAssetTypeCode;
	private String manufacturerName;
	private String manufacturerModelNumber;
	private String vendorName;
	private String assetTopsDescription;
	private String organizationDescription;
	private String organizationText;
	private String organizationInventoryName;
	private String representativeUniversalIdentifier;
	private String chartOfAccountsCode;
	private Date pretagCreateDate;
	private String organizationCode;

	private Chart chartOfAccounts;
	private Org organization;
    private PendingPurchaseOrder purchaseOrder;
    private AssetType capitalAssetType;
    private UniversalUser personUniversal;
    
    private List<PretagDetail> pretagDetails;
    
  
	/**
	 * Default constructor.
	 */
	public Pretag() {
        pretagDetails = new ArrayList<PretagDetail>();
        
	}

	/**
	 * Gets the purchaseOrderNumber attribute.
	 * 
	 * @return Returns the purchaseOrderNumber
	 * 
	 */
	public String getPurchaseOrderNumber() { 
		return purchaseOrderNumber;
	}

	/**
	 * Sets the purchaseOrderNumber attribute.
	 * 
	 * @param purchaseOrderNumber The purchaseOrderNumber to set.
	 * 
	 */
	public void setPurchaseOrderNumber(String purchaseOrderNumber) {
		this.purchaseOrderNumber = purchaseOrderNumber;
	}


	/**
	 * Gets the lineItemNumber attribute.
	 * 
	 * @return Returns the lineItemNumber
	 * 
	 */
	public Long getLineItemNumber() { 
		return lineItemNumber;
	}

	/**
	 * Sets the lineItemNumber attribute.
	 * 
	 * @param lineItemNumber The lineItemNumber to set.
	 * 
	 */
	public void setLineItemNumber(Long lineItemNumber) {
		this.lineItemNumber = lineItemNumber;
	}


	/**
	 * Gets the quantityInvoiced attribute.
	 * 
	 * @return Returns the quantityInvoiced
	 * 
	 */
	public BigDecimal getQuantityInvoiced() { 
		return quantityInvoiced;
	}

	/**
	 * Sets the quantityInvoiced attribute.
	 * 
	 * @param quantityInvoiced The quantityInvoiced to set.
	 * 
	 */
	public void setQuantityInvoiced(BigDecimal quantityInvoiced) {
		this.quantityInvoiced = quantityInvoiced;
	}


	/**
	 * Gets the capitalAssetTypeCode attribute.
	 * 
	 * @return Returns the capitalAssetTypeCode
	 * 
	 */
	public String getCapitalAssetTypeCode() { 
		return capitalAssetTypeCode;
	}

	/**
	 * Sets the capitalAssetTypeCode attribute.
	 * 
	 * @param capitalAssetTypeCode The capitalAssetTypeCode to set.
	 * 
	 */
	public void setCapitalAssetTypeCode(String capitalAssetTypeCode) {
		this.capitalAssetTypeCode = capitalAssetTypeCode;
	}


	/**
	 * Gets the manufacturerName attribute.
	 * 
	 * @return Returns the manufacturerName
	 * 
	 */
	public String getManufacturerName() { 
		return manufacturerName;
	}

	/**
	 * Sets the manufacturerName attribute.
	 * 
	 * @param manufacturerName The manufacturerName to set.
	 * 
	 */
	public void setManufacturerName(String manufacturerName) {
		this.manufacturerName = manufacturerName;
	}


	/**
	 * Gets the manufacturerModelNumber attribute.
	 * 
	 * @return Returns the manufacturerModelNumber
	 * 
	 */
	public String getManufacturerModelNumber() { 
		return manufacturerModelNumber;
	}

	/**
	 * Sets the manufacturerModelNumber attribute.
	 * 
	 * @param manufacturerModelNumber The manufacturerModelNumber to set.
	 * 
	 */
	public void setManufacturerModelNumber(String manufacturerModelNumber) {
		this.manufacturerModelNumber = manufacturerModelNumber;
	}


	/**
	 * Gets the vendorName attribute.
	 * 
	 * @return Returns the vendorName
	 * 
	 */
	public String getVendorName() { 
		return vendorName;
	}

	/**
	 * Sets the vendorName attribute.
	 * 
	 * @param vendorName The vendorName to set.
	 * 
	 */
	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}


	/**
	 * Gets the assetTopsDescription attribute.
	 * 
	 * @return Returns the assetTopsDescription
	 * 
	 */
	public String getAssetTopsDescription() { 
		return assetTopsDescription;
	}

	/**
	 * Sets the assetTopsDescription attribute.
	 * 
	 * @param assetTopsDescription The assetTopsDescription to set.
	 * 
	 */
	public void setAssetTopsDescription(String assetTopsDescription) {
		this.assetTopsDescription = assetTopsDescription;
	}


	/**
	 * Gets the organizationDescription attribute.
	 * 
	 * @return Returns the organizationDescription
	 * 
	 */
	public String getOrganizationDescription() { 
		return organizationDescription;
	}

	/**
	 * Sets the organizationDescription attribute.
	 * 
	 * @param organizationDescription The organizationDescription to set.
	 * 
	 */
	public void setOrganizationDescription(String organizationDescription) {
		this.organizationDescription = organizationDescription;
	}


	/**
	 * Gets the organizationText attribute.
	 * 
	 * @return Returns the organizationText
	 * 
	 */
	public String getOrganizationText() { 
		return organizationText;
	}

	/**
	 * Sets the organizationText attribute.
	 * 
	 * @param organizationText The organizationText to set.
	 * 
	 */
	public void setOrganizationText(String organizationText) {
		this.organizationText = organizationText;
	}


	/**
	 * Gets the organizationInventoryName attribute.
	 * 
	 * @return Returns the organizationInventoryName
	 * 
	 */
	public String getOrganizationInventoryName() { 
		return organizationInventoryName;
	}

	/**
	 * Sets the organizationInventoryName attribute.
	 * 
	 * @param organizationInventoryName The organizationInventoryName to set.
	 * 
	 */
	public void setOrganizationInventoryName(String organizationInventoryName) {
		this.organizationInventoryName = organizationInventoryName;
	}

    public UniversalUser getPersonUniversal() {
        personUniversal = SpringContext.getBean(UniversalUserService.class).updateUniversalUserIfNecessary(representativeUniversalIdentifier, personUniversal);
        return personUniversal;
    }

    public void setPersonUniversal(UniversalUser personUniversal) {
        this.personUniversal = personUniversal;
    }

	/**
	 * Gets the representativeUniversalIdentifier attribute.
	 * 
	 * @return Returns the representativeUniversalIdentifier
	 * 
	 */
	public String getRepresentativeUniversalIdentifier() { 
		return representativeUniversalIdentifier;
	}

    /**
     * Sets the representativeUniversalIdentifier attribute.
     * 
     * @param representativeUniversalIdentifier The representativeUniversalIdentifier to set.
     * 
     */
    public void setRepresentativeUniversalIdentifier(String representativeUniversalIdentifier) {
        this.representativeUniversalIdentifier = representativeUniversalIdentifier;
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
	 * Gets the pretagCreateDate attribute.
	 * 
	 * @return Returns the pretagCreateDate
	 * 
	 */
	public Date getPretagCreateDate() { 
		return pretagCreateDate;
	}

	/**
	 * Sets the pretagCreateDate attribute.
	 * 
	 * @param pretagCreateDate The pretagCreateDate to set.
	 * 
	 */
	public void setPretagCreateDate(Date pretagCreateDate) {
		this.pretagCreateDate = pretagCreateDate;
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
     * Gets the pretagDetails attribute. 
     * @return Returns the pretagDetails.
     */
    public List<PretagDetail> getPretagDetails() {
        return pretagDetails;
    }

    /**
     * Sets the pretagDetails attribute value.
     * @param pretagDetails The pretagDetails to set.
     */
    public void setPretagDetails(List<PretagDetail> pretagDetails) {
        this.pretagDetails = pretagDetails;
    }

    /**
     * Gets the purchaseOrder attribute. 
     * @return Returns the purchaseOrder.
     */
    public PendingPurchaseOrder getPurchaseOrder() {
        return purchaseOrder;
    }

    /**
     * Sets the purchaseOrder attribute value.
     * @param purchaseOrder The purchaseOrder to set.
     * @deprecated
     */
    public void setPurchaseOrder(PendingPurchaseOrder purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }
    
    /**
     * Gets the capitalAssetType attribute. 
     * @return Returns the capitalAssetType.
     */
    public AssetType getCapitalAssetType() {
        return capitalAssetType;
    }

    /**
     * Sets the capitalAssetType attribute value.
     * @param capitalAssetType The capitalAssetType to set.
     * @deprecated
     */
    public void setCapitalAssetType(AssetType capitalAssetType) {
        this.capitalAssetType = capitalAssetType;
    }

    /**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("purchaseOrderNumber", this.purchaseOrderNumber);
        if (this.lineItemNumber != null) {
            m.put("lineItemNumber", this.lineItemNumber.toString());
        }
	    return m;
    }

}
