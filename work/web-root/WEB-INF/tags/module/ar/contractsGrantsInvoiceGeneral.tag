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
	description="The DataDictionary entry containing attributes for this row's fields."%>

<%@ attribute name="readOnly" required="true" description="used to decide editability of overview fields"%>

<c:set var="arDocHeaderAttributes" value="${DataDictionary.AccountsReceivableDocumentHeader.attributes}" />

<c:if test="${!empty KualiForm.document.invoiceGeneralDetail.proposalNumber}">

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
								labelFor="document.accountsReceivableDocumentHeader.customerNumber" />
						</div>
					</th>
					<td align=left valign=middle class="datacell" style="width: 25%;">
						<kul:htmlControlAttribute attributeEntry="${arDocHeaderAttributes.customerNumber}"
							property="document.accountsReceivableDocumentHeader.customerNumber" readOnly="true" />
					</td>
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
