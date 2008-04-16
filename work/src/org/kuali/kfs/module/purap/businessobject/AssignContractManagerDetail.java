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
package org.kuali.module.purap.bo;

import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.web.format.DateViewTimestampObjectFormatter;
import org.kuali.core.web.format.Formatter;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.purap.document.AssignContractManagerDocument;
import org.kuali.module.purap.document.RequisitionDocument;
import org.kuali.module.vendor.bo.CommodityContractManager;
import org.kuali.module.vendor.bo.ContractManager;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * Assign Contract Manager Detail Business Object. Defines attributes in Assign Contract Manager tab.
 */
public class AssignContractManagerDetail extends PersistableBusinessObjectBase {

    private String documentNumber;
    private Integer requisitionIdentifier;
    private Integer contractManagerCode;
    private String deliveryCampusCode;
    private String vendorName;
    
    private RequisitionDocument requisition;
    private ContractManager contractManager;
    private AssignContractManagerDocument assignContractManagerDocument;
    
    private String createDate;
    
    /**
     * Default constructor.
     */
    public AssignContractManagerDetail() {

    }

    /**
     * Constructs a AssignContractManagerDetail object from an existing AssignContractManagerDocument object.
     * 
     * @param acmDocument the AssignContractManagerDocument to copy from.
     * @param requisitionDocument reference to the related requisition document.
     */
    public AssignContractManagerDetail(AssignContractManagerDocument acmDocument, RequisitionDocument requisitionDocument) {
        this.documentNumber = acmDocument.getDocumentNumber();
        this.assignContractManagerDocument = acmDocument;
        this.requisition = requisitionDocument;
        this.requisitionIdentifier = requisitionDocument.getPurapDocumentIdentifier();
        this.deliveryCampusCode = requisitionDocument.getDeliveryCampusCode();
        this.vendorName = requisitionDocument.getVendorName();
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public Integer getRequisitionIdentifier() {
        return requisitionIdentifier;
    }

    public void setRequisitionIdentifier(Integer requisitionIdentifier) {
        this.requisitionIdentifier = requisitionIdentifier;
    }

    /**
     * Returns the default contract manager code if the first line item of the
     * requisition contains commodity codes with one contract manager whose campus
     * code matches the delivery campus code on the requisition. If there are more
     * than one contract managers of the same campus code as the delivery code, we'll
     * return null. If the first line item of the requisition does not contain commodity
     * code, or contain commodity code that does not have contract manager, we'll
     * also return null
     * 
     * @return Integer the default contract manager code if applicable, or null.
     */
    public Integer getContractManagerCode() {
        if ( (contractManagerCode == null) && getFirstLineItem().getCommodityCode() != null) {
            List<CommodityContractManager> commodityContractManagers = getFirstLineItem().getCommodityCode().getCommodityContractManagers();
            if (commodityContractManagers != null && commodityContractManagers.size() > 0) {
                int count = 0;
                Integer matchingContractManagerCode = null;
                for (CommodityContractManager commodityContractManager : commodityContractManagers) {
                    if (this.getRequisition().getDeliveryCampusCode().equals(commodityContractManager.getCampusCode())) {
                        count = count + 1;
                        matchingContractManagerCode = commodityContractManager.getContractManagerCode();
                    }
                }
                if (count == 1) {
                    setContractManagerCode(matchingContractManagerCode);
                    return contractManagerCode;
                }
            }
        }
        return contractManagerCode;
    }

    public void setContractManagerCode(Integer contractManagerCode) {
        this.contractManagerCode = contractManagerCode;
    }

    public ContractManager getContractManager() {
        return contractManager;
    }

    /**
     * @deprecated
     */
    public void setContractManager(ContractManager contractManager) {
        this.contractManager = contractManager;
    }

    public RequisitionDocument getRequisition() {
        return requisition;
    }

    /**
     * @deprecated
     */
    public void setRequisition(RequisitionDocument requisition) {
        this.requisition = requisition;
    }

    public AssignContractManagerDocument getAssignContractManagerDocument() {
        return assignContractManagerDocument;
    }

    public void setAssignContractManagerDocument(AssignContractManagerDocument assignContractManagerDocument) {
        this.assignContractManagerDocument = assignContractManagerDocument;
    }
    
    /**
     * Returns the formatted string of the create date. If the createDate is currently null, we'll
     * get the createDate from the workflowDocument.
     * 
     * @return
     * @throws WorkflowException
     */
    public String getCreateDate() throws WorkflowException{
        if (createDate == null) {
            Formatter formatter = new DateViewTimestampObjectFormatter();
            createDate = (String)formatter.format(getRequisition().getDocumentHeader().getWorkflowDocument().getCreateDate());
        }
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    private PurchasingItemBase getFirstLineItem() {
        return (PurchasingItemBase)this.getRequisition().getItem(0);
    }

    public String getDeliveryCampusCode() {
        return deliveryCampusCode;
    }

    public void setDeliveryCampusCode(String deliveryCampusCode) {
        this.deliveryCampusCode = deliveryCampusCode;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        if (this.requisitionIdentifier != null) {
            m.put("requisitionIdentifier", this.requisitionIdentifier.toString());
        }
        return m;
    }
    
}
