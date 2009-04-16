/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.cab.document.web.struts;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.cab.CabConstants;
import org.kuali.kfs.module.cab.CabPropertyConstants;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.cab.document.service.PurApInfoService;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.dto.DocumentTypeDTO;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.TypedArrayList;
import org.kuali.rice.kns.web.struts.form.KualiForm;
import org.kuali.rice.kns.workflow.service.KualiWorkflowInfo;

public class PurApLineForm extends KualiForm {
    private static final Logger LOG = Logger.getLogger(PurApLineAction.class);
    private Integer purchaseOrderIdentifier;
    private String purApContactEmailAddress;
    private String purApContactPhoneNumber;

    private List<PurchasingAccountsPayableDocument> purApDocs;
    private int actionPurApDocIndex;
    private int actionItemAssetIndex;

    private KualiDecimal mergeQty;
    private String mergeDesc;

    private Integer requisitionIdentifier;

    private String purchaseOrderInquiryUrl;

    private boolean selectAll;

    private String documentNumber;

    public PurApLineForm() {
        this.purApDocs = new TypedArrayList(PurchasingAccountsPayableDocument.class);
    }

    @Override
    public boolean shouldMethodToCallParameterBeUsed(String methodToCallParameterName, String methodToCallParameterValue, HttpServletRequest request) {
        if (StringUtils.equals(methodToCallParameterName, KNSConstants.DISPATCH_REQUEST_PARAMETER) && StringUtils.equals(methodToCallParameterValue, CabConstants.Actions.START)) {
            return true;
        }
        return super.shouldMethodToCallParameterBeUsed(methodToCallParameterName, methodToCallParameterValue, request);
    }

    @Override
    public void addRequiredNonEditableProperties() {
        super.addRequiredNonEditableProperties();
        registerRequiredNonEditableProperty(CabPropertyConstants.PurchasingAccountsPayableDocument.PURCHASE_ORDER_IDENTIFIER);
    }

    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber.
     */
    public String getDocumentNumber() {
        return documentNumber;
    }


    /**
     * Sets the documentNumber attribute value.
     * 
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }


    /**
     * Gets the requisitionIdentifier attribute.
     * 
     * @return Returns the requisitionIdentifier.
     */
    public Integer getRequisitionIdentifier() {
        return requisitionIdentifier;
    }


    /**
     * Sets the requisitionIdentifier attribute value.
     * 
     * @param requisitionIdentifier The requisitionIdentifier to set.
     */
    public void setRequisitionIdentifier(Integer requisitionIdentifier) {
        this.requisitionIdentifier = requisitionIdentifier;
    }

    /**
     * Gets the mergeQty attribute.
     * 
     * @return Returns the mergeQty.
     */
    public KualiDecimal getMergeQty() {
        return mergeQty;
    }


    /**
     * Sets the mergeQty attribute value.
     * 
     * @param mergeQty The mergeQty to set.
     */
    public void setMergeQty(KualiDecimal mergeQty) {
        this.mergeQty = mergeQty;
    }


    /**
     * Gets the mergeDesc attribute.
     * 
     * @return Returns the mergeDesc.
     */
    public String getMergeDesc() {
        return mergeDesc;
    }


    /**
     * Sets the mergeDesc attribute value.
     * 
     * @param mergeDesc The mergeDesc to set.
     */
    public void setMergeDesc(String mergeDesc) {
        this.mergeDesc = mergeDesc;
    }


    /**
     * Gets the purApContactEmailAddress attribute.
     * 
     * @return Returns the purApContactEmailAddress.
     */
    public String getPurApContactEmailAddress() {
        return purApContactEmailAddress;
    }


    /**
     * Sets the purApContactEmailAddress attribute value.
     * 
     * @param purApContactEmailAddress The purApContactEmailAddress to set.
     */
    public void setPurApContactEmailAddress(String purApContactEmailAddress) {
        this.purApContactEmailAddress = purApContactEmailAddress;
    }


    /**
     * Gets the purApContactPhoneNumber attribute.
     * 
     * @return Returns the purApContactPhoneNumber.
     */
    public String getPurApContactPhoneNumber() {
        return purApContactPhoneNumber;
    }


    /**
     * Sets the purApContactPhoneNumber attribute value.
     * 
     * @param purApContactPhoneNumber The purApContactPhoneNumber to set.
     */
    public void setPurApContactPhoneNumber(String purApContactPhoneNumber) {
        this.purApContactPhoneNumber = purApContactPhoneNumber;
    }


    /**
     * Gets the actionPurApDocIndex attribute.
     * 
     * @return Returns the actionPurApDocIndex.
     */
    public int getActionPurApDocIndex() {
        return actionPurApDocIndex;
    }


    /**
     * Sets the actionPurApDocIndex attribute value.
     * 
     * @param actionPurApDocIndex The actionPurApDocIndex to set.
     */
    public void setActionPurApDocIndex(int actionPurApDocIndex) {
        this.actionPurApDocIndex = actionPurApDocIndex;
    }


