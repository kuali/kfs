package org.kuali.module.gl.bo;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.Org;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class CollectorHeader extends PersistableBusinessObjectBase {

	private String chartOfAccountsCode;
	private String organizationCode;
	private Date processTransmissionDate;
	private Integer processBatchSequenceNumber;
	private Integer processTotalRecordCount;
	private KualiDecimal processTotalAmount;
    private String campusCode;
    private String contactPersonPhoneNumber;
    private String contactMailingAddress;
    private String contactDeparmentName;
    
    private Org organization;
	private Chart chartOfAccounts;

	/**
	 * Default constructor.
	 */
	public CollectorHeader() {

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
	 * Gets the processTransmissionDate attribute.
	 * 
	 * @return Returns the processTransmissionDate
	 * 
	 */
	public Date getProcessTransmissionDate() { 
		return processTransmissionDate;
	}

	/**
	 * Sets the processTransmissionDate attribute.
	 * 
	 * @param processTransmissionDate The processTransmissionDate to set.
	 * 
	 */
	public void setProcessTransmissionDate(Date processTransmissionDate) {
		this.processTransmissionDate = processTransmissionDate;
	}


	/**
	 * Gets the processBatchSequenceNumber attribute.
	 * 
	 * @return Returns the processBatchSequenceNumber
	 * 
	 */
	public Integer getProcessBatchSequenceNumber() { 
		return processBatchSequenceNumber;
	}

	/**
	 * Sets the processBatchSequenceNumber attribute.
	 * 
	 * @param processBatchSequenceNumber The processBatchSequenceNumber to set.
	 * 
	 */
	public void setProcessBatchSequenceNumber(Integer processBatchSequenceNumber) {
		this.processBatchSequenceNumber = processBatchSequenceNumber;
	}


	/**
	 * Gets the processTotalRecordCount attribute.
	 * 
	 * @return Returns the processTotalRecordCount
	 * 
	 */
	public Integer getProcessTotalRecordCount() { 
		return processTotalRecordCount;
	}

	/**
	 * Sets the processTotalRecordCount attribute.
	 * 
	 * @param processTotalRecordCount The processTotalRecordCount to set.
	 * 
	 */
	public void setProcessTotalRecordCount(Integer processTotalRecordCount) {
		this.processTotalRecordCount = processTotalRecordCount;
	}


	/**
	 * Gets the processTotalAmount attribute.
	 * 
	 * @return Returns the processTotalAmount
	 * 
	 */
	public KualiDecimal getProcessTotalAmount() { 
		return processTotalAmount;
	}

	/**
	 * Sets the processTotalAmount attribute.
	 * 
	 * @param processTotalAmount The processTotalAmount to set.
	 * 
	 */
	public void setProcessTotalAmount(KualiDecimal processTotalAmount) {
		this.processTotalAmount = processTotalAmount;
	}

	/**
     * Gets the campusCode attribute. 
     * @return Returns the campusCode.
     */
    public String getCampusCode() {
        return campusCode;
    }

    /**
     * Sets the campusCode attribute value.
     * @param campusCode The campusCode to set.
     */
    public void setCampusCode(String campusCode) {
        this.campusCode = campusCode;
    }

    /**
     * Gets the contactDeparmentName attribute. 
     * @return Returns the contactDeparmentName.
     */
    public String getContactDeparmentName() {
        return contactDeparmentName;
    }

    /**
     * Sets the contactDeparmentName attribute value.
     * @param contactDeparmentName The contactDeparmentName to set.
     */
    public void setContactDeparmentName(String contactDeparmentName) {
        this.contactDeparmentName = contactDeparmentName;
    }

    /**
     * Gets the contactMailingAddress attribute. 
     * @return Returns the contactMailingAddress.
     */
    public String getContactMailingAddress() {
        return contactMailingAddress;
    }

    /**
     * Sets the contactMailingAddress attribute value.
     * @param contactMailingAddress The contactMailingAddress to set.
     */
    public void setContactMailingAddress(String contactMailingAddress) {
        this.contactMailingAddress = contactMailingAddress;
    }

    /**
     * Gets the contactPersonPhoneNumber attribute. 
     * @return Returns the contactPersonPhoneNumber.
     */
    public String getContactPersonPhoneNumber() {
        return contactPersonPhoneNumber;
    }

    /**
     * Sets the contactPersonPhoneNumber attribute value.
     * @param contactPersonPhoneNumber The contactPersonPhoneNumber to set.
     */
    public void setContactPersonPhoneNumber(String contactPersonPhoneNumber) {
        this.contactPersonPhoneNumber = contactPersonPhoneNumber;
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
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("organizationCode", this.organizationCode);
        if (this.processTransmissionDate != null) {
            m.put("processTransmissionDate", this.processTransmissionDate.toString());
        }
        if (this.processBatchSequenceNumber != null) {
            m.put("processBatchSequenceNumber", this.processBatchSequenceNumber.toString());
        }
        if (this.processTotalRecordCount != null) {
            m.put("processTotalRecordCount", this.processTotalRecordCount.toString());
        }
        if (this.processTotalAmount != null) {
            m.put("processTotalAmount", this.processTotalAmount.toString());
        }
	    return m;
    }
}
