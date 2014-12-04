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
              description="The DataDictionary entry containing attributes for this row's fields." %>
              
<c:set var="fullEntryMode" value="${KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />
<c:set var="fullDocumentEntryCompleted" value="${not empty KualiForm.editingMode['fullDocumentEntryCompleted']}" />
<c:set var="purchaseOrderAttributes" value="${DataDictionary.PurchaseOrderDocument.attributes}" />

<kul:tab tabTitle="Credit Memo Info" defaultOpen="true">
   
    <div class="tab-container" align=center>
            <h3>Credit Memo Info</h3>

        <table cellpadding="0" cellspacing="0" class="datatable" summary="Credit Memo Info Section">

            <tr>
                <th align=right valign=middle class="bord-l-b" width="25%">
                   <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.creditMemoNumber}" /></div>
                </th>
                <td align=left valign=middle class="datacell" width="25%">
                   <kul:htmlControlAttribute attributeEntry="${documentAttributes.creditMemoNumber}" property="document.creditMemoNumber" readOnly="true" /> 
                </td>
                <th align=right valign=middle class="bord-l-b" width="25%">
                   <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.creditMemoType}" /></div>
                </th>
                <td align=left valign=middle class="datacell" width="25%">
                   <bean:write name="KualiForm" property="document.creditMemoType" />
                </td>
            </tr>
            
            <tr>
                <th align=right valign=middle class="bord-l-b">
                   <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.creditMemoDate}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                   <kul:htmlControlAttribute attributeEntry="${documentAttributes.creditMemoDate}" property="document.creditMemoDate" readOnly="true" />
                </td>
                <th align=right valign=middle class="bord-l-b">
                   <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorNumber}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                   <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorNumber}" property="document.vendorNumber" readOnly="true" />
                </td>
             </tr>
             
             <c:if test="${not fullDocumentEntryCompleted}">
                  <tr>
                     <th align=right valign=middle class="bord-l-b">                   
        	    		<div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.creditMemoAmount}" useShortLabel="true" /></div>
                     </th>
                     <td align=left valign=middle class="datacell">                   
                     	<kul:htmlControlAttribute attributeEntry="${documentAttributes.creditMemoAmount}" property="document.creditMemoAmount" readOnly="true" />
                     </td>
                     <th align=right valign=middle class="bord-l-b">&nbsp;</th>               
                     <td align=left valign=middle class="datacell">&nbsp;</td>                
                  <tr>   
             </c:if>

             <tr>   
                <th align=right valign=middle class="bord-l-b">
                   <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.purchaseOrderEndDate}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                   <kul:htmlControlAttribute  attributeEntry="${documentAttributes.purchaseOrderEndDate}" property="document.purchaseOrder.purchaseOrderEndDate" readOnly="true" />
                </td>
                <th align=right valign=middle class="bord-l-b">
                   <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.purchaseOrderIdentifier}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                   <kul:htmlControlAttribute attributeEntry="${documentAttributes.purchaseOrderIdentifier}" property="document.purchaseOrderIdentifier" readOnly="true" />
                </td>
             </tr>
             
             <tr>   
                <th align=right valign=middle class="bord-l-b">
                   <div align="right"><kul:htmlAttributeLabel  attributeEntry="${documentAttributes.purchaseOrderNotes}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                   <bean:write name="KualiForm" property="document.purchaseOrderNotes" />
                </td>
                <th align=right valign=middle class="bord-l-b">
                   <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.paymentRequestIdentifier}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                   <kul:htmlControlAttribute attributeEntry="${documentAttributes.paymentRequestIdentifier}" property="document.paymentRequestIdentifier" readOnly="true" />
                </td>
            </tr>
			<tr>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.extractedTimestamp}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.extractedTimestamp}" property="document.extractedTimestamp" readOnly="${true}" />
                    <c:if test="${not empty KualiForm.document.extractedTimestamp}">
                        <purap:disbursementInfo sourceDocumentNumber="${KualiForm.document.documentNumber}" sourceDocumentType="${KualiForm.document.documentType}" />          
					</c:if>
                </td>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.accountsPayableApprovalTimestamp}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.accountsPayableApprovalTimestamp}" property="document.accountsPayableApprovalTimestamp" readOnly="${not displayInitTab}" />
                </td>          
            </tr>

			<tr>
	            <sys:bankLabel align="right"/>
                <sys:bankControl property="document.bankCode" objectProperty="document.bank" readOnly="${not fullEntryMode}"/>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right">&nbsp;</div>
                </th>
                <td align=left valign=middle class="datacell">
                    &nbsp;
                </td>
                
            </tr>
            
		</table> 
    </div>
    
</kul:tab>
