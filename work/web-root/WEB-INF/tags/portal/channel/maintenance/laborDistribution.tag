<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>

<channel:portalChannelTop
	channelTitle="Labor Distribution" />
<div class="body">
	<ul class="chan">
		<li>
			<portal:portalLink displayTitle="true"
				title="Labor Benefits Calculation"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.labor.bo.BenefitsCalculation&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true" title="Labor Benefits Type"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.labor.bo.BenefitsType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true" title="Labor Object Code"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.labor.bo.LaborObject&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true"
				title="Labor Object Code Benefits"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.labor.bo.PositionObjectBenefit&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true"
				title="Labor Position Object Code Group"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.labor.bo.PositionObjectGroup&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
	</ul>
</div>
<channel:portalChannelBottom />
