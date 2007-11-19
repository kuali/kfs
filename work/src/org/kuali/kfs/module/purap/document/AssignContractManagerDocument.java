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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.TransactionalDocumentBase;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapParameterConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.PurapWorkflowConstants;
import org.kuali.module.purap.bo.AssignContractManagerDetail;
import org.kuali.module.purap.service.PurapService;
import org.kuali.module.purap.service.PurchaseOrderService;
import org.kuali.module.purap.service.RequisitionService;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.exception.WorkflowException;

public class AssignContractManagerDocument extends TransactionalDocumentBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssignContractManagerDocument.class);

    private List<AssignContractManagerDetail> assignContractManagerDetails = new ArrayList();

    // Not persisted (only for labels in tag)
    private String requisitionNumber;
    private String deliveryCampusCode;
    private String vendorName;
    private String generalDescription;
    private String requisitionTotalAmount;
    private String requisitionCreateDate;
    private String firstItemDescription;
    private String firstObjectCode;


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
     * Perform logic needed to populate the Assign Contract Manager Document with requisitions in status of Awaiting Contract
     * Manager Assignment.
     */
    public void populateDocumentWithRequisitions() {
        LOG.debug("populateDocumentWithRequisitions() Entering method.");
        
        Map fieldValues = new HashMap();
        fieldValues.put(PurapPropertyConstants.STATUS_CODE, PurapConstants.RequisitionStatuses.AWAIT_CONTRACT_MANAGER_ASSGN);
        List<RequisitionDocument> unassignedRequisitions = new ArrayList(SpringContext.getBean(BusinessObjectService.class).findMatchingOrderBy(RequisitionDocument.class, fieldValues, PurapPropertyConstants.PURAP_DOC_ID, true));
        List<String>documentHeaderIds = new ArrayList();
        for (RequisitionDocument req : unassignedRequisitions) {
            documentHeaderIds.add(req.getDocumentNumber());
        }
        
        List<RequisitionDocument> requisitionDocumentsFromDocService = new ArrayList();
        try {
            if ( documentHeaderIds.size() > 0 )
                requisitionDocumentsFromDocService = SpringContext.getBean(DocumentService.class).getDocumentsByListOfDocumentHeaderIds(RequisitionDocument.class, documentHeaderIds);
        }
        catch (WorkflowException we) {
            String errorMsg = "Workflow Exception caught: " + we.getLocalizedMessage();
            LOG.error(errorMsg, we);
            throw new RuntimeException(errorMsg, we);
        }
  
        for (RequisitionDocument req : requisitionDocumentsFromDocService) {
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
                    RequisitionDocument req = SpringContext.getBean(RequisitionService.class).getRequisitionById(detail.getRequisitionIdentifier());

                    if (req.getStatusCode().equals(PurapConstants.RequisitionStatuses.AWAIT_CONTRACT_MANAGER_ASSGN)) {
                        // only update REQ if code is empty and status is correct
                        SpringContext.getBean(PurapService.class).updateStatus(req, PurapConstants.RequisitionStatuses.CLOSED);
                        SpringContext.getBean(RequisitionService.class).saveDocumentWithoutValidation(req);
                        SpringContext.getBean(PurchaseOrderService.class).createPurchaseOrderDocument(req, this.getDocumentHeader().getWorkflowDocument().getInitiatorNetworkId(), detail.getContractManagerCode());

                    }
                }

            }// endfor

            if (!isSuccess) {
                failedReqs.deleteCharAt(failedReqs.lastIndexOf(","));
                KualiWorkflowDocument workflowDoc = this.getDocumentHeader().getWorkflowDocument();
                String currentNodeName = null;
                try {
                    currentNodeName = PurapWorkflowConstants.DOC_ADHOC_NODE_NAME;
                    if (!(EdenConstants.ROUTE_HEADER_INITIATED_CD.equals(workflowDoc.getRouteHeader().getDocRouteStatus()))) {
                        if (this.getCurrentRouteNodeName(workflowDoc) != null) {
                            currentNodeName = this.getCurrentRouteNodeName(workflowDoc);
                        }
                    }
                    workflowDoc.appSpecificRouteDocumentToUser(EdenConstants.ACTION_REQUEST_FYI_REQ, currentNodeName, 0, PurapWorkflowConstants.AssignContractManagerDocument.ASSIGN_CONTRACT_DOC_ERROR_COMPLETING_POST_PROCESSING + failedReqs, new NetworkIdVO(workflowDoc.getInitiatorNetworkId()), "Initiator", true);
                }
                catch (WorkflowException e) {
                    // do nothing; document should have processed successfully and problem is with sending FYI
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

    /**
     * @see org.kuali.core.document.Document#getDocumentTitle()
     */
    @Override
    public String getDocumentTitle() {
        String title = "";
        String specificTitle = SpringContext.getBean(ParameterService.class).getParameterValue(AssignContractManagerDocument.class, PurapParameterConstants.PURAP_OVERRIDE_ASSIGN_CONTRACT_MGR_DOC_TITLE);
        if (StringUtils.equalsIgnoreCase(specificTitle, Boolean.TRUE.toString())) {
            title = PurapWorkflowConstants.AssignContractManagerDocument.WORKFLOW_DOCUMENT_TITLE;
        }
        else {
            title = super.getDocumentTitle();
        }
        return title;
    }

    public List getAssignContractManagerDetails() {
        return assignContractManagerDetails;
    }

    public void setAssignContractManagerDetails(List assignContractManagerDetails) {
        this.assignContractManagerDetails = assignContractManagerDetails;
    }

    /**
     * Gets the firstObjectCode attribute.
     * 
     * @return Returns the firstObjectCode.
     */
    public String getFirstObjectCode() {
        return firstObjectCode;
    }

    /**
     * Gets the deliveryCampusCode attribute.
     * 
     * @return Returns the deliveryCampusCode.
     */
    public String getDeliveryCampusCode() {
        return deliveryCampusCode;
    }

    /**
     * Gets the firstItemDescription attribute.
     * 
     * @return Returns the firstItemDescription.
     */
    public String getFirstItemDescription() {
        return firstItemDescription;
    }

    /**
     * Gets the generalDescription attribute.
     * 
     * @return Returns the generalDescription.
     */
    public String getGeneralDescription() {
        return generalDescription;
    }

    /**
     * Gets the requisitionCreateDate attribute.
     * 
     * @return Returns the requisitionCreateDate.
     */
    public String getRequisitionCreateDate() {
        return requisitionCreateDate;
    }

    /**
     * Gets the requisitionNumber attribute.
     * 
     * @return Returns the requisitionNumber.
     */
    public String getRequisitionNumber() {
        return requisitionNumber;
    }

    /**
     * Gets the requisitionTotalAmount attribute.
     * 
     * @return Returns the requisitionTotalAmount.
     */
    public String getRequisitionTotalAmount() {
        return requisitionTotalAmount;
    }

    /**
     * Gets the vendorName attribute.
     * 
     * @return Returns the vendorName.
     */
    public String getVendorName() {
        return vendorName;
    }

}