    /**
     * Gets the actionItemAssetIndex attribute.
     * 
     * @return Returns the actionItemAssetIndex.
     */
    public int getActionItemAssetIndex() {
        return actionItemAssetIndex;
    }


    /**
     * Sets the actionItemAssetIndex attribute value.
     * 
     * @param actionItemAssetIndex The actionItemAssetIndex to set.
     */
    public void setActionItemAssetIndex(int actionItemAssetIndex) {
        this.actionItemAssetIndex = actionItemAssetIndex;
    }


    /**
     * Gets the purchaseOrderIdentifier attribute.
     * 
     * @return Returns the purchaseOrderIdentifier.
     */
    public Integer getPurchaseOrderIdentifier() {
        return purchaseOrderIdentifier;
    }


    /**
     * Sets the purchaseOrderIdentifier attribute value.
     * 
     * @param purchaseOrderIdentifier The purchaseOrderIdentifier to set.
     */
    public void setPurchaseOrderIdentifier(Integer purchaseOrderIdentifier) {
        this.purchaseOrderIdentifier = purchaseOrderIdentifier;
    }


    /**
     * Gets the purApDocs attribute.
     * 
     * @return Returns the purApDocs.
     */
    public List<PurchasingAccountsPayableDocument> getPurApDocs() {
        return purApDocs;
    }


    /**
     * Sets the purApDocs attribute value.
     * 
     * @param purApDocs The purApDocs to set.
     */
    public void setPurApDocs(List<PurchasingAccountsPayableDocument> purApDocs) {
        this.purApDocs = purApDocs;
    }


    /**
     * Gets the purchaseOrderInquiryUrl attribute.
     * 
     * @return Returns the purchaseOrderInquiryUrl.
     */
    public String getPurchaseOrderInquiryUrl() {
        return purchaseOrderInquiryUrl;
    }


    @Override
    public void populate(HttpServletRequest request) {
        super.populate(request);

        String parameterName = (String) request.getAttribute(KNSConstants.METHOD_TO_CALL_ATTRIBUTE);
        if (StringUtils.isNotBlank(parameterName)) {
            // populate collection index
            String purApDocIndex = StringUtils.substringBetween(parameterName, CabConstants.DOT_DOC, ".");
            if (StringUtils.isNotBlank(purApDocIndex)) {
                this.setActionPurApDocIndex(Integer.parseInt(purApDocIndex));
            }
            String itemAssetIndex = StringUtils.substringBetween(parameterName, CabConstants.DOT_LINE, ".");
            if (StringUtils.isNotBlank(itemAssetIndex)) {
                this.setActionItemAssetIndex(Integer.parseInt(itemAssetIndex));
            }
        }

        if (this.purchaseOrderIdentifier != null) {
            PurchaseOrderDocument poDoc = this.getPurApInfoService().getCurrentDocumentForPurchaseOrderIdentifier(this.purchaseOrderIdentifier);
            if (ObjectUtils.isNotNull(poDoc) && StringUtils.isNotBlank(poDoc.getDocumentNumber())) {
                this.purchaseOrderInquiryUrl = "purapPurchaseOrder.do?methodToCall=docHandler&docId=" + poDoc.getDocumentNumber() + "&command=displayDocSearchView";
            }
        }

        // clear up the documentNumber saved when submit CAMS doc
        this.setDocumentNumber(null);
    }

    private PurApInfoService getPurApInfoService() {
        return SpringContext.getBean(PurApInfoService.class);
    }


    public boolean isSelectAll() {
        return selectAll;
    }


    public void setSelectAll(boolean selectAll) {
        this.selectAll = selectAll;
    }

    /**
     * Return Asset Global forwarding URL.
     * 
     * @param request
     * @param documentNumber
     * @return
     */
    public String getDocHandlerForwardLink() {
        KualiWorkflowInfo kualiWorkflowInfo = SpringContext.getBean(KualiWorkflowInfo.class);
        try {
            String docTypeName = SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(this.documentNumber).getDocumentHeader().getWorkflowDocument().getDocumentType();
            DocumentTypeDTO docType = kualiWorkflowInfo.getDocType(docTypeName);
            String docHandlerUrl = docType.getDocTypeHandlerUrl();
            if (docHandlerUrl.indexOf("?") == -1) {
                docHandlerUrl += "?";
            }
            else {
                docHandlerUrl += "&";
            }

            docHandlerUrl += KNSConstants.PARAMETER_DOC_ID + "=" + this.documentNumber + "&" + KNSConstants.PARAMETER_COMMAND + "=" + KEWConstants.DOCSEARCH_COMMAND;
            return docHandlerUrl;
        }
        catch (WorkflowException e) {
            throw new RuntimeException("Caught WorkflowException trying to get document handler URL from Workflow", e);
        }
    }

    public PurchasingAccountsPayableDocument getPurApDoc(int index) {
        int size = getPurApDocs().size();
        while (size <= index || getPurApDocs().get(index) == null) {
            getPurApDocs().add(size++, new PurchasingAccountsPayableDocument());
        }
        return (PurchasingAccountsPayableDocument) getPurApDocs().get(index);

    }
}
