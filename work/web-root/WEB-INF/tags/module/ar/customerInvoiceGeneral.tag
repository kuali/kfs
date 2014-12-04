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
<script type='text/javascript' src="dwr/interface/CustomerAddressService.js"></script>
<script language="JavaScript" type="text/javascript" src="scripts/module/ar/customerAddressObjectInfo.js"></script>

<%@ attribute name="documentAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for this row's fields." %>
              
<%@ attribute name="readOnly" required="true" description="used to decide editability of overview fields" %>

<c:set var="arDocHeaderAttributes" value="${DataDictionary.AccountsReceivableDocumentHeader.attributes}" />
<c:set var="tabindexOverrideBase" value="100" />

<kul:tab tabTitle="General" defaultOpen="true" tabErrorKey="${KFSConstants.CUSTOMER_INVOICE_DOCUMENT_GENERAL_ERRORS}">
    <div class="tab-container" align=center>	
        <table cellpadding="0" cellspacing="0" class="datatable" summary="Invoice Section">
            <tr>
                <td colspan="4" class="subhead">Customer Information</td>
            </tr>
			<tr>
				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${arDocHeaderAttributes.customerNumber}" labelFor="document.accountsReceivableDocumentHeader.customerNumber" forceRequired="true" /></div>
                </th>
                <td align=left valign=middle class="datacell" style="width: 25%;">
                
				    <c:choose>
				        <c:when test="${!readOnly}">
				            <c:set var="onblurForCustomer" value="loadCustomerInfo( this.name, 'document.accountsReceivableDocumentHeader.customer.customerName'); loadCustomerAddressInfo( this.name );"/>
				        </c:when>
				        <c:otherwise>
				            <c:set var="onblurForCustomer" value=""/>
				        </c:otherwise>
				    </c:choose>
				    
				    <c:if test="${readOnly}">
					 <a href="arCustomerOpenItemReportLookup.do?methodToCall=search&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.CustomerOpenItemReportDetail&lookupableImplementaionServiceName=arCustomerOpenItemReportLookupable&docFormKey=88888888&returnLocation=&hideReturnLink=true&reportName=${KFSConstants.CustomerOpenItemReport.HISTORY_REPORT_NAME}&customerNumber=${KualiForm.customerInvoiceDocument.accountsReceivableDocumentHeader.customerNumber}&customerName=${KualiForm.customerInvoiceDocument.customerName}"
						target="new">
					</c:if>
					
				    <kul:htmlControlAttribute attributeEntry="${arDocHeaderAttributes.customerNumber}" 
				    	property="document.accountsReceivableDocumentHeader.customerNumber" 
				    	tabindexOverride="${tabindexOverrideBase}"
				    	readOnly="${readOnly}" onblur="${onblurForCustomer}" onchange="${onblurForCustomer}" forceRequired="true"/></a>
                    
                    <c:if test="${not readOnly}">
	                    &nbsp;
	                    <kul:lookup boClassName="org.kuali.kfs.module.ar.businessobject.Customer" fieldConversions="customerNumber:document.accountsReceivableDocumentHeader.customerNumber" lookupParameters="document.accountsReceivableDocumentHeader.customerNumber:customerNumber" />
                    </c:if>
                </td>			
                <th align=right valign=middle class="bord-l-b" style="width: 25%;"> 
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${arDocHeaderAttributes.customerName}" labelFor="document.accountsReceivableDocumentHeader.customer.customerName" /></div>
                </th>
                <td align=left valign=middle class="datacell" style="width: 25%;">
                	<div id="document.accountsReceivableDocumentHeader.customer.customerName.div">
                		<kul:htmlControlAttribute attributeEntry="${documentAttributes.customerName}"
                		 property="document.customer.customerName" 
                		 tabindexOverride="${tabindexOverrideBase} + 5"
                		 readOnly="true" />
                	</div>
                </td>
            </tr>    
            <tr>        
				<th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.customerPurchaseOrderNumber}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.customerPurchaseOrderNumber}"
                    property="document.customerPurchaseOrderNumber" 
                    tabindexOverride="${tabindexOverrideBase} + 10"
                    readOnly="${readOnly}"/>
				</td>    
                <th align=right valign=middle class="bord-l-b" style="width: 25%;"> 
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.customerPurchaseOrderDate}" /></div>
                </th>
                <td align=left valign=middle class="datacell" style="width: 25%;">
	               	<c:choose>
	                    <c:when test="${readOnly}">
	                        <kul:htmlControlAttribute attributeEntry="${documentAttributes.customerPurchaseOrderDate}" 
	                        property="document.customerPurchaseOrderDate" 
	                        tabindexOverride="${tabindexOverrideBase} + 15"
	                        readOnly="${readOnly}" />
	                    </c:when>
	                    <c:otherwise>
	                        <kul:dateInput attributeEntry="${documentAttributes.customerPurchaseOrderDate}" tabindexOverride="${tabindexOverrideBase} + 20" property="document.customerPurchaseOrderDate"/>
	                    </c:otherwise>
	                </c:choose>                
                </td>          
            </tr>         
            <tr>
                <td colspan="4" class="subhead">Detail Information</td>
            </tr>
            
            <tr>
				<th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.billingDateForDisplay}" /></div>
                </th>     
                <td>
					<kul:htmlControlAttribute attributeEntry="${documentAttributes.billingDateForDisplay}"
					 property="document.billingDateForDisplay" 
					 tabindexOverride="${tabindexOverrideBase} + 25"
					 readOnly="true" />
                </td>  
				<th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.invoiceDueDate }" /></div>
                </th>
                <td>
	               	<c:choose>
	                    <c:when test="${readOnly}">
	                        <kul:htmlControlAttribute attributeEntry="${documentAttributes.invoiceDueDate}"
	                         property="document.invoiceDueDate"
	                         tabindexOverride="${tabindexOverrideBase} + 30"
	                          readOnly="${readOnly}" />
	                    </c:when>
	                    <c:otherwise>
	                        <kul:dateInput attributeEntry="${documentAttributes.invoiceDueDate}" tabindexOverride="${tabindexOverrideBase} + 35" property="document.invoiceDueDate"/>
	                    </c:otherwise>
	                </c:choose>
                </td>                          
            </tr>
            
            <tr>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.invoiceTermsText}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.invoiceTermsText}" 
                    property="document.invoiceTermsText" 
                    tabindexOverride="${tabindexOverrideBase} + 40"
                    readOnly="${readOnly}"/>
                </td>     
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.openInvoiceIndicator}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.openInvoiceIndicator}" 
                    tabindexOverride="${tabindexOverrideBase} + 45"
                    property="document.openInvoiceIndicator" readOnly="true"/>
                </td> 
            </tr>
            
			<tr>
                <td colspan="4" class="subhead">Statement Information</td>
            </tr>
			<tr>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.invoiceHeaderText}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.invoiceHeaderText}" 
                    property="document.invoiceHeaderText" 
                    tabindexOverride="${tabindexOverrideBase} + 50"
                    readOnly="${readOnly}"/>
                </td> 	
				<th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.invoiceAttentionLineText }" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.invoiceAttentionLineText }" 
                    property="document.invoiceAttentionLineText" 
                    tabindexOverride="${tabindexOverrideBase} + 55"
                    readOnly="${readOnly}"/>
                </td>
            </tr>
            
            <tr>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.printInvoiceIndicator }" /></div>
                </th>
                <td align=left valign=middle class="datacell">
					<c:choose>
						<c:when test="${not readOnly}">
							<kul:htmlControlAttribute attributeEntry="${documentAttributes.printInvoiceIndicator }" 
							property="document.printInvoiceIndicator" 
							tabindexOverride="${tabindexOverrideBase} + 60"
							readOnly="${readOnly}"/>
						</c:when>
						<c:otherwise>
							<c:if test="${not empty KualiForm.document.printInvoiceOption }">
								${KualiForm.document.printInvoiceOption.printInvoiceDescription}
							</c:if>
						</c:otherwise>
					</c:choose>
                </td>
				<th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${ documentAttributes.printDate }" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.printDate }" 
                    property="document.printDate" 
                    tabindexOverride="${tabindexOverrideBase} + 65"
                    readOnly="true"/>
                </td>     
            </tr>        
        </table>
    </div>
</kul:tab>
