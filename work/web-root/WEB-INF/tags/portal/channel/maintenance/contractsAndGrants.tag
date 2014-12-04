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

<channel:portalChannelTop channelTitle="Contracts & Grants" />
<div class="body">
	<ul class="chan">

 	<c:choose>
 	<c:when test="${ConfigProperties['module.external.kuali.coeus.enabled'] == 'true'}"> 
 		<li><portal:portalLink displayTitle="true" title="CG Account Default" url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.external.kc.businessobject.AccountAutoCreateDefaults&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
		<c:if test="${ConfigProperties.contracts.grants.billing.enabled == 'true'}">
 			<li><portal:portalLink displayTitle="true" title="KC Billing Frequency Mapping" url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.external.kc.businessobject.BillingFrequencyMapping&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
 		</c:if>
    </c:when>
    <c:otherwise>
		<li>
			<portal:portalLink displayTitle="true" title="Agency"
				url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.cg.businessobject.Agency&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true" title="Agency Type"
				url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.cg.businessobject.AgencyType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true" title="Award Status"
				url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.cg.businessobject.AwardStatus&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<c:if test="${ConfigProperties.contracts.grants.billing.enabled == 'true'}">
			<li><portal:portalLink displayTitle="true"
					title="Billing Frequency"
					url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.cg.businessobject.BillingFrequency&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
			</li>
		</c:if>
		<li>
			<portal:portalLink displayTitle="true" title="CFDA"
				url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.cg.businessobject.CFDA&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<c:if test="${ConfigProperties.contracts.grants.billing.enabled == 'true'}">
			<li><portal:portalLink displayTitle="true"
					title="Instrument Type"
					url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.cg.businessobject.InstrumentType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
			</li>	
		</c:if>
		<li>
			<portal:portalLink displayTitle="true" title="Grant Description"
				url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.cg.businessobject.GrantDescription&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true"
				title="Letter of Credit Fund Group"
				url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.cg.businessobject.LetterOfCreditFundGroup&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<c:if test="${ConfigProperties.contracts.grants.billing.enabled == 'true'}">
			<li><portal:portalLink displayTitle="true"
					title="Letter of Credit Fund"
					url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.cg.businessobject.LetterOfCreditFund&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
			</li>
		</c:if>
		<li>
			<portal:portalLink displayTitle="true"
				title="Proposal/Award Close"
				url="${ConfigProperties.application.url}/cgClose.do?methodToCall=docHandler&command=initiate&docTypeName=CLOS" />
		</li>
		<li>
			<portal:portalLink displayTitle="true" title="Proposal Purpose"
				url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.cg.businessobject.ProposalPurpose&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true" title="Proposal Status"
				url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.cg.businessobject.ProposalStatus&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true" title="Proposal Type"
				url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.cg.businessobject.ProposalAwardType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		
		<li>
			<portal:portalLink displayTitle="true" title="Research Risk Type"
				url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.cg.businessobject.ResearchRiskType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true" title="Sub-Contractor"
				url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.cg.businessobject.SubContractor&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>	
	</c:otherwise>
	</c:choose>
	</ul>
</div>
<channel:portalChannelBottom />
