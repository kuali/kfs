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

import org.kuali.core.bo.BusinessObject;
import org.kuali.core.document.DocumentHeader;
import org.kuali.core.document.TransactionalDocumentBase;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.financial.document.AuxiliaryVoucherDocument;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.bo.AssignContractManagerDetail;
import org.kuali.module.purap.bo.ContractManager;
import org.kuali.module.purap.service.PurapService;

/**
 * @author PURAP (kualidev@oncourse.iu.edu)
 */
public class AssignContractManagerDocument extends TransactionalDocumentBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssignContractManagerDocument.class);

    private String financialDocumentNumber;
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
    
    private BusinessObjectService boService;
    protected BusinessObjectService getBOService() {
        if( ObjectUtils.isNull( this.boService ) ) {
            this.boService = SpringServiceLocator.getBusinessObjectService();
        }
        return this.boService;
    }

    private PurapService purapService;
    protected PurapService getPurapService() {
        if( ObjectUtils.isNull( this.purapService ) ) {
            this.purapService = SpringServiceLocator.getPurapService();
        }
        return this.purapService;
    }

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
    
    /**
     * Perform logic needed to initiate Assign Contract Manager Document.
     */
    public void initiateDocument() {

        // Get list of requisitions with status of Awaiting Contract Manager Assignment.
        Map fieldValues = new HashMap();
        fieldValues.put(PurapPropertyConstants.STATUS_CODE, PurapConstants.REQ_STAT_AWAIT_CONTRACT_MANAGER_ASSGN);
        List unassignedRequisitions = new ArrayList(getBOService().findMatching(RequisitionDocument.class, fieldValues));
// TODO: ripierce, the above list won't be sorted. The findMatchingOrderBy method in the line below should sort but
//    I can't make it work, will come back to it.
//        List requisitionList = new ArrayList(getBOService().findMatchingOrderBy(RequisitionDocument.class, fieldValues, PurapPropertyConstants.DOCUMENT_IDENTIFIER, true));
        this.setUnassignedRequisitions(unassignedRequisitions);
    }
    
	@Override
    public void handleRouteStatusChange() {
        LOG.debug("In handleRouteStatusChange() for AssignContractManager documents");
        super.handleRouteStatusChange();
        
        if (this.getDocumentHeader().getWorkflowDocument().stateIsProcessed()
          || this.getDocumentHeader().getWorkflowDocument().stateIsApproved()) {
            // TODO: verify that the above if is checking for the proper status.

            for (Iterator iter = this.getAssignContractManagerDetails().iterator(); iter.hasNext();) {
                AssignContractManagerDetail detail = (AssignContractManagerDetail) iter.next();
                
                // Get the requisition for this AssignContractManagerDetail.
                Map keys = new HashMap();
                keys.put(PurapPropertyConstants.DOCUMENT_IDENTIFIER, detail.getRequisitionIdentifier());
                RequisitionDocument req = 
                  (RequisitionDocument)boService.findByPrimaryKey(RequisitionDocument.class, keys);

                // TODO: verify that another document hasn't already assigned this req, 
                //   deal with fyi's if needed.
                req.setContractManagerCode(detail.getContractManagerCode());
                
                boolean success = this.getPurapService().updateStatusAndStatusHistory(req, 
                  PurapConstants.REQ_STAT_CONTRACT_MANAGER_ASSGN);
                if (success) {
                    LOG.debug("Status and status history have been updated for requisition #"+detail.getRequisitionIdentifier());
                }
                else {
                    LOG.debug("Updating of status and status history failed for requisition #"+detail.getRequisitionIdentifier());
                }
                
                boService.save((BusinessObject)req);
            }
        }
    }

    /**
	 * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
	 */
//	protected LinkedHashMap toStringMapper() {
//	    LinkedHashMap m = new LinkedHashMap();	    
//        if (this.requisitionIdentifier != null) {
//            m.put("requisitionIdentifier", this.requisitionIdentifier.toString());
//        }
//	    return m;
//    }

    public List getUnassignedRequisitions() {
        return unassignedRequisitions;
    }

    public void setUnassignedRequisitions(List unassignedRequisitions) {
        this.unassignedRequisitions = unassignedRequisitions;
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

    public String getFinancialDocumentNumber() {
        return financialDocumentNumber;
    }

    public void setFinancialDocumentNumber(String financialDocumentNumber) {
        this.financialDocumentNumber = financialDocumentNumber;
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
