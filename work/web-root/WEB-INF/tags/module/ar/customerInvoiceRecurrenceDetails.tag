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

<script type='text/javascript' src="dwr/interface/CustomerService.js"></script>
<script language="JavaScript" type="text/javascript" src="scripts/module/ar/customerObjectInfo.js"></script>

<%@ attribute name="documentAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for this row's fields." %>
              
<%@ attribute name="readOnly" required="true" description="If document is in read only mode"%>

<c:set var="customerInvoiceRecurrenceAttributes" value="${DataDictionary.CustomerInvoiceRecurrenceDetails.attributes}" />
<c:set var="tabindexOverrideBase" value="20" />

<kul:tab tabTitle="Recurrence Details" defaultOpen="true" tabErrorKey="${KFSConstants.CUSTOMER_INVOICE_DOCUMENT_RECURRENCE_DETAILS_ERRORS}">
    <div class="tab-container" align=center>	
        <table cellpadding="0" cellspacing="0" class="datatable" summary="Invoice Section">
            <tr>
                <td colspan="4" class="subhead">Recurrence Details</td>
            </tr>
			<tr>
                <th align=right valign=middle class="bord-l-b" style="width: 25%;"> 
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${customerInvoiceRecurrenceAttributes.documentRecurrenceIntervalCode}" /></div>
                </th>
                <td align=left valign=middle class="datacell" style="width: 25%;">
                    <kul:htmlControlAttribute attributeEntry="${customerInvoiceRecurrenceAttributes.documentRecurrenceIntervalCode}" 
                       property="document.customerInvoiceRecurrenceDetails.documentRecurrenceIntervalCode" 
                       tabindexOverride="${tabindexOverrideBase}"
                       readOnly="${readOnly}" />
                </td>          
				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${customerInvoiceRecurrenceAttributes.documentTotalRecurrenceNumber}" /></div>
                </th>
                <td align=left valign=middle class="datacell" style="width: 25%;">
                    <kul:htmlControlAttribute 
                       attributeEntry="${customerInvoiceRecurrenceAttributes.documentTotalRecurrenceNumber}" 
                       property="document.customerInvoiceRecurrenceDetails.documentTotalRecurrenceNumber" 
                       tabindexOverride="${tabindexOverrideBase} + 5"
                       readOnly="${readOnly}" />
                </td>			
            </tr>
			<tr>
				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${customerInvoiceRecurrenceAttributes.documentRecurrenceBeginDate}" /></div>
                </th>
                <td align=left valign=middle class="datacell" style="width: 25%;">
     		       	<c:choose>
			            <c:when test="${readOnly}">
			                <kul:htmlControlAttribute attributeEntry="${customerInvoiceRecurrenceAttributes.documentRecurrenceBeginDate}" 
			                	property="document.customerInvoiceRecurrenceDetails.documentRecurrenceBeginDate"
			                	tabindexOverride="${tabindexOverrideBase} + 10"
			                 	readOnly="${readOnly}" />
			            </c:when>
                        <c:otherwise>
		                    <kul:dateInput attributeEntry="${customerInvoiceRecurrenceAttributes.documentRecurrenceBeginDate}"
                       			property="document.customerInvoiceRecurrenceDetails.documentRecurrenceBeginDate" tabindexOverride="${tabindexOverrideBase} + 15" />
			            </c:otherwise>
					</c:choose>
                </td>			          
            </tr>    
			<tr>
				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${customerInvoiceRecurrenceAttributes.documentRecurrenceEndDate}" /></div>
                </th>
                <td align=left valign=middle class="datacell" style="width: 25%;">
     		       	<c:choose>
			            <c:when test="${readOnly}">
			                <kul:htmlControlAttribute attributeEntry="${customerInvoiceRecurrenceAttributes.documentRecurrenceEndDate}" 
			                	property="document.customerInvoiceRecurrenceDetails.documentRecurrenceEndDate" 
			                	tabindexOverride="${tabindexOverrideBase} + 20"
			                	readOnly="${readOnly}" />
			            </c:when>
                        <c:otherwise>
		                    <kul:dateInput attributeEntry="${customerInvoiceRecurrenceAttributes.documentRecurrenceEndDate}"
                       			property="document.customerInvoiceRecurrenceDetails.documentRecurrenceEndDate" tabindexOverride="${tabindexOverrideBase} + 25" />
			            </c:otherwise>
					</c:choose>
                </td>			
				<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${customerInvoiceRecurrenceAttributes.documentInitiatorUserIdentifier}" /></th>
				<td class="grid" width="25%">
				<kul:checkErrors keyMatch="document.customerInvoiceRecurrenceDetails.documentInitiatorUser.principalName" />
				<kul:user userIdFieldName="document.customerInvoiceRecurrenceDetails.documentInitiatorUser.principalName" universalIdFieldName="document.customerInvoiceRecurrenceDetails.documentInitiatorUserIdentifier" userNameFieldName="document.customerInvoiceRecurrenceDetails.documentInitiatorUser.name" label="User" 
				lookupParameters="document.customerInvoiceRecurrenceDetails.documentInitiatorUser.principalName:principalName,document.customerInvoiceRecurrenceDetails.documentInitiatorUserIdentifier:principalId,document.customerInvoiceRecurrenceDetails.documentInitiatorUser.name:name" 
				fieldConversions="principalName:document.customerInvoiceRecurrenceDetails.documentInitiatorUser.principalName,principalId:document.customerInvoiceRecurrenceDetails.documentInitiatorUserIdentifier,name:document.customerInvoiceRecurrenceDetails.documentInitiatorUser.name" 
				tabIndex="${tabindexOverrideBase} + 30"
				userId="${KualiForm.document.customerInvoiceRecurrenceDetails.documentInitiatorUser.principalName}" universalId="${KualiForm.document.customerInvoiceRecurrenceDetails.documentInitiatorUserIdentifier}" userName="${KualiForm.document.customerInvoiceRecurrenceDetails.documentInitiatorUser.name}" hasErrors="${hasErrors}" readOnly="${readOnly}" />
				</td>
            </tr>    
                <th align=right valign=middle class="bord-l-b" style="width: 25%;"> 
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${customerInvoiceRecurrenceAttributes.active}" /></div>
                </th>
                <td align=left valign=middle class="datacell" style="width: 25%;">
                    <kul:htmlControlAttribute attributeEntry="${customerInvoiceRecurrenceAttributes.active}" 
                       property="document.customerInvoiceRecurrenceDetails.active"
                       tabindexOverride="${tabindexOverrideBase} + 30"
                       readOnly="${readOnly}"/>
                </td>
				<th align=right valign=middle class="bord-l-b">
                    &nbsp;
                </th>
                <td align=left valign=middle class="datacell">
                    &nbsp;
                </td> 
            </tr>
        </table>
    </div>
</kul:tab>

