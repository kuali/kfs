/*
 * Copyright 2007-2009 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
public class AccountsReceivableDocumentHeader extends PersistableBusinessObjectBase {

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
	 * Default constructor.
	 */
	public AccountsReceivableDocumentHeader() {

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
	 * Gets the customerNumber attribute.
	 *
	 * @return Returns the customerNumber
	 *
	 */
	public String getCustomerNumber() {
		return StringUtils.upperCase(customerNumber);
	}

	/**
	 * Sets the customerNumber attribute.
	 *
	 * @param customerNumber The customerNumber to set.
	 *
	 */
	public void setCustomerNumber(String customerNumber) {
		this.customerNumber = customerNumber;
	}

	/**
	 * Gets the processingChartOfAccountCode attribute.
	 *
	 * @return Returns the processingChartOfAccountCode
	 *
	 */
	public String getProcessingChartOfAccountCode() {
		return processingChartOfAccountCode;
	}

	/**
	 * Sets the processingChartOfAccountCode attribute.
	 *
	 * @param processingChartOfAccountCode The processingChartOfAccountCode to set.
	 *
	 */
	public void setProcessingChartOfAccountCode(String processingChartOfAccountCode) {
		this.processingChartOfAccountCode = processingChartOfAccountCode;
	}


	/**
	 * Gets the processingOrganizationCode attribute.
	 *
	 * @return Returns the processingOrganizationCode
	 *
	 */
	public String getProcessingOrganizationCode() {
		return processingOrganizationCode;
	}

	/**
	 * Sets the processingOrganizationCode attribute.
	 *
	 * @param processingOrganizationCode The processingOrganizationCode to set.
	 *
	 */
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
    @SuppressWarnings("unchecked")
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
