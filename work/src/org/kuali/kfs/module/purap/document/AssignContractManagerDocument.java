/*
 * Copyright 2006-2007 The Kuali Foundation.
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
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.bo.AssignContractManagerDetail;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.exception.WorkflowException;

public class AssignContractManagerDocument extends TransactionalDocumentBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssignContractManagerDocument.class);

    private String documentNumber;
    private String financialDocumentStatusCode;
    private String financialDocumentDescription;
    private KualiDecimal financialDocumentTotalAmount;
    private String organizationDocumentNumber;
    private String financialDocumentInErrorNumber;
    private String financialDocumentTemplateNumber;
    // TODO: remove following field from here, OJB, and database after workflow API to retrieve this is implemented
    private Date documentFinalDate;

    private List notes;

    private List<AssignContractManagerDetail> assignContractManagerDetails = new ArrayList();
    
    /**
	 * Default constructor.
	 */
	public AssignContractManagerDocument() {
        super();
	}

    public AssignContractManagerDetail getAssignContractManagerDetail(int index) {
        while (assignContractManagerDetails.size() <= index) {
            assignContractManagerDetails.add(new AssignContractManagerDetail());
        }
        return (AssignContractManagerDetail) assignContractManagerDetails.get(index);
    }

    /**
     * Perform logic needed to populate the Assign Contract Manager Document with
     *   requisitions in status of Awaiting Contract Manager Assignment.
     */
    public void populateDocumentWithRequisitions() {
        LOG.debug("populateDocumentWithRequisitions() Entering method.");

        Map fieldValues = new HashMap();
        fieldValues.put(PurapPropertyConstants.STATUS_CODE, PurapConstants.RequisitionStatuses.AWAIT_CONTRACT_MANAGER_ASSGN);
        List<RequisitionDocument> unassignedRequisitions = new ArrayList(SpringServiceLocator.getBusinessObjectService().findMatchingOrderBy(RequisitionDocument.class, 
                fieldValues, PurapPropertyConstants.PURAP_DOC_ID, true));

        for (RequisitionDocument req : unassignedRequisitions) {
            assignContractManagerDetails.add(new AssignContractManagerDetail(this, req));
        }
        LOG.debug("populateDocumentWithRequisitions() Leaving method.");
    }
    
	@Override
    public void handleRouteStatusChange() {
        LOG.debug("handleRouteStatusChange() Entering method.");
        
        super.handleRouteStatusChange();
        
        if (this.getDocumentHeader().getWorkflowDocument().stateIsProcessed()) {
            boolean isSuccess = true;
            StringBuffer failedReqs = new StringBuffer();
            for (Iterator iter = this.getAssignContractManagerDetails().iterator(); iter.hasNext();) {
                AssignContractManagerDetail detail = (AssignContractManagerDetail) iter.next();
                
                if (ObjectUtils.isNotNull(detail.getContractManagerCode())) {
                    // Get the requisition for this AssignContractManagerDetail.
                    RequisitionDocument req = SpringServiceLocator.getRequisitionService().getRequisitionById(detail.getRequisitionIdentifier());

                    if (ObjectUtils.isNull(req.getContractManagerCode()) &&
                            req.getStatusCode().equals(PurapConstants.RequisitionStatuses.AWAIT_CONTRACT_MANAGER_ASSGN)) { 
                        //only update REQ if code is empty and status is correct
                        req.setContractManagerCode(detail.getContractManagerCode());
                        SpringServiceLocator.getPurapService().updateStatusAndStatusHistory(req, PurapConstants.RequisitionStatuses.CLOSED);
                        SpringServiceLocator.getRequisitionService().save(req);
                        PurchaseOrderDocument poDocument = SpringServiceLocator.getPurchaseOrderService().createPurchaseOrderDocument(req);

                    }
                    else {
                        //only send FYI to initiator if code that was already set doesn't match code this doc was trying to set
                        if (req.getContractManagerCode().compareTo(detail.getContractManagerCode()) != 0) {
                            // TODO: can we route back to initiator
                            isSuccess = false;
                            failedReqs.append(req.getPurapDocumentIdentifier() + ", ");
                        }
                    }
                }

            }// endfor

            if (!isSuccess) {
                failedReqs.deleteCharAt(failedReqs.lastIndexOf(","));
                KualiWorkflowDocument workflowDoc = this.getDocumentHeader().getWorkflowDocument();
                String currentNodeName = null;
                try {
                    currentNodeName = PurapConstants.WorkflowConstants.DOC_ADHOC_NODE_NAME;
                    if (!(EdenConstants.ROUTE_HEADER_INITIATED_CD.equals(workflowDoc.getRouteHeader().getDocRouteStatus()))) {
                        if (this.getCurrentRouteNodeName(workflowDoc) != null) {
                            currentNodeName = this.getCurrentRouteNodeName(workflowDoc);
                        }
                    }
                    workflowDoc.appSpecificRouteDocumentToUser(EdenConstants.ACTION_REQUEST_FYI_REQ, currentNodeName, 0, 
                            PurapConstants.WorkflowConstants.ASSIGN_CONTRACT_DOC_ERROR_COMPLETING_POST_PROCESSING + failedReqs, 
                            new NetworkIdVO(workflowDoc.getInitiatorNetworkId()), "Initiator", true);
                }
                catch (WorkflowException e) {
                    // TODO do something
                }
            }
        }
        LOG.debug("handleRouteStatusChange() Leaving method.");
    }


    private String getCurrentRouteNodeName(KualiWorkflowDocument wd) throws WorkflowException {
        String[] nodeNames = wd.getNodeNames();
        if ((nodeNames == null) || (nodeNames.length == 0)) {
            return null;
        }
        else {
            return nodeNames[0];
        }
    }

    public List getAssignContractManagerDetails() {
        return assignContractManagerDetails;
    }

    public void setAssignContractManagerDetails(List assignContractManagerDetails) {
        this.assignContractManagerDetails = assignContractManagerDetails;
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

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

}
