<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>

<channel:portalChannelTop channelTitle="Post-Award" />
<div class="body">
	<ul class="chan">
		<li>
			<portal:portalLink displayTitle="true" title="Agency"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.cg.bo.Agency&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true" title="Agency Type"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.cg.bo.AgencyType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true" title="Award Status"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.cg.bo.AwardStatus&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true" title="CFDA Reference"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.cg.bo.Cfda&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true" title="Grant Description"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.cg.bo.GrantDescription&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true"
				title="Indirect Cost Recovery Automated Entry"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.chart.bo.IcrAutomatedEntry&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true"
				title="Indirect Cost Recovery Exclusion Account"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.chart.bo.IndirectCostRecoveryExclusionAccount&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true"
				title="Indirect Cost Recovery Exclusion Type"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.chart.bo.IndirectCostRecoveryExclusionType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true"
				title="Indirect Cost Recovery Type"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.chart.bo.codes.ICRTypeCode&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true"
				title="Letter of Credit Fund Group"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.cg.bo.LetterOfCreditFundGroup&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true"
				title="Post-Award Close"
				url="cgClose.do?methodToCall=docHandler&command=initiate&docTypeName=CloseDocument" />
		</li>		
		<li>
			<portal:portalLink displayTitle="true" title="Project Director"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.cg.bo.ProjectDirector&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true" title="Proposal Purpose"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.cg.bo.ProposalPurpose&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true" title="Proposal Status"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.cg.bo.ProposalStatus&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true" title="Proposal Type"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.cg.bo.ProposalAwardType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true" title="Sub-Contractor"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.cg.bo.Subcontractor&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
	</ul>
</div>
<channel:portalChannelBottom />
