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

<script type='text/javascript' src="dwr/interface/CustomerService.js"></script>
<script language="JavaScript" type="text/javascript" src="scripts/ar/customerObjectInfo.js"></script>
 
<c:set var="documentAttributes" value="${DataDictionary.CustomerCreditMemoDocument.attributes}" />             
<c:set var="customerAttributes" value="${DataDictionary.Customer.attributes}" />
              
<kul:tab tabTitle="General" defaultOpen="false" tabErrorKey="${KFSConstants.CUSTOMER_INVOICE_DOCUMENT_GENERAL_ERRORS}">
    <div class="tab-container" align=center>
    	<div class="h2-container">
        	<h2>General</h2>
        </div>	
        <table cellpadding="0" cellspacing="0" class="datatable" summary="General Section">

        	<tr>
        		<th align=right valign=middle class="bord-l-b">
        			<div align="right">
        				<kul:htmlAttributeLabel attributeEntry="${documentAttributes.financialDocumentReferenceInvoiceNumber}" />
        			</div>
        		</th>
        		<td>
        			<kul:htmlControlAttribute attributeEntry="${documentAttributes.financialDocumentReferenceInvoiceNumber}"
        			                      	  property="document.financialDocumentReferenceInvoiceNumber"
        			                          readOnly="true" />	
        		</td>
        	</tr>
        
        	<tr>
        		<th align=right valign=middle class="bord-l-b">
        			<div align="right">
        				<kul:htmlAttributeLabel attributeEntry="${customerAttributes.customerNumber}" />
        			</div>
        		</th>
        		<td>
        			<kul:htmlControlAttribute attributeEntry="${documentAttributes.invoice.accountsReceivableDocumentHeader.customerNumber}"
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
        			<kul:htmlControlAttribute attributeEntry="${documentAttributes.invoice.customerSpecialProcessing.customerSpecialProcessingDescription}"
        			                      	property="document.invoice.customerSpecialProcessing.customerSpecialProcessingDescription"
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
					<kul:htmlControlAttribute attributeEntry="${documentAttributes.invoice.billingDate}"
				                          	property="document.invoice.billingDateForDisplay"
				                          	readOnly="true" />
				</td>
			</tr>
			
            <tr>
        		<kul:htmlAttributeHeaderCell align="right" literalLabel="Invoice Outstanding Days:" scope="row" />
        		<!--  service should be called here -->
        		<td align=left valign=middle class="datacell" style="width: 50%;"></td>
        	</tr>
        	
		</table>
	</div>
</kul:tab>
