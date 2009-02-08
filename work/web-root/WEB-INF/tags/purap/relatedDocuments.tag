<%--
 Copyright 2007 The Kuali Foundation.
 
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

<%@ attribute name="documentAttributes" required="true" type="java.util.Map" 
              description="The DataDictionary entry containing attributes for this row's fields."%>
              
<c:set var="documentType" value="${KualiForm.document.documentHeader.workflowDocument.documentType}" />
<c:choose>
    <c:when test="${fn:startsWith(documentType,'PO')}">
        <c:set var="limitByPoId" value="${KualiForm.document.purapDocumentIdentifier}" />
    </c:when>
    <c:when test="${documentType == 'REQS'}">
    </c:when>   
    <c:otherwise>
        <c:set var="limitByPoId" value="${KualiForm.document.purchaseOrderIdentifier}" />
	</c:otherwise>
</c:choose>

<kul:tab tabTitle="View Related Documents" defaultOpen="false" tabErrorKey="${PurapConstants.RELATED_DOCS_TAB_ERRORS}">
    <div class="tab-container" align=center>
            <h3>Related Documents</h3>
		<br />

		<purap:relatedDocumentsDetail documentAttributes="${documentAttributes}"
			viewList="document.relatedViews.relatedRequisitionViews" /> 
		
		<purap:relatedPurchaseOrderDocumentsDetail documentAttributes="${documentAttributes}"
			viewList="document.relatedViews.groupedRelatedPurchaseOrderViews"
			limitByPoId="${limitByPoId}" /> 

		<purap:relatedDocumentsDetail documentAttributes="${documentAttributes}"
			viewList="document.relatedViews.relatedPaymentRequestViews"
			limitByPoId="${limitByPoId}" /> 

		<purap:relatedDocumentsDetail documentAttributes="${documentAttributes}"
			viewList="document.relatedViews.relatedCreditMemoViews"
			limitByPoId="${limitByPoId}" /> 

		<purap:relatedDocumentsDetail documentAttributes="${documentAttributes}"
			viewList="document.relatedViews.relatedLineItemReceivingViews" /> 

		<purap:relatedDocumentsDetail documentAttributes="${documentAttributes}"
			viewList="document.relatedViews.relatedCorrectionReceivingViews" /> 
			
		<purap:relatedDocumentsDetail documentAttributes="${documentAttributes}"
			viewList="document.relatedViews.relatedBulkReceivingViews" /> 
    </div>
</kul:tab>
