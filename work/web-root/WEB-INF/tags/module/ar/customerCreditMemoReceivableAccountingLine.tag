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
