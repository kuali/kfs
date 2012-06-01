/*
 * Copyright 2006-2008 The Kuali Foundation
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

package org.kuali.kfs.module.purap.document;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.PurapWorkflowConstants;
import org.kuali.kfs.module.purap.businessobject.ContractManagerAssignmentDetail;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.module.purap.document.service.RequisitionService;
import org.kuali.kfs.sys.DynamicCollectionComparator;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.action.ActionRequestType;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.DocumentService;

public class ContractManagerAssignmentDocument extends FinancialSystemTransactionalDocumentBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ContractManagerAssignmentDocument.class);

    protected List<ContractManagerAssignmentDetail> contractManagerAssignmentDetails = new ArrayList<ContractManagerAssignmentDetail>();

    // Not persisted (only for labels in tag)
    protected String requisitionNumber;
    protected String deliveryCampusCode;
    protected String vendorName;
    protected String generalDescription;
    protected String requisitionTotalAmount;
    protected String requisitionCreateDate;
    protected String firstItemDescription;
    protected String firstItemCommodityCode;
    protected String firstObjectCode;
    protected String universityFiscalYear;


    /**
     * Default constructor.
     */
    public ContractManagerAssignmentDocument() {
        super();
    }

    public ContractManagerAssignmentDetail getContractManagerAssignmentDetail(int index) {
        while (contractManagerAssignmentDetails.size() <= index) {
            contractManagerAssignmentDetails.add(new ContractManagerAssignmentDetail());
        }
        return (ContractManagerAssignmentDetail) contractManagerAssignmentDetails.get(index);
    }

    /**
     * Perform logic needed to populate the Assign Contract Manager Document with requisitions in status of Awaiting Contract
     * Manager Assignment.
     */
    public void populateDocumentWithRequisitions() {
        LOG.debug("populateDocumentWithRequisitions() Entering method.");
        
        List<RequisitionDocument> unassignedRequisitions = new ArrayList(SpringContext.getBean(RequisitionService.class).getRequisitionsAwaitingContractManagerAssignment());
        List<String>documentHeaderIds = new ArrayList();
        for (RequisitionDocument req : unassignedRequisitions) {
            documentHeaderIds.add(req.getDocumentNumber());
        }
        
        List<Document> requisitionDocumentsFromDocService = new ArrayList();
        try {
            if ( documentHeaderIds.size() > 0 )
                requisitionDocumentsFromDocService = SpringContext.getBean(DocumentService.class).getDocumentsByListOfDocumentHeaderIds(RequisitionDocument.class, documentHeaderIds);
        }
        catch (WorkflowException we) {
            String errorMsg = "Workflow Exception caught: " + we.getLocalizedMessage();
            LOG.error(errorMsg, we);
            throw new RuntimeException(errorMsg, we);
        }
  
        for (Document req : requisitionDocumentsFromDocService) {
            contractManagerAssignmentDetails.add(new ContractManagerAssignmentDetail(this, (RequisitionDocument) req));
        }

        String[] fieldNames = {PurapPropertyConstants.DELIVERY_CAMPUS_CODE, PurapPropertyConstants.VENDOR_NAME, PurapPropertyConstants.REQUISITION_IDENTIFIER};
        DynamicCollectionComparator.sort(contractManagerAssignmentDetails, fieldNames);
        LOG.debug("populateDocumentWithRequisitions() Leaving method.");   
    }

    @Override
    public void doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) {
        LOG.debug("doRouteStatusChange() Entering method.");

        super.doRouteStatusChange(statusChangeEvent);

        if (this.getFinancialSystemDocumentHeader().getWorkflowDocument().isProcessed()) {
            boolean isSuccess = true;
            StringBuffer failedReqs = new StringBuffer();
            SpringContext.getBean(PurchaseOrderService.class).processACMReq(this);

            if (!isSuccess) {
                failedReqs.deleteCharAt(failedReqs.lastIndexOf(","));
                WorkflowDocument workflowDoc = this.getDocumentHeader().getWorkflowDocument();
                String currentNodeName = null;
                try {
                    currentNodeName = PurapWorkflowConstants.DOC_ADHOC_NODE_NAME;
                    if (!(KewApiConstants.ROUTE_HEADER_INITIATED_CD.equals(workflowDoc.getStatus().getCode()))) {
                        if (this.getCurrentRouteNodeName(workflowDoc) != null) {
                            currentNodeName = this.getCurrentRouteNodeName(workflowDoc);
                        }
                    }
                    workflowDoc.adHocToPrincipal( ActionRequestType.FYI, currentNodeName, PurapWorkflowConstants.ContractManagerAssignmentDocument.ASSIGN_CONTRACT_DOC_ERROR_COMPLETING_POST_PROCESSING + failedReqs, workflowDoc.getInitiatorPrincipalId(), "Initiator", true);
                }
                catch (WorkflowException e) {
                    // do nothing; document should have processed successfully and problem is with sending FYI
                }
            }
        }
        LOG.debug("doRouteStatusChange() Leaving method.");
    }

    /**
     * 
     * @param wd
     * @return
     * @throws WorkflowException
     */
    protected String getCurrentRouteNodeName(WorkflowDocument wd) throws WorkflowException {
        Set<String> nodeNames = wd.getCurrentNodeNames();
        if (nodeNames == null || nodeNames.isEmpty()) {
            return null;
        }
        else {
            return nodeNames.iterator().next();
        }
    }

    /**
     * @see org.kuali.rice.krad.document.Document#getDocumentTitle()
     */
    @Override
    public String getDocumentTitle() {
        String title = "";
        if (SpringContext.getBean(ParameterService.class).getParameterValueAsBoolean(ContractManagerAssignmentDocument.class, PurapParameterConstants.PURAP_OVERRIDE_ASSIGN_CONTRACT_MGR_DOC_TITLE)) {
            title = PurapWorkflowConstants.ContractManagerAssignmentDocument.WORKFLOW_DOCUMENT_TITLE;
        }
        else {
            title = super.getDocumentTitle();
        }
        return title;
    }

    public List getContractManagerAssignmentDetails() {
        return contractManagerAssignmentDetails;
    }

    public void setContractManagerAssignmentDetailss(List contractManagerAssignmentDetails) {
        this.contractManagerAssignmentDetails = contractManagerAssignmentDetails;
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
    * Gets the firstItemCommodityCode attribute.
    * 
    * @return Returns the firstItemCommodityCode.
    */
    public String getFirstItemCommodityCode() {
        return firstItemCommodityCode;
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

    public String getUniversityFiscalYear() {
        return universityFiscalYear;
    }
    
}
