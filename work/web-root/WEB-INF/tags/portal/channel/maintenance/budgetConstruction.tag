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

<channel:portalChannelTop channelTitle="Budget Construction" />
<div class="body">
	<ul class="chan">
		<li>
			<portal:portalLink displayTitle="true" title="Account Reports"
				url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.bc.businessobject.BudgetConstructionAccountReports&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true" title="Appointment Funding Duration"
				url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.bc.businessobject.BudgetConstructionDuration&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true" title="Appointment Funding Reason Code"
				url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.bc.businessobject.BudgetConstructionAppointmentFundingReasonCode&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true" title="Budget Construction Position"
				url="${ConfigProperties.application.url}/budgetTempListLookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.bc.businessobject.BudgetConstructionPosition&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true" title="Calculated Salary Foundation Tracker Override"
				url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.bc.businessobject.CalculatedSalaryFoundationTrackerOverride&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true" title="Intended Incumbent"
				url="${ConfigProperties.application.url}/budgetTempListLookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.bc.businessobject.BudgetConstructionIntendedIncumbent&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true" title="Organization Reports"
				url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.bc.businessobject.BudgetConstructionOrganizationReports&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
	</ul>
</div>
<channel:portalChannelBottom />
