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

<script type='text/javascript' src="dwr/interface/CustomerService.js"></script>
<script language="JavaScript" type="text/javascript" src="scripts/module/ar/customerObjectInfo.js"></script>
<script type='text/javascript' src="dwr/interface/CustomerAddressService.js"></script>
<script language="JavaScript" type="text/javascript" src="scripts/module/ar/customerAddressObjectInfo.js"></script>

<%@ attribute name="documentAttributes" required="true" type="java.util.Map"
	description="The DataDictionary entry containing attributes for this row's fields."%>

<%@ attribute name="readOnly" required="true" description="used to decide editability of overview fields"%>

<c:set var="arDocHeaderAttributes" value="${DataDictionary.AccountsReceivableDocumentHeader.attributes}" />

<c:if test="${!empty KualiForm.document.proposalNumber}">

	<kul:tab tabTitle="Customer Information" defaultOpen="true" tabErrorKey="${KFSConstants.CUSTOMER_INVOICE_DOCUMENT_GENERAL_ERRORS}">
		<div class="tab-container" align=center>
			<table cellpadding="0" cellspacing="0" class="datatable" summary="Invoice Section">
				<tr>
					<td colspan="4" class="subhead">Customer Information</td>
				</tr>
				<tr>
					<th align=right valign=middle class="bord-l-b" style="width: 25%;">
						<div align="right">
							<kul:htmlAttributeLabel attributeEntry="${arDocHeaderAttributes.customerNumber}"
								labelFor="document.accountsReceivableDocumentHeader.customerNumber" forceRequired="true" />
						</div>
					</th>
					<td align=left valign=middle class="datacell" style="width: 25%;"><c:choose>
							<c:when test="${!readOnly}">
								<c:set var="onblurForCustomer"
									value="loadCustomerInfo( this.name, 'document.accountsReceivableDocumentHeader.customer.customerName'); loadCustomerAddressInfo( this.name );" />
							</c:when>
							<c:otherwise>
								<c:set var="onblurForCustomer" value="" />
							</c:otherwise>
						</c:choose> <kul:htmlControlAttribute attributeEntry="${arDocHeaderAttributes.customerNumber}"
							property="document.accountsReceivableDocumentHeader.customerNumber" readOnly="${readOnly}" onblur="${onblurForCustomer}"
							onchange="${onblurForCustomer}" forceRequired="true" /> <c:if test="${not readOnly}">
	                    &nbsp;
	                    <kul:lookup boClassName="org.kuali.kfs.module.ar.businessobject.Customer"
								fieldConversions="customerNumber:document.accountsReceivableDocumentHeader.customerNumber"
								lookupParameters="document.accountsReceivableDocumentHeader.customerNumber:customerNumber" />
						</c:if></td>
					<th align=right valign=middle class="bord-l-b" style="width: 25%;">
						<div align="right">
							<kul:htmlAttributeLabel attributeEntry="${documentAttributes.customerName}" />
						</div>
					</th>
					<td align=left valign=middle class="datacell" style="width: 25%;">
						<div id="document.accountsReceivableDocumentHeader.customer.customerName.div">
							<kul:htmlControlAttribute attributeEntry="${documentAttributes.customerName}"
								property="document.accountsReceivableDocumentHeader.customer.customerName" readOnly="true" />
						</div>
					</td>
				</tr>
			</table>
		</div>
	</kul:tab>
</c:if>