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

<channel:portalChannelTop
	channelTitle="Capital Asset" />
<div class="body">
	<ul class="chan">
		<li>
             <portal:portalLink displayTitle="true"
				title="Asset Acquisition Type"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.cams.bo.AssetAcquisitionType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />				
		</li>		
		<li>
			<portal:portalLink displayTitle="true"
				title="Asset Condition"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.cams.bo.AssetCondition&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />				
		</li>
		<li>
			<portal:portalLink displayTitle="true"
				title="Asset Depreciation Convention"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.cams.bo.AssetDepreciationConvention&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true"
				title="Asset Depreciation Method"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.cams.bo.AssetDepreciationMethod&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true"
				title="Asset Location Type"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.cams.bo.AssetLocationType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true"
				title="Asset Object Code"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.cams.bo.AssetObjectCode&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true"
				title="Asset Retirement Reason"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.cams.bo.AssetRetirementReason&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
        <li>
             <portal:portalLink displayTitle="true"
				title="Asset Status"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.cams.bo.AssetStatus&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true"
				title="Asset Type"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.cams.bo.AssetType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>	
	</ul>
</div>
<channel:portalChannelBottom />
