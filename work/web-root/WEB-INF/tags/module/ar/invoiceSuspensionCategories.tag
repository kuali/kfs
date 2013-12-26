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
<%@ attribute name="readOnly" required="false" description="If document is in read only mode"%>

<c:if test="${!empty KualiForm.document.proposalNumber}">
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
									attributeEntry="${invoiceSuspensionCategoriesAttributes['suspensionCategory.suspensionCategoryDescription']}"
									property="document.invoiceSuspensionCategories[${ctr}].suspensionCategory.suspensionCategoryDescription" readOnly="true" /></td>
						</tr>
					</logic:iterate>
				</c:if>
			</table>
		</div>
		<SCRIPT type="text/javascript">
			var kualiForm = document.forms['KualiForm'];
			var kualiElements = kualiForm.elements;
		</SCRIPT>
	</kul:tab>
</c:if>