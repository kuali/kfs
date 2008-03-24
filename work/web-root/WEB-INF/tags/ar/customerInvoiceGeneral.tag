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

<%@ attribute name="documentAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for this row's fields." %>
              
<%@ attribute name="readOnly" required="true" description="Controlls if values should be read only" %>                  
                 
<c:set var="arDocHeaderAttributes" value="${DataDictionary.AccountsReceivableDocumentHeader.attributes}" />

<kul:tab tabTitle="General" defaultOpen="true" tabErrorKey="${KFSConstants.CUSTOMER_INVOICE_DOCUMENT_GENERAL_ERRORS}">
    <div class="tab-container" align=center>	
        <table cellpadding="0" cellspacing="0" class="datatable" summary="Invoice Section">
            <tr>
                <td colspan="4" class="subhead">Customer Information</td>
            </tr>
			<tr>
				<th align=right valign=middle class="bord-l-b" style="width: 25%;">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${arDocHeaderAttributes.customerNumber}" forceRequired="true" /></div>
                </th>
                <td align=left valign=middle class="datacell" style="width: 25%;">
                
				    <c:choose>
				        <c:when test="${!readOnly}">
				            <c:set var="onblurForCustomer" value="loadCustomerInfo( this.name, 'document.accountsReceivableDocumentHeader.customer.customerName');"/>
				        </c:when>
				        <c:otherwise>
				            <c:set var="onblurForCustomer" value=""/>
				        </c:otherwise>
				    </c:choose>                
                    <kul:htmlControlAttribute attributeEntry="${arDocHeaderAttributes.customerNumber}" property="document.accountsReceivableDocumentHeader.customerNumber" readOnly="${readOnly}" onblur="${onblurForCustomer}"/>
                    <c:if test="${not readOnly}">
	                    &nbsp;
	                    <kul:lookup boClassName="org.kuali.module.ar.bo.Customer" fieldConversions="customerNumber:document.accountsReceivableDocumentHeader.customerNumber" lookupParameters="document.accountsReceivableDocumentHeader.customerNumber:customerNumber" />
                    </c:if>
                    <!--  Using accountlingLineDataCellDetail tag because it generates the appropriate div for displaying customer name using DWR -->
					<fin:accountingLineDataCellDetail
					    detailField="accountsReceivableDocumentHeader.customer.customerName"
					    accountingLine="document"
					    />                    
                </td>			
                <th align=right valign=middle class="bord-l-b" style="width: 25%;"> 
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.customerPurchaseOrderDate}" /></div>
                </th>
                <td align=left valign=middle class="datacell" style="width: 25%;">
	               	<c:choose>
	                    <c:when test="${readOnly}">
	                        <kul:htmlControlAttribute attributeEntry="${documentAttributes.customerPurchaseOrderDate}" property="document.customerPurchaseOrderDate" readOnly="${readOnly}" />
	                    </c:when>
	                    <c:otherwise>
	                        <kul:dateInput attributeEntry="${documentAttributes.customerPurchaseOrderDate}" property="document.customerPurchaseOrderDate"/>
	                    </c:otherwise>
	                </c:choose>                
                </td>          
            </tr>    
            <tr>        
				<th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.customerPurchaseOrderNumber}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.customerPurchaseOrderNumber}" property="document.customerPurchaseOrderNumber" readOnly="${readOnly}"/>
				</td>    
				<th align=right valign=middle class="bord-l-b">
                    &nbsp;
                </th>
                <td align=left valign=middle class="datacell">
                    &nbsp;
                </td> 
            </tr>         
            <tr>
                <td colspan="4" class="subhead">Detail Information</td>
            </tr>
            
            <tr>
				<th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.billingDate}" /></div>
                </th>     
                <td>
	               	<c:choose>
	                    <c:when test="${readOnly}">
	                        <kul:htmlControlAttribute attributeEntry="${documentAttributes.billingDate}" property="document.billingDate" readOnly="${readOnly}" />
	                    </c:when>
	                    <c:otherwise>
	                        <kul:dateInput attributeEntry="${documentAttributes.billingDate}" property="document.billingDate"/>
	                    </c:otherwise>
	                </c:choose>
                </td>  
				<th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.invoiceDueDate }" /></div>
                </th>
                <td>
	               	<c:choose>
	                    <c:when test="${readOnly}">
	                        <kul:htmlControlAttribute attributeEntry="${documentAttributes.invoiceDueDate}" property="document.invoiceDueDate" readOnly="${readOnly}" />
	                    </c:when>
	                    <c:otherwise>
	                        <kul:dateInput attributeEntry="${documentAttributes.invoiceDueDate}" property="document.invoiceDueDate"/>
	                    </c:otherwise>
	                </c:choose>
                </td>                          
            </tr>
            
            <tr>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.invoiceTermsText}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.invoiceTermsText}" property="document.invoiceTermsText" readOnly="${readOnly}"/>
                </td>     
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.openInvoiceIndicator}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.openInvoiceIndicator}" property="document.openInvoiceIndicator" readOnly="true"/>
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
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.invoiceHeaderText}" property="document.invoiceHeaderText" readOnly="${readOnly}"/>
                </td> 	
				<th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.invoiceAttentionLineText }" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.invoiceAttentionLineText }" property="document.invoiceAttentionLineText" readOnly="${readOnly}"/>
                </td>
            </tr>
            
            <tr>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right">Inv Print Option</div>
                </th>
                <td align=left valign=middle class="datacell">
                <c:choose>  
                	<c:when test="${! readOnly}">
                		<input type="text">    
                	</c:when>
				</c:choose>
                </td>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right">Print Date</div>
                </th>
                <td align=left valign=middle class="datacell">
                    &nbsp;
                </td>       
            </tr>        
        </table>
    </div>
</kul:tab>