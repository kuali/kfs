<%--
 Copyright 2007-2008 The Kuali Foundation
 
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

<channel:portalChannelTop channelTitle="Budget Construction" />
<div class="body">
	<ul class="chan">
		<li>
			<portal:portalLink displayTitle="true" title="Account Reports"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.bc.businessobject.BudgetConstructionAccountReports&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true" title="Appointment Funding Duration"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.bc.businessobject.BudgetConstructionDuration&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true" title="Appointment Funding Reason Code"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.bc.businessobject.BudgetConstructionAppointmentFundingReasonCode&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true" title="Budget Construction Position"
				url="budgetTempListLookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.bc.businessobject.BudgetConstructionPosition&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true" title="Calculated Salary Foundation Tracker Override"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.bc.businessobject.CalculatedSalaryFoundationTrackerOverride&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true" title="Intended Incumbent"
				url="budgetTempListLookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.bc.businessobject.BudgetConstructionIntendedIncumbent&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true" title="Organization Reports"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.bc.businessobject.BudgetConstructionOrganizationReports&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
	</ul>
</div>
<channel:portalChannelBottom />
