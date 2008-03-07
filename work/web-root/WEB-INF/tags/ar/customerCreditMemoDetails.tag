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

<%@ attribute name="documentAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for customer credit memo document fields." %>
              
<%@ attribute name="customerCreditMemoDetailAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for customer credit memo detail fields." %>
              
<%@ attribute name="readOnly" required="true" description="If document is in read only mode" %>         
              
<kul:tab tabTitle="Credit Memo Details" defaultOpen="true" tabErrorKey="${KFSConstants.CUSTOMER_INVOICE_DETAIL_ERRORS}">
    <div class="tab-container" align=center>		
        <table cellpadding="0" cellspacing="0" class="datatable" summary="Credit Memo Details">
            <tr>
                <td colspan="11" class="subhead">Credit Memo Details</td>
            </tr>
			<tr>
			    <kul:htmlAttributeHeaderCell literalLabel="&nbsp;"/>
			    <kul:htmlAttributeHeaderCell attributeEntry="${customerCreditMemoDetailAttributes.invoiceItemQuantity}" />
			    <kul:htmlAttributeHeaderCell attributeEntry="${customerInvoiceDetailAttributes.invoiceItemCode}" />
			    <kul:htmlAttributeHeaderCell attributeEntry="${customerInvoiceDetailAttributes.invoiceItemUnitOfMeasureCode}" />
			    <kul:htmlAttributeHeaderCell attributeEntry="${customerInvoiceDetailAttributes.invoiceItemDescription}" />
			    <kul:htmlAttributeHeaderCell attributeEntry="${customerInvoiceDetailAttributes.invoiceItemUnitPrice}" />
			    <kul:htmlAttributeHeaderCell attributeEntry="${customerInvoiceDetailAttributes.amount}" />
			    <kul:htmlAttributeHeaderCell attributeEntry="${customerInvoiceDetailAttributes.invoiceItemServiceDate}" />
			    <kul:htmlAttributeHeaderCell attributeEntry="${customerInvoiceDetailAttributes.accountsReceivableObjectCode}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${customerInvoiceDetailAttributes.accountsReceivableSubObjectCode}" />	
				<c:if test="${not readOnly}">			
			    	<kul:htmlAttributeHeaderCell literalLabel="Actions" />
			    </c:if>
			</tr>     
			<c:if test="${not readOnly}">
				<ar:customerInvoiceDetail propertyName="newCustomerInvoiceDetail" customerInvoiceDetailAttributes="${customerInvoiceDetailAttributes}" readOnly="${readOnly}" rowHeading="add" cssClass="datacell" actionMethod="addCustomerInvoiceDetail"  actionAlt="Add Customer Invoice Detail" actionImage="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" />				
			</c:if>     
			<logic:iterate id="customerInvoiceDetail" name="KualiForm" property="document.customerInvoiceDetails" indexId="ctr">
				<ar:customerInvoiceDetail propertyName="document.customerInvoiceDetail[${ctr}]" customerInvoiceDetailAttributes="${customerInvoiceDetailAttributes}" readOnly="${readOnly}" rowHeading="${ctr+1}" cssClass="datacell" actionMethod="deleteCustomerInvoiceDetail.line${ctr}" actionAlt="Delete Customer Invoice Detail" actionImage="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" />
	        </logic:iterate>  
    	    </table>
    </div>
</kul:tab>