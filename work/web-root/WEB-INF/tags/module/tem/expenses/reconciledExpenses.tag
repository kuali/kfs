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
<jsp:useBean id="paramMap" class="java.util.HashMap" />
  
<div class="tab-container" align="center">
<h3>Reconciled Expenses</h3>
<table cellpadding="0" cellspacing="0" class="datatable" summary="Imported Expenses">
	<tr>
		<th align="center">Action</th>
		<th align="center">Status</th>
		<th align="center">Document Number</th>
		<th align="center">Document Type</th>
		<th align="center">Card or Agency Type</th>
		<th align="center">Expense Date</th>
		<th align="center">Expense Type</th>
		<th align="center">Company Name</th>
		<th align="center">Amount</th>
	</tr>
	<logic:iterate indexId="ctr" name="KualiForm"
			property="document.historicalTravelExpenses" id="currentLine">
			<c:set var="lineCounter" value="${lineCounter + 1}" />
		<tr>
			<td>
				<div align="center">
					<c:choose>
						<c:when test="${KualiForm.document.historicalTravelExpenses[ctr].agencyStagingDataId != null}">
							<a target="blank" href="kr/inquiry.do?businessObjectClassName=org.kuali.kfs.module.tem.businessobject.AgencyStagingData&methodToCall=start&id=${KualiForm.document.historicalTravelExpenses[ctr].agencyStagingDataId }">View Details</a>
						</c:when>
						<c:otherwise>
							<a target="blank" href="kr/inquiry.do?businessObjectClassName=org.kuali.kfs.module.tem.businessobject.CreditCardStagingData&methodToCall=start&id=${KualiForm.document.historicalTravelExpenses[ctr].creditCardStagingDataId }">View Details</a>
						</c:otherwise>
					</c:choose>
				</div>
			</td>
			<td>
				<div align="center">
					<kul:htmlControlAttribute
						attributeEntry="${historicalTravelExpenseAttributes.reconciled}"
						property="document.historicalTravelExpenses[${ctr}].reconciled" 
						readOnly="true" />
				</div>
			</td>
			<td>
				<div align="center">
					${KualiForm.document.historicalTravelExpenses[ctr].documentNumber}
				</div>
			</td>
			<td>
				<div align="center">
					${KualiForm.document.historicalTravelExpenses[ctr].documentType}
				</div>
			</td>
			<td>
				<div align="center">
					<c:choose>
						<c:when test="${KualiForm.document.historicalTravelExpenses[ctr].agencyStagingDataId != null}">
							${KualiForm.document.historicalTravelExpenses[ctr].creditCardAgency.travelCardTypeCode}
						</c:when>
						<c:otherwise>
							${KualiForm.document.historicalTravelExpenses[ctr].creditCardAgency.creditCardOrAgencyCode}
						</c:otherwise>
					</c:choose>
				</div>
			</td>
			<td>
				<div align="center">
					<kul:htmlControlAttribute
						attributeEntry="${historicalTravelExpenseAttributes.transactionPostingDate}"
						property="document.historicalTravelExpenses[${ctr}].transactionPostingDate" 
						readOnly="true" />
				</div>
			</td>
			<td>
				<div align="left">
					<kul:htmlControlAttribute
						attributeEntry="${historicalTravelExpenseAttributes.travelExpenseType}"
						property="document.historicalTravelExpenses[${ctr}].travelExpenseType" 
						readOnly="true" />
				</div>
			</td>
			<td>
				<div align="left">
					<kul:htmlControlAttribute
						attributeEntry="${historicalTravelExpenseAttributes.travelCompany}"
						property="document.historicalTravelExpenses[${ctr}].travelCompany" 
						readOnly="true" />
				</div>
			
			</td>
			<td>
				<div align="right">
					<kul:htmlControlAttribute
						attributeEntry="${historicalTravelExpenseAttributes.convertedAmount}"
						property="document.historicalTravelExpenses[${ctr}].convertedAmount" 
						readOnly="true" />
				</div>
			</td>
		</tr>
	</logic:iterate>
  </table>
</div>