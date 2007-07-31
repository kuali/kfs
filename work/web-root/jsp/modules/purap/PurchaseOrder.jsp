<%--
 Copyright 2006-2007 The Kuali Foundation.
 
 Licensed under the Educational Community License, Version 1.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl1.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>

<kul:documentPage showDocumentInfo="true"
    documentTypeName="PurchaseOrderDocument"
    htmlFormAction="purapPurchaseOrder" renderMultipart="true"
    showTabButtons="true">

    <c:if test="${!empty KualiForm.editingMode['fullEntry']}">
        <c:set var="fullEntryMode" value="true" scope="request" />
    </c:if>

    <c:if test="${((KualiForm.editingMode['displayRetransmitTab']) and (KualiForm.document.documentHeader.workflowDocument.routeHeader.docRouteStatus != 'F'))}">
        <c:set var="retransmitMode" value="true" scope="request" />
    </c:if>
    
    <kul:hiddenDocumentFields excludePostingYear="true" />

    <purap:hiddenPurapFields />
    <!-- TODO move this to where? -->
    <html:hidden property="document.requisitionIdentifier" />
    <html:hidden property="document.purchaseOrderCurrentIndicator" />
    <html:hidden property="document.pendingActionIndicator" />

    <c:if test="${empty KualiForm.editingMode['amendmentEntry']}">
        <kul:documentOverview editingMode="${KualiForm.editingMode}"
            includePostingYear="true"
            postingYearAttributes="${DataDictionary.PurchaseOrderDocument.attributes}" >

            <purap:purapDocumentDetail
                documentAttributes="${DataDictionary.PurchaseOrderDocument.attributes}"
                purchaseOrder="true"
                detailSectionLabel="Purchase Order Detail" />
        </kul:documentOverview>
    </c:if>
    <!--  TODO maybe we ought to rename the accountingLineEditingMode to something more generic -->
    <c:if test="${! empty KualiForm.editingMode['amendmentEntry']}">
        <c:set target="${KualiForm.accountingLineEditingMode}" property="fullEntry" value="true" />
        <kul:documentOverview editingMode="${KualiForm.accountingLineEditingMode}"
            includePostingYear="true"
            postingYearAttributes="${DataDictionary.PurchaseOrderDocument.attributes}" >

            <purap:purapDocumentDetail
                documentAttributes="${DataDictionary.PurchaseOrderDocument.attributes}"
                purchaseOrder="true"
                detailSectionLabel="Purchase Order Detail" />
        </kul:documentOverview>
    </c:if>
    
    <c:if test="${retransmitMode}" >
        <purap:purchaseOrderRetransmit 
            documentAttributes="${DataDictionary.PurchaseOrderDocument.attributes}"
            itemAttributes="${DataDictionary.PurchaseOrderItem.attributes}"
            displayPurchaseOrderFields="true" />
    </c:if>
    	 		 
<c:if test="${not retransmitMode}" >
    <purap:vendor
        documentAttributes="${DataDictionary.PurchaseOrderDocument.attributes}" 
        displayPurchaseOrderFields="true" />

    <purap:stipulationsAndInfo
        documentAttributes="${DataDictionary.PurchaseOrderDocument.attributes}" />

    <purap:puritems itemAttributes="${DataDictionary.PurchaseOrderItem.attributes}"
        accountingLineAttributes="${DataDictionary.PurchaseOrderAccount.attributes}"
        camsAttributes="${DataDictionary.PurchaseOrderItemCapitalAsset.attributes}" /> 
     
    <purap:paymentinfo
        documentAttributes="${DataDictionary.PurchaseOrderDocument.attributes}" 
        displayPurchaseOrderFields="true"/>

    <purap:delivery
        documentAttributes="${DataDictionary.PurchaseOrderDocument.attributes}" />

    <purap:additional
        documentAttributes="${DataDictionary.PurchaseOrderDocument.attributes}" />
        
    <!-- TEMPORARILY DISABLING ACCOUNT SUMMARY FUNCTIONALITY -->
    <!-- purap:accountsummary
        itemAttributes="${DataDictionary.PurchaseOrderItem.attributes}"
    	documentAttributes="${DataDictionary.SourceAccountingLine.attributes}" / -->  
	
    <purap:quotes
        documentAttributes="${DataDictionary.PurchaseOrderDocument.attributes}"
        vendorQuoteAttributes="${DataDictionary.PurchaseOrderVendorQuote.attributes}"
        isPurchaseOrderAwarded="${KualiForm.document.purchaseOrderAwarded}" />

    <purap:relatedDocuments
            documentAttributes="${DataDictionary.RelatedDocuments.attributes}" />

    <purap:paymentHistory
            documentAttributes="${DataDictionary.RelatedDocuments.attributes}" />

    <purap:statushistory 
        documentAttributes="${DataDictionary.PurchaseOrderStatusHistory.attributes}">
          <html:messages id="warnings" property="statusHistoryWarning" message="true">
            &nbsp;&nbsp;&nbsp;<bean:write name="warnings"/><br><br>
          </html:messages>       
    </purap:statushistory>

    <kul:notes notesBo="${KualiForm.document.documentBusinessObject.boNotes}" noteType="${Constants.NoteTypeEnum.BUSINESS_OBJECT_NOTE_TYPE}"  allowsNoteFYI="true">
          <html:messages id="warnings" property="noteWarning" message="true">
            &nbsp;&nbsp;&nbsp;<bean:write name="warnings"/><br><br>
          </html:messages>
    </kul:notes> 

    <kul:adHocRecipients />

    <kul:routeLog />
    <gl:generalLedgerPendingEntries />

</c:if>
    <kul:panelFooter />

    <c:set var="extraButtons" value="${KualiForm.extraButtons}"/>  	
  	
    <kul:documentControls 
        transactionalDocument="true" 
        extraButtons="${extraButtons}"
        />


</kul:documentPage>
