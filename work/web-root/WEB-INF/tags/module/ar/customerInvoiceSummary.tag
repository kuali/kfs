<%--
 Copyright 2006-2009 The Kuali Foundation
 
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
