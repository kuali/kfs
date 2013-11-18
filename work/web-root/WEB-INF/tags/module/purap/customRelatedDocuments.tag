<%--
  Copyright 2007-2009 The Kuali Foundation
 
 Licensed under the Educational Community License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl2.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<%@ attribute name="documentAttributes" required="true" type="java.util.Map" 
              description="The DataDictionary entry containing attributes for this row's fields."%>
              
<c:set var="documentType" value="${KualiForm.document.documentHeader.workflowDocument.documentTypeName}" />
<c:set var="isATypeOfPurAPRecDoc" value="${KualiForm.document.isATypeOfPurAPRecDoc}" />
<c:set var="isATypeOfPODoc" value="${KualiForm.document.isATypeOfPODoc}" />
<c:set var="isRequisition" value="${KualiForm.document.isReqsDoc}" />

<c:choose>
    <c:when test="${isRequisition}">
    </c:when>   
    <c:when test="${isATypeOfPODoc}">
        <c:set var="limitByPoId" value="${KualiForm.document.purapDocumentIdentifier}" />
    </c:when>
    <c:otherwise>
        <c:set var="limitByPoId" value="${KualiForm.document.purchaseOrderIdentifier}" />
	</c:otherwise>
</c:choose>

<purap:loadOnOpenTab tabTitle="View Related Documents" defaultOpen="false" tabErrorKey="${PurapConstants.RELATED_DOCS_TAB_ERRORS}">
    <div class="tab-container" align=center>

		<h3>Related Documents</h3>
		<br/>
		<c:if test="${kfunc:getTabState(KualiForm, tabKey) == 'OPEN'}">
		<purap:relatedDocumentsDetail documentAttributes="${documentAttributes}"
			viewList="document.relatedViews.relatedRequisitionViews" /> 
		
		<purap:relatedPurchaseOrderDocumentsDetail documentAttributes="${documentAttributes}"
			groupList="document.relatedViews.groupedRelatedPurchaseOrderViews"
			limitByPoId="${limitByPoId}" /> 

		<purap:relatedDocumentsDetail documentAttributes="${documentAttributes}"
			viewList="document.relatedViews.relatedBulkReceivingViews" 
			limitByPoId="${limitByPoId}" /> 

		<purap:relatedReceivingDocumentsDetail documentAttributes="${documentAttributes}"
			groupList="document.relatedViews.groupedRelatedReceivingViews" 
			limitByPoId="${limitByPoId}" /> 
			 
		<purap:relatedDocumentsDetail documentAttributes="${documentAttributes}"
			viewList="document.relatedViews.relatedPaymentRequestViews"
			limitByPoId="${limitByPoId}" /> 

		<purap:relatedDocumentsDetail documentAttributes="${documentAttributes}"
			viewList="document.relatedViews.relatedCreditMemoViews"
			limitByPoId="${limitByPoId}" /> 
			
		<c:if test="${!isATypeOfPurAPRecDoc}">
			<purap:relatedElectronicRejectDocumentsDetail documentAttributes="${documentAttributes}"
				viewList="document.relatedViews.relatedRejectViews"
				limitByPoId="${limitByPoId}" /> 	
		</c:if>
		</c:if>
    </div>
</purap:loadOnOpenTab>
