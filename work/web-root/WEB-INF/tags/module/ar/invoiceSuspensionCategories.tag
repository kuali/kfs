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
<%@ attribute name="readOnly" required="false" description="If document is in read only mode"%>

<c:if test="${!empty KualiForm.document.invoiceGeneralDetail.proposalNumber}">
	<kul:tab tabTitle="Invoice Suspension Categories" defaultOpen="true" tabErrorKey="document.invoiceSuspensionCategories*">
		<c:set var="invoiceSuspensionCategoriesAttributes" value="${DataDictionary.InvoiceSuspensionCategory.attributes}" />
		<c:set var="suspensionCategoriesAttributes" value="${DataDictionary.SuspensionCategory.attributes}" />
		<div class="tab-container" align="center">
			<h3>Invoice Suspension Categories</h3>
 			<table cellpadding=0 class="datatable" summary="Invoice Suspension Categories section">
				<tr>
					<kul:htmlAttributeHeaderCell attributeEntry="${invoiceSuspensionCategoriesAttributes.suspensionCategoryCode}" useShortLabel="false" />
					<kul:htmlAttributeHeaderCell attributeEntry="${suspensionCategoriesAttributes.suspensionCategoryDescription}" useShortLabel="false" />
				</tr>
				<c:if test="${!empty KualiForm.document.invoiceSuspensionCategories}">
					<logic:iterate indexId="ctr" name="KualiForm" property="document.invoiceSuspensionCategories" id="SuspensionCategory">
						<tr>
							<td class="datacell"><kul:htmlControlAttribute attributeEntry="${invoiceSuspensionCategoriesAttributes.suspensionCategoryCode}"
									property="document.invoiceSuspensionCategories[${ctr}].suspensionCategoryCode" readOnly="true" /></td>
							<td class="datacell" width="75%"><kul:htmlControlAttribute
									attributeEntry="${invoiceSuspensionCategoriesAttributes['suspensionCategoryDescription']}"
									property="document.invoiceSuspensionCategories[${ctr}].suspensionCategoryDescription" readOnly="true" /></td>
						</tr>
					</logic:iterate>
				</c:if>
			</table>
		</div>
	</kul:tab>
</c:if>
