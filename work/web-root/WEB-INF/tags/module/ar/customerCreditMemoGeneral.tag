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

<script language="JavaScript" type="text/javascript" src="scripts/module/ar/customerObjectInfo.js"></script>
 
<c:set var="documentAttributes" value="${DataDictionary.CustomerCreditMemoDocument.attributes}" />             
<c:set var="customerAttributes" value="${DataDictionary.Customer.attributes}" />

<kul:tab tabTitle="General" defaultOpen="true" >
    <div class="tab-container" align=center>
       	<h3>General</h3>
        <table cellpadding="0" cellspacing="0" class="datatable" summary="General Section">

        	<tr>
        		<th align=right valign=middle class="bord-l-b">
        			<div align="right">
        				<kul:htmlAttributeLabel attributeEntry="${documentAttributes.financialDocumentReferenceInvoiceNumber}" />
        			</div>
        		</th>
        		<td>
					<a href="${ConfigProperties.workflow.url}/DocHandler.do?docId=${KualiForm.document.financialDocumentReferenceInvoiceNumber}&command=displayDocSearchView" target="blank">
        			<kul:htmlControlAttribute attributeEntry="${documentAttributes.financialDocumentReferenceInvoiceNumber}"
                      	  property="document.financialDocumentReferenceInvoiceNumber"
                          readOnly="true" />	
					</a>
        		</td>
        	</tr>
        	<tr>
        		<th align=right valign=middle class="bord-l-b">
        			<div align="right">
        				<kul:htmlAttributeLabel attributeEntry="${customerAttributes.customerNumber}" />
        			</div>
        		</th>
        		<td>
        			<kul:htmlControlAttribute attributeEntry="${customerAttributes.customerNumber}"
                      	  property="document.invoice.accountsReceivableDocumentHeader.customerNumber"
                          readOnly="true" />
        		</td>
        	</tr>
        	
        	<tr>
        		<th align=right valign=middle class="bord-l-b">
        			<div align="right">
        				<kul:htmlAttributeLabel attributeEntry="${customerAttributes.customerName}" />
        			</div>
            	</th>
        		<td>
        			<kul:htmlControlAttribute attributeEntry="${customerAttributes.customerNumber}"
                      	property="document.invoice.accountsReceivableDocumentHeader.customer.customerName"
                      	readOnly="true" />
        		</td>
        	</tr>
        	
			<tr>
				<th align=right valign=middle class="bord-l-b">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${DataDictionary.CustomerInvoiceDocument.attributes.billingDateForDisplay}" />
					</div>
				</th>
				<td>
					<kul:htmlControlAttribute attributeEntry="${DataDictionary.CustomerInvoiceDocument.attributes.billingDateForDisplay}"
                       	property="document.invoice.billingDateForDisplay"
                       	readOnly="true" />
				</td>
			</tr>
			
            <tr>
				<th align=right valign=middle class="bord-l-b">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${documentAttributes.invOutstandingDays}" />
					</div>
				</th>
				<td style="width: 50%;">
					<kul:htmlControlAttribute attributeEntry="${documentAttributes.invOutstandingDays}"
                       	property="document.invOutstandingDays"
                       	readOnly="true" />
				</td>
            <%--
        		<kul:htmlAttributeHeaderCell align="right" literalLabel="Invoice Outstanding Days:" scope="row" />
        		<!--  service should be called here customerShipToAddressIdentifier-->
        		<td align=left valign=middle class="datacell" style="width: 50%;"></td>
        	--%>
        	</tr>
		</table>
	</div>
</kul:tab>
