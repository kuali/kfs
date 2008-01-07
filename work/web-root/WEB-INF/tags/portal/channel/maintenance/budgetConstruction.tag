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

<channel:portalChannelTop channelTitle="Budget Construction" />
<div class="body">
	<ul class="chan">
		<li>
			<portal:portalLink displayTitle="true" title="Calculated Salary Foundation Tracker Override"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.budget.bo.CalculatedSalaryFoundationTrackerOverride&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true" title="Budget Construction Account Reports"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.budget.bo.BudgetConstructionAccountReports&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true" title="Budget Construction Organization Reports"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.budget.bo.BudgetConstructionOrganizationReports&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true" title="Budget Construction Duration"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.budget.bo.BudgetConstructionDuration&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true" title="Budget Construction Position"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.budget.bo.BudgetConstructionPosition&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true" title="Budget Construction Intended Incumbent"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.budget.bo.BudgetConstructionIntendedIncumbent&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true" title="Budget Construction Appointment Funding Reason Code"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.budget.bo.BudgetConstructionAppointmentFundingReasonCode&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
	</ul>
</div>
<channel:portalChannelBottom />
