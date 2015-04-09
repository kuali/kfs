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

<%@ attribute name="readOnly" required="true"
	description="If document is in read only mode"%>

<kul:tab tabTitle="Edit List of Invoices" defaultOpen="true"
	tabErrorKey="document.invoiceDetails">
	<div class="tab-container" align="center">
		<c:if test="${not empty KualiForm.document.proposalNumber}">
			<c:if test="${not readOnly}">
				<table width="100%" cellpadding="0" cellspacing="0"	class="datatable">
					<tr>
						<td colspan='2' class='tab-subhead'>New</td>
					</tr>
					<tr>
						<th colspan='2'>Look Up / Add Multiple Invoices
							<kul:multipleValueLookup lookedUpCollectionName="invoiceDetails" boClassName="org.kuali.kfs.module.ar.businessobject.CollectionActivityInvoiceLookup"
								lookupParameters="document.proposalNumber:proposalNumber,document.agencyNumber:agencyNumber,document.agencyName:agencyName,document.customerNumber:customerNumber,document.customerName:customerName"/>
						</th>
					</tr>
				</table>
			</c:if>
			<c:if test="${!empty KualiForm.document.invoiceDetails}">
				<table width="100%" cellpadding="0" cellspacing="0" class="datatable">
					<logic:iterate indexId="ctr" name="KualiForm" property="document.invoiceDetails" id="Invoice">
						<ar:contractsGrantsCollectionActivityInvoiceDetail
							invPropertyName="document.invoiceDetails[${ctr}]"
							ctr="${ctr}" readOnly="${readOnly}" />
					</logic:iterate>
				</table>
			</c:if>							
		</c:if>
	</div>
</kul:tab>