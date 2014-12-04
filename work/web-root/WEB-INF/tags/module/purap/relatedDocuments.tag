<%--
   - The Kuali Financial System, a comprehensive financial management system for higher education.
   - 
   - Copyright 2005-2014 The Kuali Foundation
   - 
   - This program is free software: you can redistribute it and/or modify
   - it under the terms of the GNU Affero General Public License as
   - published by the Free Software Foundation, either version 3 of the
   - License, or (at your option) any later version.
   - 
   - This program is distributed in the hope that it will be useful,
   - but WITHOUT ANY WARRANTY; without even the implied warranty of
   - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   - GNU Affero General Public License for more details.
   - 
   - You should have received a copy of the GNU Affero General Public License
   - along with this program.  If not, see <http://www.gnu.org/licenses/>.
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

<kul:tab tabTitle="View Related Documents" defaultOpen="false" tabErrorKey="${PurapConstants.RELATED_DOCS_TAB_ERRORS}">
    <div class="tab-container" align=center>

        <h3>Related Documents</h3>
		<br/>

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
    </div>
</kul:tab>
