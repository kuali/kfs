<%--
 Copyright 2007 The Kuali Foundation
 
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

<channel:portalChannelTop
	channelTitle="Labor Distribution" />
<div class="body">
	<ul class="chan">
		<li>
			<portal:portalLink displayTitle="true"
				title="Labor Benefits Calculation"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ld.businessobject.BenefitsCalculation&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true"
				title="Labor Benefits Rate Category"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ld.businessobject.LaborBenefitRateCategory&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true" title="Labor Benefits Type"
				url="kr/lookup.do?methodToCall=search&businessObjectClassName=org.kuali.kfs.module.ld.businessobject.BenefitsType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true" title="Labor Object Code"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ld.businessobject.LaborObject&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true"
				title="Labor Object Code Benefits"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ld.businessobject.PositionObjectBenefit&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true"
				title="Labor Position Object Group Code"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ld.businessobject.PositionObjectGroup&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
	</ul>
</div>
<channel:portalChannelBottom />
