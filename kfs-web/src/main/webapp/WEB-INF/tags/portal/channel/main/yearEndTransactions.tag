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

<channel:portalChannelTop channelTitle="Year End Transactions" />
<div class="body">
	<c:if test="${fn:trim(ConfigProperties.environment) != fn:trim(ConfigProperties.production.environment.code)}">
		<strong>Capital Asset Management</strong><br />
	    <ul class="chan">    
			<li><portal:portalLink displayTitle="true" title="Year End Depreciation" url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.cam.businessobject.AssetYearEndDepreciation&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
		</ul>	
	</c:if>	
    <strong>Financial Processing</strong><br />
    <ul class="chan">
		<li><portal:portalLink displayTitle="true" title="Year End Budget Adjustment" url="${ConfigProperties.application.url}/financialYearEndBudgetAdjustment.do?methodToCall=docHandler&command=initiate&docTypeName=YEBA" /></li>
		<li><portal:portalLink displayTitle="true" title="Year End Distribution of Income and Expense" url="${ConfigProperties.application.url}/financialYearEndDistributionOfIncomeAndExpense.do?methodToCall=docHandler&command=initiate&docTypeName=YEDI" /></li>
		<li><portal:portalLink displayTitle="true" title="Year End General Error Correction" url="${ConfigProperties.application.url}/financialYearEndGeneralErrorCorrection.do?methodToCall=docHandler&command=initiate&docTypeName=YEGE" /></li>
		<li><portal:portalLink displayTitle="true" title="Year End Transfer of Funds" url="${ConfigProperties.application.url}/financialYearEndTransferOfFunds.do?methodToCall=docHandler&command=initiate&docTypeName=YETF" /></li>
    </ul>
    <c:if test="${ConfigProperties.module.labor.distribution.enabled == 'true'}">
	    <strong>Labor Distribution</strong><br />
	    <ul class="chan">
			<li>
				<portal:portalLink displayTitle="true"
					title="Year End Salary Expense Transfer"
					url="${ConfigProperties.application.url}/laborYearEndSalaryExpenseTransfer.do?methodToCall=docHandler&command=initiate&docTypeName=YEST" />
			</li>
	    </ul>
    </c:if>
</div>
<channel:portalChannelBottom />
