/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.ar.businessobject;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.coa.service.ChartService;
import org.kuali.kfs.coa.service.OrganizationService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class AccountsReceivableDocumentHeader extends PersistableBusinessObjectBase implements org.kuali.kfs.integration.ar.AccountsReceivableDocumentHeader{

	private String documentNumber;
	private String customerNumber;
	private String processingChartOfAccountCode;
	private String processingOrganizationCode;
	private Date entryDate;
	private String financialDocumentExplanationText;

	private Customer customer;
	private Chart processingChartOfAccount;
	private Organization processingOrganization;
    private DocumentHeader documentHeader;

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
	@Override
    public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}

	/**
	 * Gets the customerNumber attribute.
	 *
	 * @return Returns the customerNumber
	 *
	 */
	@Override
    public String getCustomerNumber() {
		return StringUtils.upperCase(customerNumber);
	}

	/**
	 * Sets the customerNumber attribute.
	 *
	 * @param customerNumber The customerNumber to set.
	 *
	 */
	@Override
    public void setCustomerNumber(String customerNumber) {
		this.customerNumber = customerNumber;
	}

	/**
	 * Gets the processingChartOfAccountCode attribute.
	 *
	 * @return Returns the processingChartOfAccountCode
	 *
	 */
	@Override
    public String getProcessingChartOfAccountCode() {
		return processingChartOfAccountCode;
	}

	/**
	 * Sets the processingChartOfAccountCode attribute.
	 *
	 * @param processingChartOfAccountCode The processingChartOfAccountCode to set.
	 *
	 */
	@Override
    public void setProcessingChartOfAccountCode(String processingChartOfAccountCode) {
		this.processingChartOfAccountCode = processingChartOfAccountCode;
	}


	/**
	 * Gets the processingOrganizationCode attribute.
	 *
	 * @return Returns the processingOrganizationCode
	 *
	 */
	@Override
    public String getProcessingOrganizationCode() {
		return processingOrganizationCode;
	}

	/**
	 * Sets the processingOrganizationCode attribute.
	 *
	 * @param processingOrganizationCode The processingOrganizationCode to set.
	 *
	 */
	@Override
    public void setProcessingOrganizationCode(String processingOrganizationCode) {
		this.processingOrganizationCode = processingOrganizationCode;
	}


	/**
	 * Gets the entryDate attribute.
	 *
	 * @return Returns the entryDate
	 *
	 */
	public Date getEntryDate() {
		return entryDate;
	}

	/**
	 * Sets the entryDate attribute.
	 *
	 * @param entryDate The entryDate to set.
	 *
	 */
	public void setEntryDate(Date entryDate) {
		this.entryDate = entryDate;
	}


	/**
	 * Gets the financialDocumentExplanationText attribute.
	 *
	 * @return Returns the financialDocumentExplanationText
	 *
	 */
	public String getFinancialDocumentExplanationText() {
		return financialDocumentExplanationText;
	}

	/**
	 * Sets the financialDocumentExplanationText attribute.
	 *
	 * @param financialDocumentExplanationText The financialDocumentExplanationText to set.
	 *
	 */
	public void setFinancialDocumentExplanationText(String financialDocumentExplanationText) {
		this.financialDocumentExplanationText = financialDocumentExplanationText;
	}

	/**
	 * Gets the customer attribute.
	 *
	 * @return Returns the customer
	 *
	 */
	public Customer getCustomer() {
		return customer;
	}

	/**
	 * Sets the customer attribute.
	 *
	 * @param customer The customer to set.
	 * @deprecated
	 */
	@Deprecated
    public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	/**
	 * Gets the processingChartOfAccount attribute.
	 *
	 * @return Returns the processingChartOfAccount
	 *
	 */
	public Chart getProcessingChartOfAccount() {
        if(processingChartOfAccount==null) {
            if(StringUtils.isNotBlank(getProcessingChartOfAccountCode())) {
                processingChartOfAccount = SpringContext.getBean(ChartService.class).getByPrimaryId(getProcessingChartOfAccountCode());
            }
        }

		return processingChartOfAccount;
	}

	/**
	 * Sets the processingChartOfAccount attribute.
	 *
	 * @param processingChartOfAccount The processingChartOfAccount to set.
	 * @deprecated
	 */
	@Deprecated
    public void setProcessingChartOfAccount(Chart processingChartOfAccount) {
		this.processingChartOfAccount = processingChartOfAccount;
	}

	/**
	 * Gets the processingOrganization attribute.
	 *
	 * @return Returns the processingOrganization
	 *
	 */
	public Organization getProcessingOrganization() {
        if(processingOrganization==null) {
            if(StringUtils.isNotBlank(getProcessingOrganizationCode()) && StringUtils.isNotBlank(getProcessingChartOfAccountCode())) {
                processingOrganization = SpringContext.getBean(OrganizationService.class).getByPrimaryId(getProcessingChartOfAccountCode(), getProcessingOrganizationCode());
            }
        }

		return processingOrganization;
	}

	/**
	 * Sets the processingOrganization attribute.
	 *
	 * @param processingOrganization The processingOrganization to set.
	 * @deprecated
	 */
	@Deprecated
    public void setProcessingOrganization(Organization processingOrganization) {
		this.processingOrganization = processingOrganization;
	}

    /**
     * Gets the documentHeader attribute.
     * @return Returns the documentHeader.
     */
    public DocumentHeader getDocumentHeader() {
        return documentHeader;
    }

    /**
     * Sets the documentHeader attribute value.
     * @param documentHeader The documentHeader to set.
     * @deprecated
     */
    @Deprecated
    public void setDocumentHeader(DocumentHeader documentHeader) {
        this.documentHeader = documentHeader;
    }

	/**
	 * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
	 */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
	    LinkedHashMap m = new LinkedHashMap();
        m.put("documentNumber", this.documentNumber);
	    return m;
    }

    /**
     * Get a string representation for processing organization
     * @return
     */
    public String getProcessingChartOfAccCodeAndOrgCode() {
        String returnVal = getProcessingChartOfAccountCode() + "/" +getProcessingOrganizationCode();

        return returnVal;
    }

    /**
     * Gets the documentStatus attribute.
     * @return Returns the documentStatus.
     */
    public String getDocumentStatus() {
        return getDocumentHeader().getWorkflowDocument().getApplicationDocumentStatus();
    }

    /**
     *
     * This method...
     * @return
     */
    public String getCreateDate() {
        return SpringContext.getBean(DateTimeService.class).toDateString(getDocumentHeader().getWorkflowDocument().getDateCreated().toDate());
    }

    /**
     *
     * This method...
     * @return
     */
    public String getInitiatorId() {
        return getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId();
    }
}
