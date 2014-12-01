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
 
<c:set var="documentAttributes" value="${DataDictionary.CustomerCreditMemoDocument.attributes}" />          
<c:set var="customerInvoiceAttributes" value="${DataDictionary.CustomerInvoiceDocument.attributes}" />    
                      
<kul:tab tabTitle="Receivable" defaultOpen="false" >
    <div class="tab-container" align=center>	
        <table cellpadding="0" cellspacing="0" class="datatable" summary="Invoice Section">
            <tr>
                <td colspan="7" class="subhead">Receivable</td>
            </tr>               
            <tr>
			    <kul:htmlAttributeHeaderCell attributeEntry="${customerInvoiceAttributes.paymentChartOfAccountsCode}"/>
			    <kul:htmlAttributeHeaderCell attributeEntry="${customerInvoiceAttributes.paymentAccountNumber}"/>
			    <kul:htmlAttributeHeaderCell attributeEntry="${customerInvoiceAttributes.paymentSubAccountNumber}" />
			    <kul:htmlAttributeHeaderCell attributeEntry="${customerInvoiceAttributes.paymentFinancialObjectCode}"/>
			    <kul:htmlAttributeHeaderCell attributeEntry="${customerInvoiceAttributes.paymentFinancialSubObjectCode}" />
			    <kul:htmlAttributeHeaderCell attributeEntry="${customerInvoiceAttributes.paymentProjectCode}" />
			    <kul:htmlAttributeHeaderCell attributeEntry="${customerInvoiceAttributes.paymentOrganizationReferenceIdentifier}" />
			              
            </tr>
            <tr>
            	<td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.invoice.paymentChartOfAccountsCode}" property="document.invoice.paymentChartOfAccountsCode" readOnly="true"/>
                </td> 
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.invoice.paymentAccountNumber}" property="document.invoice.paymentAccountNumber" readOnly="true"/>
                </td>    
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.invoice.paymentSubAccountNumber}" property="document.invoice.paymentSubAccountNumber" readOnly="true"/>
                </td>
				<td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.invoice.paymentFinancialObjectCode}" property="document.invoice.paymentFinancialObjectCode" readOnly="true"/>
                </td>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.invoice.paymentFinancialSubObjectCode}" property="document.invoice.paymentFinancialSubObjectCode" readOnly="true"/>
                </td>
				<td align=left valign=middle class="datacell">
					<kul:htmlControlAttribute attributeEntry="${documentAttributes.invoice.paymentProjectCode}" property="document.invoice.paymentProjectCode" readOnly="true"/>
                </td>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.invoice.paymentOrganizationReferenceIdentifier}" property="document.invoice.paymentOrganizationReferenceIdentifier" readOnly="true"/>
                </td>                
           </tr>
        </table>
    </div>
</kul:tab>
