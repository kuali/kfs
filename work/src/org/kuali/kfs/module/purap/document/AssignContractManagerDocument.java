/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/purap/document/AssignContractManagerDocument.java,v $
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kuali.module.purap.document;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.core.document.TransactionalDocumentBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.bo.AssignContractManagerDetail;

import edu.iu.uis.eden.exception.WorkflowException;

public class AssignContractManagerDocument extends TransactionalDocumentBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssignContractManagerDocument.class);

    private String financialDocumentStatusCode;
    private String financialDocumentDescription;
    private KualiDecimal financialDocumentTotalAmount;
    private String organizationDocumentNumber;
    private String financialDocumentInErrorNumber;
    private String financialDocumentTemplateNumber;
    // TODO: remove following field from here, OJB, and database after workflow API to retrieve this is implemented
    private Date documentFinalDate;

    private List notes;

    private List<AssignContractManagerDetail> assignContractManagerDetails = new ArrayList();;
    
    private List<RequisitionDocument> unassignedRequisitions = new ArrayList(); // Not in the database.
    
    /**
	 * Default constructor.
	 */
	public AssignContractManagerDocument() {
        super();
	}

    public RequisitionDocument getUnassignedRequisition(int index) {
        while (getUnassignedRequisitions().size() <= index) {
            getUnassignedRequisitions().add(new RequisitionDocument());
        }
        return (RequisitionDocument) getUnassignedRequisitions().get(index);
    }
    
    @Override
    public void processAfterRetrieve() {
        this.populateDocumentWithRequisitions();
        super.processAfterRetrieve();
    }

    /**
     * Perform logic needed to populate the Assign Contract Manager Document with
     *   requisitions in status of Awaiting Contract Manager Assignment.
     */
    public void populateDocumentWithRequisitions() {
        LOG.debug("populateDocumentWithRequisitions() Entering method.");

        Map fieldValues = new HashMap();
        fieldValues.put(PurapPropertyConstants.STATUS_CODE, PurapConstants.RequisitionStatuses.AWAIT_CONTRACT_MANAGER_ASSGN);
        List unassignedRequisitions = new ArrayList(SpringServiceLocator.getBusinessObjectService().findMatchingOrderBy(RequisitionDocument.class, 
                fieldValues, PurapPropertyConstants.REQUISITION_ID, true));

        this.setUnassignedRequisitions(unassignedRequisitions);
        LOG.debug("populateDocumentWithRequisitions() Leaving method.");
    }
    
	@Override
    public void handleRouteStatusChange() {
        LOG.debug("handleRouteStatusChange() Entering method.");
        
        super.handleRouteStatusChange();
        
        if (this.getDocumentHeader().getWorkflowDocument().stateIsProcessed()) {
            for (Iterator iter = this.getAssignContractManagerDetails().iterator(); iter.hasNext();) {
                AssignContractManagerDetail detail = (AssignContractManagerDetail) iter.next();
                
                // Get the requisition for this AssignContractManagerDetail.
                RequisitionDocument req = detail.getRequisition();
   
                // If the ContractManagerCode of the saved req is not null it means that another
                //   AssignContractManagerDocument already assigned the contract manager.
                //   If so we won't assign it here but will send an fyi to the initiator of this document.
// TODO: check the logic of the following if.
                if (ObjectUtils.isNotNull(req.getContractManagerCode()) &&
                  req.getStatusCode().equals(PurapConstants.RequisitionStatuses.CLOSED) && 
                  !req.getContractManagerCode().equals(detail.getContractManagerCode())) {
                    // TODO: send a workflow fyi here.
                    this.getDocumentHeader().getWorkflowDocument().isFYIRequested();
                    try {
                        this.getDocumentHeader().getWorkflowDocument().fyi();
                    }
                    catch (WorkflowException e) {
                        e.printStackTrace();
                    }
                } else {
                    req.setContractManagerCode(detail.getContractManagerCode());                    
                    boolean success = SpringServiceLocator.getPurapService().updateStatusAndStatusHistory(req, 
                      PurapConstants.RequisitionStatuses.CLOSED);
                    if (success) {
                        LOG.debug("Status and status history have been updated for requisition #"+detail.getRequisitionIdentifier());
                        SpringServiceLocator.getRequisitionService().save(req);
                        // TODO:  what do we do if the save fails for one or more reqs in the list?                    
                        // TODO: create PO here.                        
                    }
                    else {
                        LOG.info("FAILURE while updating status and status history for requisition #"+detail.getRequisitionIdentifier());
                    }
                }
            }
        }
        LOG.debug("handleRouteStatusChange() Leaving method.");
    }
    
    @Override
    public void prepareForSave() {
        LOG.debug("prepareForSave() Entering method.");

        this.setUserAssignedRequisitions(this.getUnassignedRequisitions());

        LOG.debug("prepareForSave() Leaving method.");
        super.prepareForSave();
    }

    public void setUserAssignedRequisitions(List unassignedRequisitions) {
        LOG.debug("setUserAssignedRequisitions(): Entering method.");
        List assignedRequisitions = this.getAssignContractManagerDetails();
        
        for (Iterator iter=unassignedRequisitions.iterator(); iter.hasNext();) {
            RequisitionDocument req = (RequisitionDocument) iter.next();
            
            if (ObjectUtils.isNotNull(req.getContractManagerCode())) {
            // User filled in the Contract Manager Code and it was validated in the Rules class.
                AssignContractManagerDetail detail = new AssignContractManagerDetail();
                detail.setContractManagerCode(req.getContractManagerCode());
                detail.setDocumentNumber(req.getDocumentNumber());
                detail.setRequisitionIdentifier(req.getIdentifier());
                assignedRequisitions.add(detail);
            }
        }
        this.assignContractManagerDetails = assignedRequisitions;
        LOG.debug("setUserAssignedRequisitions(): Leaving method.");
    }

    public List getAssignContractManagerDetails() {
        return assignContractManagerDetails;
    }

    public void setAssignContractManagerDetails(List assignContractManagerDetails) {
        this.assignContractManagerDetails = assignContractManagerDetails;
    }

    public List getUnassignedRequisitions() {
        return unassignedRequisitions;
    }

    public void setUnassignedRequisitions(List unassignedRequisitions) {
        this.unassignedRequisitions = unassignedRequisitions;
    }

    public Date getDocumentFinalDate() {
        return documentFinalDate;
    }

    public void setDocumentFinalDate(Date documentFinalDate) {
        this.documentFinalDate = documentFinalDate;
    }

    public String getFinancialDocumentDescription() {
        return financialDocumentDescription;
    }

    public void setFinancialDocumentDescription(String financialDocumentDescription) {
        this.financialDocumentDescription = financialDocumentDescription;
    }

    public String getFinancialDocumentInErrorNumber() {
        return financialDocumentInErrorNumber;
    }

    public void setFinancialDocumentInErrorNumber(String financialDocumentInErrorNumber) {
        this.financialDocumentInErrorNumber = financialDocumentInErrorNumber;
    }

    public String getFinancialDocumentStatusCode() {
        return financialDocumentStatusCode;
    }

    public void setFinancialDocumentStatusCode(String financialDocumentStatusCode) {
        this.financialDocumentStatusCode = financialDocumentStatusCode;
    }

    public String getFinancialDocumentTemplateNumber() {
        return financialDocumentTemplateNumber;
    }

    public void setFinancialDocumentTemplateNumber(String financialDocumentTemplateNumber) {
        this.financialDocumentTemplateNumber = financialDocumentTemplateNumber;
    }

    public KualiDecimal getFinancialDocumentTotalAmount() {
        return financialDocumentTotalAmount;
    }

    public void setFinancialDocumentTotalAmount(KualiDecimal financialDocumentTotalAmount) {
        this.financialDocumentTotalAmount = financialDocumentTotalAmount;
    }

    public List getNotes() {
        return notes;
    }

    public void setNotes(List notes) {
        this.notes = notes;
    }

    public String getOrganizationDocumentNumber() {
        return organizationDocumentNumber;
    }

    public void setOrganizationDocumentNumber(String organizationDocumentNumber) {
        this.organizationDocumentNumber = organizationDocumentNumber;
    }

}
