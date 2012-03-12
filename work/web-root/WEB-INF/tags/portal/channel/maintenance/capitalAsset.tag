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
	channelTitle="Capital Asset Management" />
<div class="body">
	<ul class="chan">
		<li>
             <portal:portalLink displayTitle="true"
				title="Asset Acquisition Type"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.cam.businessobject.AssetAcquisitionType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />				
		</li>		
		<li>
			<portal:portalLink displayTitle="true"
				title="Asset Condition"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.cam.businessobject.AssetCondition&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />				
		</li>
		<li>
			<portal:portalLink displayTitle="true"
				title="Asset Depreciation Convention"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.cam.businessobject.AssetDepreciationConvention&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true"
				title="Asset Depreciation Method"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.cam.businessobject.AssetDepreciationMethod&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true"
				title="Asset Location Type"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.cam.businessobject.AssetLocationType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true"
				title="Asset Object Code"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.cam.businessobject.AssetObjectCode&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true"
				title="Asset Retirement Reason"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.cam.businessobject.AssetRetirementReason&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
        <li>
             <portal:portalLink displayTitle="true"
				title="Asset Status"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.cam.businessobject.AssetStatus&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
        <li>
            <portal:portalLink displayTitle="true" title="Asset Transaction Type"
                url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.cab.businessobject.AssetTransactionType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true"
				title="Asset Type"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.cam.businessobject.AssetType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true"
				title="Asset Payment Document Type"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.cam.businessobject.AssetPaymentDocumentType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
	</ul>
</div>
<channel:portalChannelBottom />
