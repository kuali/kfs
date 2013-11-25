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
<c:set var="isATypeOfPODoc" value="${KualiForm.document.isATypeOfPODoc}" />
<c:set var="isRequisition" value="${KualiForm.document.isReqsDoc}" />

<c:choose>
    <c:when test="${isATypeOfPODoc}">
        <c:set var="limitByPoId" value="${KualiForm.document.purapDocumentIdentifier}" />
    </c:when>
    <c:when test="${isRequisition}">
    </c:when>   
    <c:otherwise>
        <c:set var="limitByPoId" value="${KualiForm.document.purchaseOrderIdentifier}" />
	</c:otherwise>
</c:choose>

<purap:loadOnOpenTab tabTitle="View Payment History" defaultOpen="false" tabErrorKey="${PurapConstants.PAYMENT_HISTORY_TAB_ERRORS}">
    <div class="tab-container" align=center>
            <h3>Payment History	- Payment Requests</h3>
		<br />

		<c:if test="${kfunc:getTabState(KualiForm, tabKey) == 'OPEN'}">
	   	<logic:notEmpty name="KualiForm" property="document.relatedViews.paymentHistoryPaymentRequestViews">
		    <table cellpadding="0" cellspacing="0" class="datatable" summary="Payment History">
				<tr>
					<kul:htmlAttributeHeaderCell scope="col">PREQ #</kul:htmlAttributeHeaderCell>
					<kul:htmlAttributeHeaderCell scope="col">Invoice #</kul:htmlAttributeHeaderCell>
					<kul:htmlAttributeHeaderCell scope="col">PO #</kul:htmlAttributeHeaderCell>
					<kul:htmlAttributeHeaderCell scope="col">PREQ Status</kul:htmlAttributeHeaderCell>
					<kul:htmlAttributeHeaderCell scope="col">Hold</kul:htmlAttributeHeaderCell>
					<kul:htmlAttributeHeaderCell scope="col">Request Cancel</kul:htmlAttributeHeaderCell>
					<kul:htmlAttributeHeaderCell scope="col">Vendor Name</kul:htmlAttributeHeaderCell>
					<kul:htmlAttributeHeaderCell scope="col">Customer #</kul:htmlAttributeHeaderCell>
					<kul:htmlAttributeHeaderCell scope="col">Amount</kul:htmlAttributeHeaderCell>
					<kul:htmlAttributeHeaderCell scope="col">Pay Date</kul:htmlAttributeHeaderCell>
					<kul:htmlAttributeHeaderCell scope="col">PDP Extract Date</kul:htmlAttributeHeaderCell>
					<kul:htmlAttributeHeaderCell scope="col">Paid?</kul:htmlAttributeHeaderCell>
	        	</tr>
				<logic:iterate id="preqHistory" name="KualiForm" property="document.relatedViews.paymentHistoryPaymentRequestViews" indexId="ctr">
					<c:if test="${(empty limitByPoId) or (limitByPoId eq preqHistory.purchaseOrderIdentifier)}">
		        		<tr>
		        			<td align="left" valign="middle" class="datacell">
		        				<c:out value="${preqHistory.purapDocumentIdentifier}" />
			        		</td>
			        		<td align="left" valign="middle" class="datacell">
		        				<c:out value="${preqHistory.invoiceNumber}" />
			        		</td>
			        		<td align="left" valign="middle" class="datacell">
		        				<c:out value="${preqHistory.purchaseOrderIdentifier}" />
			        		</td>
		        			<td align="left" valign="middle" class="datacell">
		        				<c:out value="${preqHistory.applicationDocumentStatus}" />
			        		</td>
			        		<td align="left" valign="middle" class="datacell">
			        			<c:choose>
			        				<c:when test="${preqHistory.paymentHoldIndicator == true}">Yes</c:when>
		        					<c:otherwise>No</c:otherwise>
		        				</c:choose>
			        		</td>
			        		<td align="left" valign="middle" class="datacell">
			        			<c:choose>
			        				<c:when test="${preqHistory.paymentRequestedCancelIndicator == true}">Yes</c:when>
			        				<c:otherwise>No</c:otherwise>
			        			</c:choose>
			        		</td>
		        			<td align="left" valign="middle" class="datacell">
		        				<c:out value="${preqHistory.vendorName}" />
			        		</td>
			        		<td align="left" valign="middle" class="datacell">
		        				<c:out value="${preqHistory.vendorCustomerNumber}" />
			        		</td>
			        		<td align="left" valign="middle" class="datacell">
			        			<c:out value="${preqHistory.totalAmount}" />
			        		</td>
		        			<td align="left" valign="middle" class="datacell">
		        				<c:out value="${preqHistory.paymentRequestPayDate}"/>
			        		</td>
			        		<td align="left" valign="middle" class="datacell">
			        		<c:out value="${preqHistory.paymentExtractedTimestamp}" />
		        			<c:if test="${not empty preqHistory.paymentExtractedTimestamp}">
		        			  <purap:disbursementInfo sourceDocumentNumber="${preqHistory.documentNumber}" sourceDocumentType="${preqHistory.documentType}" />
		        			</c:if>
			        		</td>
			        		<td align="left" valign="middle" class="datacell">
			        			<c:choose>
			        				<c:when test="${not empty preqHistory.paymentPaidTimestamp}">Yes</c:when>
		        					<c:otherwise>No</c:otherwise>
		        				</c:choose>
			        		</td>
			        	</tr>
			        </c:if>
		       	</logic:iterate>
	    	</table>
		    <br />
		    <br />
		</logic:notEmpty>
        <logic:empty name="KualiForm" property="document.relatedViews.paymentHistoryPaymentRequestViews">
	        <h4>No Payment Requests</h4>
        </logic:empty>

            <h3>Payment History	- Credit Memos</h3>
		<br />

	   	<logic:notEmpty name="KualiForm" property="document.relatedViews.paymentHistoryCreditMemoViews">
		    <table cellpadding="0" cellspacing="0" class="datatable" summary="Payment History">
				<tr>
					<kul:htmlAttributeHeaderCell scope="col">CM #</kul:htmlAttributeHeaderCell>
					<kul:htmlAttributeHeaderCell scope="col">Vendor CM #</kul:htmlAttributeHeaderCell>
					<kul:htmlAttributeHeaderCell scope="col">PREQ #</kul:htmlAttributeHeaderCell>
					<kul:htmlAttributeHeaderCell scope="col">PO #</kul:htmlAttributeHeaderCell>
					<kul:htmlAttributeHeaderCell scope="col">Credit Memo Status</kul:htmlAttributeHeaderCell>
					<kul:htmlAttributeHeaderCell scope="col">Hold</kul:htmlAttributeHeaderCell>
					<kul:htmlAttributeHeaderCell scope="col">Vendor Name</kul:htmlAttributeHeaderCell>
					<kul:htmlAttributeHeaderCell scope="col">Customer #</kul:htmlAttributeHeaderCell>
					<kul:htmlAttributeHeaderCell scope="col">Amount</kul:htmlAttributeHeaderCell>
					<kul:htmlAttributeHeaderCell scope="col">APAD Date</kul:htmlAttributeHeaderCell>
					<kul:htmlAttributeHeaderCell scope="col">PDP Extract Date</kul:htmlAttributeHeaderCell>
					<kul:htmlAttributeHeaderCell scope="col">Paid?</kul:htmlAttributeHeaderCell>
	        	</tr>
				<logic:iterate id="cmHistory" name="KualiForm" property="document.relatedViews.paymentHistoryCreditMemoViews" indexId="ctr">
					<c:if test="${(empty limitByPoId) or (limitByPoId eq cmHistory.purchaseOrderIdentifier)}">
		        		<tr>
		        			<td align="left" valign="middle" class="datacell">
		        				<c:out value="${cmHistory.purapDocumentIdentifier}" />
			        		</td>
			        		<td align="left" valign="middle" class="datacell">
		        				<c:out value="${cmHistory.creditMemoNumber}" />
			        		</td>
			        		<td align="left" valign="middle" class="datacell">
		        				<c:out value="${cmHistory.paymentRequestIdentifier}" />
			        		</td>
		        			<td align="left" valign="middle" class="datacell">
		        				<c:out value="${cmHistory.purchaseOrderIdentifier}" />
			        		</td>
			        		<td align="left" valign="middle" class="datacell">
		        				<c:out value="${cmHistory.applicationDocumentStatus}" />
			        		</td>
			        		<td align="left" valign="middle" class="datacell">
			        			<c:choose>
			        				<c:when test="${cmHistory.creditHoldIndicator == true}">Yes</c:when>
			        				<c:otherwise>No</c:otherwise>
			        			</c:choose>
			        		</td>
		        			<td align="left" valign="middle" class="datacell">
		        				<c:out value="${cmHistory.vendorName}" />
			        		</td>
			        		<td align="left" valign="middle" class="datacell">
		        				<c:out value="${cmHistory.vendorCustomerNumber}" />
			        		</td>
			        		<td align="left" valign="middle" class="datacell">
		        				<c:out value="${cmHistory.totalAmount}" />
			        		</td>
		        			<td align="left" valign="middle" class="datacell">
		        				<c:out value="${cmHistory.accountsPayableApprovalTimestamp}" />
			        		</td>
			        		<td align="left" valign="middle" class="datacell">
		        				<c:out value="${cmHistory.creditMemoExtractedTimestamp}" />
	                			<c:if test="${not empty cmHistory.creditMemoExtractedTimestamp}">
	                			  <purap:disbursementInfo sourceDocumentNumber="${cmHistory.documentNumber}" sourceDocumentType="${cmHistory.documentType}" />
		        			    </c:if>
			        		</td>
			        		<td align="left" valign="middle" class="datacell">
								<c:choose>
									<c:when test="${not empty cmHistory.creditMemoPaidTimestamp}">Yes</c:when>
									<c:otherwise>No</c:otherwise>
								</c:choose>
			        		</td>
			        	</tr>
			        </c:if>
		       	</logic:iterate>
	    	</table>
		    <br />
		    <br />
		</logic:notEmpty>
        <logic:empty name="KualiForm" property="document.relatedViews.paymentHistoryCreditMemoViews">
	        <h4>No Credit Memos</h4>
        </logic:empty>
		</c:if>
    </div>
</purap:loadOnOpenTab>

