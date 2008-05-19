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
              description="The DataDictionary entry containing attributes for this row's fields." %>
              
<c:set var="customerAddressAttributes" value="${DataDictionary.CustomerAddress.attributes}" />              

<%@ attribute name="editingMode" required="true" description="used to decide editability of overview fields" type="java.util.Map"%>
<c:set var="readOnly" value="${empty editingMode['fullEntry']}" />

<%-- hidden attribute for document number since it isn't displayed--%>
<html:hidden property="document.accountsReceivableDocumentHeader.documentNumber" />

<kul:tab tabTitle="Billing/Shipping" defaultOpen="true" tabErrorKey="${KFSConstants.CUSTOMER_INVOICE_DOCUMENT_ADDRESS}">
    <div class="tab-container" align=center>	
        <table cellpadding="0" cellspacing="0" class="datatable" summary="Invoice Section">
            <ar:customerInvoiceAddress 
            	customerAddressIdentifierAttributeEntry="${documentAttributes.customerBillToAddressIdentifier}"
            	customerAddressIdentifierProperty="document.customerBillToAddressIdentifier"
            	customerAddressObject="document.customerBillToAddress"
            	refreshAction="refresh"
            	editingMode="${editingMode}"
            	subTitle="Bill To Address"
            	lookupFieldConversion="customerAddressIdentifier:document.customerBillToAddressIdentifier" />   
            <ar:customerInvoiceAddress 
            	customerAddressIdentifierAttributeEntry="${documentAttributes.customerShipToAddressIdentifier}"
            	customerAddressIdentifierProperty="document.customerShipToAddressIdentifier"
            	customerAddressObject="document.customerShipToAddress"
            	refreshAction="refresh"
            	editingMode="${editingMode}"
            	subTitle="Ship To Address"
            	lookupFieldConversion="customerAddressIdentifier:document.customerShipToAddressIdentifier" />             	        	
        </table>
    </div>
</kul:tab>