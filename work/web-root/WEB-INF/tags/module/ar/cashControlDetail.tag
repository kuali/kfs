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

<%@ attribute name="cashControlDetailAttributes" required="true"
	type="java.util.Map"
	description="The DataDictionary entry containing attributes for cash control detail fields."%>

<%@ attribute name="readOnly" required="true"
	description="determines whether the cash control detail will be displayed readonly"%>
<%@ attribute name="addLine" required="true"
	description="determines whether the displayed line is the add line or not"%>
<%@ attribute name="rowHeading" required="true"%>
<%@ attribute name="propertyName" required="true"
	description="name of form property containing the cash control document"%>
<%@ attribute name="actionMethod" required="true"
	description="methodToCall value for actionImage"%>
<%@ attribute name="actionImage" required="true"
	description="path to image to be displayed in Action column"%>
<%@ attribute name="actionAlt" required="true"
	description="alt value for actionImage"%>
<%@ attribute name="cssClass" required="true"%>

<!--  this parameter is ignored now, as of KULAR-755 -->
<%@ attribute name="editPaymentAppDoc" required="true"%>
<c:set var="tabindexOverrideBase" value="20" />

<tr>
	<kul:htmlAttributeHeaderCell literalLabel="${rowHeading}:" scope="row"
		rowspan="2">
	</kul:htmlAttributeHeaderCell>

	<td align=left class="${cssClass}">
		<c:choose>
			<c:when test="${addLine}"> <!--  we always make the link live now, as of KULAR-755 -->
				<kul:htmlControlAttribute
					attributeEntry="${cashControlDetailAttributes.referenceFinancialDocumentNumber}"
					property="${propertyName}.referenceFinancialDocumentNumber"
					readOnly="true" />
			</c:when>
			<c:otherwise>
				<a href="${ConfigProperties.workflow.url}/DocHandler.do?docId=${KualiForm.document.cashControlDetails[rowHeading-1].referenceFinancialDocumentNumber}&command=displayDocSearchView" target="cashControlDetailPayApp">
					<kul:htmlControlAttribute
						attributeEntry="${cashControlDetailAttributes.referenceFinancialDocumentNumber}"
						property="${propertyName}.referenceFinancialDocumentNumber"
						readOnly="true" />
				</a>
			</c:otherwise>
		</c:choose>
	</td>

	<td align=left class="${cssClass}">
		<kul:htmlControlAttribute
			attributeEntry="${cashControlDetailAttributes.status}"
			property="${propertyName}.referenceFinancialDocument.documentHeader.workflowDocument.document.status"
			tabindexOverride="${tabindexOverrideBase}"
			readOnly="true" />
	</td>
	<td align=left class="${cssClass}">
		<kul:htmlControlAttribute
			attributeEntry="${cashControlDetailAttributes.customerNumber}"
			property="${propertyName}.customerNumber" 
			tabindexOverride="${tabindexOverrideBase} + 5"
			readOnly="${readOnly}" />
		<c:if test="${not readOnly}">
			&nbsp;
			<kul:lookup boClassName="org.kuali.kfs.module.ar.businessobject.Customer"
				fieldConversions="customerNumber:${propertyName}.customerNumber" />
		</c:if>
	</td>
	<td align=left class="${cssClass}">
		<kul:htmlControlAttribute
			attributeEntry="${cashControlDetailAttributes.customerPaymentMediumIdentifier}"
			property="${propertyName}.customerPaymentMediumIdentifier"
			tabindexOverride="${tabindexOverrideBase} + 10"
			readOnly="${readOnly}" />
	</td>
	<td align=left class="${cssClass}">
		<c:choose>
			<c:when test="${readOnly}">
				<kul:htmlControlAttribute
					attributeEntry="${cashControlDetailAttributes.customerPaymentDate}"
					property="${propertyName}.customerPaymentDate"
					tabindexOverride="${tabindexOverrideBase} + 15"
					readOnly="${readOnly}" />
			</c:when>
			<c:otherwise>
				<kul:dateInput
					attributeEntry="${cashControlDetailAttributes.customerPaymentDate}"
					tabindexOverride="${tabindexOverrideBase} + 20"
					property="${propertyName}.customerPaymentDate" />
			</c:otherwise>
		</c:choose>
	</td>
	<td align=left class="${cssClass}">
		<kul:htmlControlAttribute
			attributeEntry="${cashControlDetailAttributes.financialDocumentLineAmount }"
			property="${propertyName}.financialDocumentLineAmount"
			tabindexOverride="${tabindexOverrideBase} + 25"
			styleClass="right" readOnly="${not addLine and readOnly}" />
	</td>

	<c:if test="${not readOnly}">
		<td class="${cssClass}" rowspan="2">
			<div align="center">
				<html:image property="methodToCall.${actionMethod}"
					src="${actionImage}" alt="${actionAlt}" title="${actionAlt}"
					tabindex="${tabindexOverrideBase + 30}"
					styleClass="tinybutton" />
			</div>
		</td>
	</c:if>
</tr>
<tr>
	<kul:htmlAttributeHeaderCell
		attributeEntry="${cashControlDetailAttributes.customerPaymentDescription}" />
	<td colspan="5" class="${cssClass}">
		<kul:htmlControlAttribute
			attributeEntry="${cashControlDetailAttributes.customerPaymentDescription}"
			property="${propertyName}.customerPaymentDescription"
			tabindexOverride="${tabindexOverrideBase} + 35"
			readOnly="${readOnly}" />
	</td>
</tr>
