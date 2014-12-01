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

<%-- determine backdoor user to display app link; taken from kr:immutableBar.tag --%>
<c:set var="backdoorEnabled" value="<%=org.kuali.rice.coreservice.framework.CoreFrameworkServiceLocator.getParameterService().getParameterValueAsBoolean(org.kuali.rice.kew.api.KewApiConstants.KEW_NAMESPACE, org.kuali.rice.krad.util.KRADConstants.DetailTypes.BACKDOOR_DETAIL_TYPE, org.kuali.rice.kew.api.KewApiConstants.SHOW_BACK_DOOR_LOGIN_IND)%>"/>
<c:if test="${backdoorEnabled}">
	<c:choose> 
		<c:when test="${empty UserSession.loggedInUserPrincipalName}" > 
			<c:set var="backdoorIdUrl" value=""/> 			
		</c:when> 
		<c:otherwise> 			
			<c:choose>
				<c:when test="${UserSession.backdoorInUse}" >
					<c:set var="backdoorIdUrl" value="backdoorId=${UserSession.principalName}"/> 					
				</c:when>
				<c:otherwise>
					<c:set var="backdoorIdUrl" value="backdoorId=${UserSession.loggedInUserPrincipalName}"/>
				</c:otherwise>
			</c:choose>				 			
		</c:otherwise> 
	</c:choose>
</c:if>

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
				<a href="${ConfigProperties.workflow.url}/DocHandler.do?docId=${KualiForm.document.cashControlDetails[rowHeading-1].referenceFinancialDocumentNumber}&command=displayDocSearchView${empty backdoorIdUrl ? "" : "&"}${backdoorIdUrl}" target="cashControlDetailPayApp">
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
