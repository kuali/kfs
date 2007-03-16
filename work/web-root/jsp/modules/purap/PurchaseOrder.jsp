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

    <purap:vendor
        documentAttributes="${DataDictionary.KualiPurchaseOrderDocument.attributes}" 
        displayPurchaseOrderFields="true" />

    <purap:stipulationsAndInfo
        documentAttributes="${DataDictionary.KualiPurchaseOrderDocument.attributes}" />

    <!-- purap:items
        documentAttributes="${DataDictionary.KualiPurchaseOrderDocument.attributes}/ -->

    <purap:paymentinfo
        documentAttributes="${DataDictionary.KualiPurchaseOrderDocument.attributes}" 
        displayPurchaseOrderFields="true"/>

    <purap:delivery
        documentAttributes="${DataDictionary.KualiPurchaseOrderDocument.attributes}" />

    <purap:additional
        documentAttributes="${DataDictionary.KualiPurchaseOrderDocument.attributes}" />

    <purap:statushistory 
        documentAttributes="${DataDictionary.PurchaseOrderStatusHistory.attributes}" />

    <kul:notes />

    <kul:adHocRecipients />

    <kul:routeLog />

    <kul:panelFooter />


  	<c:set var="extraButtonSource" value="images/buttonsmall_openorder.gif"/>
  	<c:set var="extraButtonProperty" value="methodToCall.reopenPo"/>
  	<c:set var="extraButtonAlt" value="Reopen"/>
  	
<%-- 
  	<c:set var="extraButtonSource" value="images/buttonsmall_closeorder.gif"/>
  	<c:set var="extraButtonProperty" value="methodToCall.closePO"/>
  	<c:set var="extraButtonAlt" value="Close PO"/>

--%>  	
    <kul:documentControls 
        transactionalDocument="true" 
        extraButtonSource="${extraButtonSource}"
        extraButtonProperty="${extraButtonProperty}"
        extraButtonAlt="${extraButtonAlt}"
        />

</kul:documentPage>
