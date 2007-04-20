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
<%@ include file="/jsp/core/tldHeader.jsp"%>
<%@ taglib tagdir="/WEB-INF/tags/purap" prefix="purap"%>

<kul:documentPage showDocumentInfo="true"
    documentTypeName="KualiPurchaseOrderDocument"
    htmlFormAction="purapPurchaseOrder" renderMultipart="true"
    showTabButtons="true">

    <c:if test="${!empty KualiForm.editingMode['fullEntry']}">
        <c:set var="fullEntryMode" value="true" scope="request" />
    </c:if>

    <kul:hiddenDocumentFields excludePostingYear="true" />

    <purap:hiddenPurapFields />
    <!-- TODO move this to where? -->
    <html:hidden property="document.requisitionIdentifier" />
    <html:hidden property="document.purchaseOrderCurrentIndicator" />
    <html:hidden property="document.pendingActionIndicator" />

    <kul:documentOverview editingMode="${KualiForm.editingMode}"
        includePostingYear="true"
        postingYearAttributes="${DataDictionary.KualiPurchaseOrderDocument.attributes}" >

        <purap:purapDocumentDetail
            documentAttributes="${DataDictionary.KualiPurchaseOrderDocument.attributes}"
            purchaseOrder="true"
            detailSectionLabel="Purchase Order Detail" />
    </kul:documentOverview>

    <c:if test="${KualiForm.editingMode['displayRetransmitTab']}" >
        <purap:purchaseOrderRetransmit documentAttributes="${DataDictionary.KualiPurchaseOrderDocument.attributes}"
            displayPurchaseOrderFields="true" />
    </c:if>
    	 		 
<c:if test="${not KualiForm.editingMode['displayRetransmitTab']}" >
    <purap:vendor
        documentAttributes="${DataDictionary.KualiPurchaseOrderDocument.attributes}" 
        displayPurchaseOrderFields="true" />

    <purap:stipulationsAndInfo
        documentAttributes="${DataDictionary.KualiPurchaseOrderDocument.attributes}" />


    <purap:puritems itemAttributes="${DataDictionary.PurchaseOrderItem.attributes}"
        accountingLineAttributes="${DataDictionary.PurchaseOrderAccountingLine.attributes}"/> 

     
    <purap:paymentinfo
        documentAttributes="${DataDictionary.KualiPurchaseOrderDocument.attributes}" 
        displayPurchaseOrderFields="true"/>

    <purap:delivery
        documentAttributes="${DataDictionary.KualiPurchaseOrderDocument.attributes}" />

    <purap:additional
        documentAttributes="${DataDictionary.KualiPurchaseOrderDocument.attributes}" />

    <purap:statushistory 
        documentAttributes="${DataDictionary.PurchaseOrderStatusHistory.attributes}">
          <html:messages id="warnings" property="statusHistoryWarning" message="true">
            &nbsp;&nbsp;&nbsp;<bean:write name="warnings"/><br><br>
          </html:messages>       
    </purap:statushistory>

    <kul:notes notesBo="${KualiForm.document.documentBusinessObject.boNotes}" noteType="${Constants.NoteTypeEnum.BUSINESS_OBJECT_NOTE_TYPE}" >
          <html:messages id="warnings" property="noteWarning" message="true">
            &nbsp;&nbsp;&nbsp;<bean:write name="warnings"/><br><br>
          </html:messages>
    </kul:notes> 

    <kul:adHocRecipients />

    <kul:routeLog />

</c:if>
    <kul:panelFooter />

    <c:set var="extraButtons" value="${KualiForm.extraButtons}"/>  	
  	
    <kul:documentControls 
        transactionalDocument="true" 
        extraButtons="${extraButtons}"
        />


</kul:documentPage>
