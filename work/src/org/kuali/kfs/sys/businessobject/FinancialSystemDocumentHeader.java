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
package org.kuali.kfs.sys.businessobject;

import java.sql.Date;
import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.workflow.service.WorkflowDocumentService;

/**
 * This class is a custom {@link DocumentHeader} class used by KFS to facilitate custom data fields and a few UI fields
 */
public class FinancialSystemDocumentHeader extends DocumentHeader {

    protected KualiDecimal financialDocumentTotalAmount;
    protected String correctedByDocumentId;
    protected String financialDocumentInErrorNumber;
    protected String financialDocumentStatusCode;
    protected String workflowDocumentStatusCode;
    protected String applicationDocumentStatus;
    protected String initiatorPrincipalId;
    protected String workflowDocumentTypeName;
    protected Timestamp workflowCreateDate;

    /**
     * Constructor - creates empty instances of dependent objects
     *
     */
    public FinancialSystemDocumentHeader() {
        super();
        financialDocumentStatusCode = KFSConstants.DocumentStatusCodes.INITIATED;
    }

    /**
     * Gets the financialDocumentTotalAmount attribute.
     * @return Returns the financialDocumentTotalAmount.
     */
    public KualiDecimal getFinancialDocumentTotalAmount() {
        return financialDocumentTotalAmount;
    }

    /**
     * Sets the financialDocumentTotalAmount attribute value.
     * @param financialDocumentTotalAmount The financialDocumentTotalAmount to set.
     */
    public void setFinancialDocumentTotalAmount(KualiDecimal financialDocumentTotalAmount) {
        this.financialDocumentTotalAmount = financialDocumentTotalAmount;
    }

    /**
     * Gets the correctedByDocumentId attribute.
     * @return Returns the correctedByDocumentId.
     */
    public String getCorrectedByDocumentId() {
        return correctedByDocumentId;
    }

    /**
     * Sets the correctedByDocumentId attribute value.
     * @param correctedByDocumentId The correctedByDocumentId to set.
     */
    public void setCorrectedByDocumentId(String correctedByDocumentId) {
        this.correctedByDocumentId = correctedByDocumentId;
    }

    /**
     * Gets the financialDocumentInErrorNumber attribute.
     * @return Returns the financialDocumentInErrorNumber.
     */
    public String getFinancialDocumentInErrorNumber() {
        return financialDocumentInErrorNumber;
    }

    /**
     * Sets the financialDocumentInErrorNumber attribute value.
     * @param financialDocumentInErrorNumber The financialDocumentInErrorNumber to set.
     */
    public void setFinancialDocumentInErrorNumber(String financialDocumentInErrorNumber) {
        this.financialDocumentInErrorNumber = financialDocumentInErrorNumber;
    }

    /**
     * Gets the financialDocumentStatusCode attribute.
     * @return Returns the financialDocumentStatusCode.
     */
    public String getFinancialDocumentStatusCode() {
        return financialDocumentStatusCode;
    }

    /**
     * Sets the financialDocumentStatusCode attribute value.
     * @param financialDocumentStatusCode The financialDocumentStatusCode to set.
     */
    public void setFinancialDocumentStatusCode(String financialDocumentStatusCode) {
        this.financialDocumentStatusCode = financialDocumentStatusCode;
    }

    public String getWorkflowDocumentStatusCode() {
        return workflowDocumentStatusCode;
    }

    public void setWorkflowDocumentStatusCode(String workflowDocumentStatusCode) {
        this.workflowDocumentStatusCode = workflowDocumentStatusCode;
    }

    public String getInitiatorPrincipalId() {
        return initiatorPrincipalId;
    }

    public void setInitiatorPrincipalId(String initiatorPrincipalId) {
        this.initiatorPrincipalId = initiatorPrincipalId;
    }

    public String getWorkflowDocumentTypeName() {
        return workflowDocumentTypeName;
    }

    public void setWorkflowDocumentTypeName(String workflowDocumentTypeName) {
        this.workflowDocumentTypeName = workflowDocumentTypeName;
    }

    /**
     * Gets the documentFinalDate attribute.
     * @return Returns the documentFinalDate.
     */
    public Date getDocumentFinalDate() {
        WorkflowDocument workflowDoc = this.getWorkflowDocument();
        if (workflowDoc == null || (workflowDoc.getDateFinalized() == null)) {
            return null;
        }
        return  new java.sql.Date(workflowDoc.getDateFinalized().getMillis());
    }

    /**
     * Gets the applicationDocumentStatus attribute.
     *
     * @return Returns the applicationDocumentStatus
     */

    public String getApplicationDocumentStatus() {
        return applicationDocumentStatus;
    }

    /**
     * Sets the applicationDocumentStatus attribute.
     *
     * @param applicationDocumentStatus The applicationDocumentStatus to set.
     */
    public void setApplicationDocumentStatus(String applicationDocumentStatus) {
        WorkflowDocument document = getWorkflowDocument();

        document.setApplicationDocumentStatus(applicationDocumentStatus);
        this.applicationDocumentStatus = applicationDocumentStatus;
    }

    /**
     * method to retrieve the workflow document for the given documentHeader.
     *
     * @return workflowDocument
     */
    @Override
    public WorkflowDocument getWorkflowDocument() {

        WorkflowDocument workflowDocument = null;

        if (hasWorkflowDocument()) {
            workflowDocument = super.getWorkflowDocument();
        }

        try {
            if (workflowDocument != null) {
                return workflowDocument;
            }
            if ( StringUtils.isNotBlank(getDocumentNumber()) ) {
                workflowDocument = SpringContext.getBean(WorkflowDocumentService.class).loadWorkflowDocument(getDocumentNumber(), GlobalVariables.getUserSession().getPerson());
            } else {
                throw new RuntimeException("Document number is blank/null.  Unable to load a WorkflowDocument" );
            }
        }
        catch (WorkflowException we) {
            throw new RuntimeException("Unable to load a WorkflowDocument object for " + getDocumentNumber(), we);
        }

        return workflowDocument;
    }

    /**
     * Updates status of this document and saves the workflow data
     *
     * @param applicationDocumentStatus is the app doc status to save
     * @throws WorkflowException
     */
    public void updateAndSaveAppDocStatus(String applicationDocumentStatus) throws WorkflowException {
       setApplicationDocumentStatus(applicationDocumentStatus);
       SpringContext.getBean(WorkflowDocumentService.class).saveRoutingData(getWorkflowDocument());
       SpringContext.getBean(BusinessObjectService.class).save(this);
    }

    public Timestamp getWorkflowCreateDate() {
        return workflowCreateDate;
    }

    public void setWorkflowCreateDate(Timestamp workflowCreateDate) {
        this.workflowCreateDate = workflowCreateDate;
    }
}
