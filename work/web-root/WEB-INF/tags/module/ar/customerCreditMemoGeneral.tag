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
