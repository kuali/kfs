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

<channel:portalChannelTop channelTitle="Effort Certification" />
<div class="body">
	<ul class="chan">		
		<li>
			<portal:portalLink displayTitle="true" title="Report Definition"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ec.businessobject.EffortCertificationReportDefinition&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		
		<li>
			<portal:portalLink displayTitle="true" title="Report Earn Paygroup"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ec.businessobject.EffortCertificationReportEarnPaygroup&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		
		<li>
			<portal:portalLink displayTitle="true" title="Report Period Status Code"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ec.businessobject.EffortCertificationPeriodStatusCode&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		 
		<li>
			<portal:portalLink displayTitle="true" title="Report Type"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ec.businessobject.EffortCertificationReportType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
	</ul>
</div>
<channel:portalChannelBottom />
