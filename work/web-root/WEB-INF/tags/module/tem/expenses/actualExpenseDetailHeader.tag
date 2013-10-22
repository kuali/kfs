<%--
 Copyright 2007-2009 The Kuali Foundation
 
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