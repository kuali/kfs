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
<c:set var="historicalTravelExpenseAttributes" value="${DataDictionary.HistoricalTravelExpense.attributes}" />
<c:set var="travelCardTypeAttributes" value="${DataDictionary.TravelCardType.attributes}" />
<c:set var="expenseTypeAttributes" value="${DataDictionary.ExpenseType.attributes}"/>
<jsp:useBean id="paramMap" class="java.util.HashMap" />
  
<div class="tab-container" align="center">
<h3>Reconciled Expenses</h3>
<table cellpadding="0" cellspacing="0" class="datatable" summary="Imported Expenses">
	<tr>
		<th align="center">Action</th>
		<kul:htmlAttributeHeaderCell attributeEntry="${historicalTravelExpenseAttributes.reconciled}" hideRequiredAsterisk="true" />
		<th align="center">Document Number</th>
		<kul:htmlAttributeHeaderCell attributeEntry="${travelCardTypeAttributes.code}" hideRequiredAsterisk="true" />
		<kul:htmlAttributeHeaderCell attributeEntry="${historicalTravelExpenseAttributes.transactionPostingDate}" useShortLabel="true" hideRequiredAsterisk="true" />
		<kul:htmlAttributeHeaderCell attributeEntry="${expenseTypeAttributes.name}" hideRequiredAsterisk="true" />
		<kul:htmlAttributeHeaderCell attributeEntry="${historicalTravelExpenseAttributes.travelCompany}" hideRequiredAsterisk="true" />
		<kul:htmlAttributeHeaderCell attributeEntry="${historicalTravelExpenseAttributes.amount}" hideRequiredAsterisk="true" />
	</tr>
	<logic:iterate indexId="ctr" name="KualiForm"
			property="document.reconciledHistoricalTravelExpenses" id="currentLine">
			<c:set var="lineCounter" value="${lineCounter + 1}" />
		<tr>
			<td>
				<div align="center">
					<c:choose>
						<c:when test="${KualiForm.document.reconciledHistoricalTravelExpenses[ctr].agencyStagingDataId != null}">
							<a target="blank" href="kr/inquiry.do?businessObjectClassName=org.kuali.kfs.module.tem.businessobject.AgencyStagingData&methodToCall=start&id=${KualiForm.document.reconciledHistoricalTravelExpenses[ctr].agencyStagingDataId }">View Details</a>
						</c:when>
						<c:otherwise>
							<a target="blank" href="kr/inquiry.do?businessObjectClassName=org.kuali.kfs.module.tem.businessobject.CreditCardStagingData&methodToCall=start&id=${KualiForm.document.reconciledHistoricalTravelExpenses[ctr].creditCardStagingDataId }">View Details</a>
						</c:otherwise>
					</c:choose>
				</div>
			</td>
			<td>
				<div align="center">
					<kul:htmlControlAttribute
						attributeEntry="${historicalTravelExpenseAttributes.reconciled}"
						property="document.reconciledHistoricalTravelExpenses[${ctr}].reconciled" 
						readOnly="true" />
				</div>
			</td>
			<td>
				<div align="center">
					${KualiForm.document.reconciledHistoricalTravelExpenses[ctr].documentNumber}
				</div>
			</td>
			<td>
				<div align="center">
					${KualiForm.document.reconciledHistoricalTravelExpenses[ctr].creditCardAgency.travelCardTypeCode}
				</div>
			</td>
			<td>
				<div align="center">
					<kul:htmlControlAttribute
						attributeEntry="${historicalTravelExpenseAttributes.transactionPostingDate}"
						property="document.reconciledHistoricalTravelExpenses[${ctr}].transactionPostingDate" 
						readOnly="true" />
				</div>
			</td>
			<td>
				<div align="left">
					<kul:htmlControlAttribute
						attributeEntry="${expenseTypeAttributes.name}"
						property="document.reconciledHistoricalTravelExpenses[${ctr}].expenseType.name" 
						readOnly="true" />
				</div>
			</td>
			<td>
				<div align="left">
					<kul:htmlControlAttribute
						attributeEntry="${historicalTravelExpenseAttributes.travelCompany}"
						property="document.reconciledHistoricalTravelExpenses[${ctr}].travelCompany" 
						readOnly="true" />
				</div>
			
			</td>
			<td>
				<div align="right">
					<kul:htmlControlAttribute
						attributeEntry="${historicalTravelExpenseAttributes.convertedAmount}"
						property="document.reconciledHistoricalTravelExpenses[${ctr}].convertedAmount" 
						readOnly="true" />
				</div>
			</td>
		</tr>
	</logic:iterate>
  </table>
</div>