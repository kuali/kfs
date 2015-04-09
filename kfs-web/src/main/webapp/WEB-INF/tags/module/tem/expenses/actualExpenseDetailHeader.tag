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
<%@ taglib uri="/WEB-INF/tlds/temfunc.tld" prefix="temfunc"%>

<%@ attribute name="detailObject" required="true"
	description="The actual object"
	type="org.kuali.kfs.module.tem.businessobject.ActualExpense"%>

<c:set var="otherExpenseAttributes" value="${DataDictionary.ActualExpense.attributes}" />
<c:set var="temExtension" value="${DataDictionary.ExpenseTypeObjectCode.attributes}" />
<c:set var="isTA" value="${KualiForm.isTravelAuthorizationDoc}" />

<jsp:useBean id="paramMap" class="java.util.HashMap" />
<c:set target="${paramMap}" property="tripType"
	value="${KualiForm.document.tripTypeCode}" />
<c:set target="${paramMap}" property="travelerType"
	value="${KualiForm.document.traveler.travelerTypeCode}" />
<c:set target="${paramMap}" property="documentType"
	value="${KualiForm.docTypeName}" />

<tr>
	<th scope="row">&nbsp;</th>
	<th>
		<div align="left">
			<kul:htmlAttributeLabel
				attributeEntry="${otherExpenseAttributes.expenseDate}"
				noColon="true" />
		</div>
	</th>
	<th>
		<div align="left">
			<kul:htmlAttributeLabel
				attributeEntry="${otherExpenseAttributes.expenseTypeCode}"
				noColon="true" />
		</div>
	</th>
	<%-- Show Mileage --%>
	<c:if test="${detailObject.mileageIndicator}">
		<th>
			<div align=center>
				<kul:htmlAttributeLabel
					attributeEntry="${otherExpenseAttributes.miles}"
					noColon="true" />
			</div></th>
		<th>
			<div align=center>
				<kul:htmlAttributeLabel
					attributeEntry="${otherExpenseAttributes['mileageRate.rate']}"
					noColon="true" />
			</div>
		</th>
	</c:if>
	<th>
		<div align="left">
			<kul:htmlAttributeLabel
				attributeEntry="${otherExpenseAttributes.expenseAmount}"
				noColon="true" />
		</div>
	</th>
	<th>
		<div align="left">$US</div>
	</th>
	<th>
		<div align="left">
			<kul:htmlAttributeLabel
				attributeEntry="${otherExpenseAttributes.nonReimbursable}"
				noColon="true" />
		</div>
	</th>
	<th>
		<div align="left">
			<kul:htmlAttributeLabel
				attributeEntry="${otherExpenseAttributes.taxable}"
				noColon="true" />
		</div>
	</th>
	<th>
		<div align="left">
			<kul:htmlAttributeLabel
				attributeEntry="${otherExpenseAttributes.missingReceipt}"
				noColon="true" />
		</div>
	</th>
	<%-- Show Airfare --%>
	<c:if test="${detailObject.airfareIndicator}">
		<th>
			<div align=left>
				<kul:htmlAttributeLabel
					attributeEntry="${otherExpenseAttributes.airfareSourceCode}"
					noColon="true" />
			</div></th>
		<th>
			<div align=left>
				<kul:htmlAttributeLabel
					attributeEntry="${otherExpenseAttributes.classOfServiceCode}"
					noColon="true" />
			</div>
		</th>
	</c:if>

	<%-- Show Car Rental--%>
	<c:if test="${detailObject.rentalCarIndicator}">
		<th>
			<div align=left>
				<kul:htmlAttributeLabel
					attributeEntry="${otherExpenseAttributes.classOfServiceCode}"
					noColon="true" />
			</div></th>
		<th>
			<div align=left>
				<kul:htmlAttributeLabel
					attributeEntry="${otherExpenseAttributes.rentalCarInsurance}"
					noColon="true" />
			</div>
		</th>
	</c:if>
	<th>
		<div align="center">Actions</div>
	</th>
</tr>
