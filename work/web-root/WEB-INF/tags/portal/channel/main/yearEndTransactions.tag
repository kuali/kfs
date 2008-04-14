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
<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>

<channel:portalChannelTop channelTitle="Year End Transactions" />
<div class="body">
    <strong>Financial Processing</strong><br />
    <ul class="chan">
		<li><portal:portalLink displayTitle="true" title="Year End Budget Adjustment" url="financialYearEndBudgetAdjustment.do?methodToCall=docHandler&command=initiate&docTypeName=YearEndBudgetAdjustmentDocument" /></li>
		<li><portal:portalLink displayTitle="true" title="Year End Distribution of Income and Expense" url="financialYearEndDistributionOfIncomeAndExpense.do?methodToCall=docHandler&command=initiate&docTypeName=YearEndDistributionOfIncomeAndExpenseDocument" /></li>
		<li><portal:portalLink displayTitle="true" title="Year End General Error Correction" url="financialYearEndGeneralErrorCorrection.do?methodToCall=docHandler&command=initiate&docTypeName=YearEndGeneralErrorCorrectionDocument" /></li>
		<li><portal:portalLink displayTitle="true" title="Year End Transfer of Funds" url="financialYearEndTransferOfFunds.do?methodToCall=docHandler&command=initiate&docTypeName=YearEndTransferOfFundsDocument" /></li>
    </ul>
    <strong>Labor Distribution</strong><br />
    <ul class="chan">
	  	<li>
			<portal:portalLink displayTitle="true"
				title="Year End Benefit Expense Transfer"
				url="laborYearEndBenefitExpenseTransfer.do?methodToCall=docHandler&command=initiate&docTypeName=YearEndBenefitExpenseTransferDocument" />
		</li>
		<li>
			<portal:portalLink displayTitle="true"
				title="Year End Salary Expense Transfer"
				url="laborYearEndSalaryExpenseTransfer.do?methodToCall=docHandler&command=initiate&docTypeName=YearEndSalaryExpenseTransferDocument" />
		</li>
    </ul>
</div>
<channel:portalChannelBottom />
