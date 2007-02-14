<%--
 Copyright 2007 The Kuali Foundation.
 
 Licensed under the Educational Community License, Version 1.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl1.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ taglib prefix="c" uri="/tlds/c.tld" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="kul" %>

<%-- needed? --%>
<%--
<c:set var="budgetConstructionAttributes"
	value="${DataDictionary['KualiBudgetConstructionDocument'].attributes}" />
--%>

<kul:tab tabTitle="Revenue" defaultOpen="false" tabErrorKey="${Constants.BUDGET_CONSTRUCTION_REVENUE_TAB_ERRORS}">
<div class="tab-container" align=center>

		<table>
			<tr>
				<th>
					Object
				</th>
				<th>
					Sub-Object
				</th>
				<th>
					Base
				</th>
				<th>
					Request
				</th>
			</tr>
			<c:forEach items="${KualiForm.document.pendingBudgetConstructionGeneralLedgerRevenue}" var="item" >
				<tr>
					<td>${item.financialObjectCode}</td>
					<td>${item.financialSubObjectCode}</td>
					<td>${item.financialBeginningBalanceLineAmount}</td>
					<td>${item.accountLineAnnualBalanceAmount}</td>
				</tr>
			</c:forEach>
		</table>

</div>
</kul:tab>
