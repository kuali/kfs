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

<%@ attribute name="customerInvoiceDocumentAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for this row's fields." %>
<%@ attribute name="documentAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for this row's fields." %>
<%@ attribute name="customerAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for this row's fields." %>
<%@ attribute name="customerAddressAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for this row's fields." %>

<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />
              
<kul:tab tabTitle="Customer Invoice Summary" defaultOpen="true" tabErrorKey="document.customerNote">
    <div class="tab-container" align=center>	
        <table cellpadding="0" cellspacing="0" class="datatable" summary="Customer Invoice Information">
            <tr>
                <td colspan="4" class="subhead">Invoice Information</td>
            </tr>
			<tr>
				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.financialDocumentReferenceInvoiceNumber}" forceRequired="true" labelFor="document.financialDocumentReferenceInvoiceNumber" /></div>
                </th>
                <td align=left valign=middle class="datacell" style="width: 25%;">
					<a href="${ConfigProperties.workflow.url}/DocHandler.do?docId=${KualiForm.document.financialDocumentReferenceInvoiceNumber}&command=displayDocSearchView" target="blank">
                	<kul:htmlControlAttribute attributeEntry="${documentAttributes.financialDocumentReferenceInvoiceNumber}" property="document.financialDocumentReferenceInvoiceNumber" readOnly="true" forceRequired="true"/>
					</a>
                </td>			
                <th align=right valign=middle class="bord-l-b" style="width: 25%;"> 
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${customerInvoiceDocumentAttributes.openAmount}" /></div>
                </th>
                <td align=left valign=middle class="datacell" style="width: 25%;">
					<kul:htmlControlAttribute attributeEntry="${customerInvoiceDocumentAttributes.openAmount}" property="document.customerInvoiceDocument.openAmount" readOnly="true"/>
				</td>          
            </tr>
			<tr>
                <td colspan="4" class="subhead">Customer Information</td>
            </tr>
			<tr>
				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${customerAttributes.customerNumber}" /></div>
                </th>
                <td align=left valign=middle class="datacell" style="width: 25%;">
                	<kul:htmlControlAttribute attributeEntry="${customerAttributes.customerNumber}" property="document.customerInvoiceDocument.customer.customerNumber" readOnly="true"/>
                </td>			
                <th align=right valign=middle class="bord-l-b" style="width: 25%;"> 
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${customerAttributes.customerName}" /></div>
                </th>
                <td align=left valign=middle class="datacell" style="width: 25%;">
					<kul:htmlControlAttribute attributeEntry="${customerAttributes.customerName}" property="document.customerInvoiceDocument.customer.customerName" readOnly="true"/>
				</td> 
			</tr>
			<tr>

				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${customerAddressAttributes.customerLine1StreetAddress}" /></div>
                </th>
                <td align=left valign=middle class="datacell" style="width: 25%;">
                	<kul:htmlControlAttribute attributeEntry="${customerAddressAttributes.customerLine1StreetAddress}" property="document.customerInvoiceDocument.primaryAddressForCustomerNumber.customerLine1StreetAddress" readOnly="true"/>
                </td>			
    				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${customerAddressAttributes.customerCityName}" /></div>
                </th>
                <td align=left valign=middle class="datacell" style="width: 25%;">
                	<kul:htmlControlAttribute attributeEntry="${customerAddressAttributes.customerCityName}" property="document.customerInvoiceDocument.primaryAddressForCustomerNumber.customerCityName" readOnly="true"/>
                </td>
			</tr>
			<tr>

				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${customerAddressAttributes.customerStateCode}" /></div>
                </th>
                <td align=left valign=middle class="datacell" style="width: 25%;">
                	<kul:htmlControlAttribute attributeEntry="${customerAddressAttributes.customerStateCode}" property="document.customerInvoiceDocument.primaryAddressForCustomerNumber.customerStateCode" readOnly="true"/>
                </td>			
    				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${customerAddressAttributes.customerZipCode}" /></div>
                </th>
                <td align=left valign=middle class="datacell" style="width: 25%;">
                	<kul:htmlControlAttribute attributeEntry="${customerAddressAttributes.customerZipCode}" property="document.customerInvoiceDocument.primaryAddressForCustomerNumber.customerZipCode" readOnly="true"/>
                </td>           
            </tr>
            <tr>
            	<th align=right valign=middle class="bord-l-b" style="width: 25%;">
					<div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.customerNote}" /></div>
				</th>
				<td align=left valign=middle class="datacell">
					<kul:htmlControlAttribute attributeEntry="${documentAttributes.customerNote}" property="document.customerNote" readOnly="${readOnly}" forceRequired="true"/>
				</td>
				<td colspan="2"/>
            </tr>         
        </table>
    </div>
</kul:tab>
