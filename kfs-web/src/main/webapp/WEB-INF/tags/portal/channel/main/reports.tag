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

<channel:portalChannelTop channelTitle="Reports" />
<div class="body">
	<c:if
		test="${ConfigProperties['module.contracts.and.grants.enabled'] == 'true' && ConfigProperties.contracts.grants.billing.enabled == 'true'}">
		<strong>Contracts & Grants</strong>
		</br>
		<ul class="chan">
			<li><portal:portalLink displayTitle="true"
					title="Award Balances"
					url="${ConfigProperties.application.url}/contractsGrantsAwardBalancesReport.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.cg.businessobject.ContractsGrantsAwardBalancesReport&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
			</li>
		</ul>
	</c:if>
	<c:if test="${ConfigProperties.module.labor.distribution.enabled == 'true'}">
		<strong>Effort Certification</strong></br>
	    <ul class="chan">	
			<li>
				<portal:portalLink displayTitle="true" title="Outstanding Certifications by Chart/Org/Report"
					url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ec.businessobject.OutstandingCertificationsByOrganization&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
			</li>
			
			<li>
				<portal:portalLink displayTitle="true" title="Outstanding Certifications By Report"
					url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ec.businessobject.OutstandingCertificationsByReport&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
			</li>
		</ul>
	</c:if>
</div>
<channel:portalChannelBottom />
						
						
						
						
						
						
