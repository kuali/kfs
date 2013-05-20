<%--
 Copyright 2007-2009 The Kuali Foundation
 
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

<channel:portalChannelTop channelTitle="System" />
<div class="body">
    <ul class="chan">
        <li><portal:portalLink displayTitle="true" title="Bank" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.sys.businessobject.Bank&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Building" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.sys.businessobject.Building&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Origination Code" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.sys.businessobject.OriginationCode&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Payment Documentation Location" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.sys.businessobject.PaymentDocumentationLocation&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Room" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.sys.businessobject.Room&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Tax Region" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.sys.businessobject.TaxRegion&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Tax Region Type" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.sys.businessobject.TaxRegionType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Unit Of Measure" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.sys.businessobject.UnitOfMeasure&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="University Date" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.sys.businessobject.UniversityDate&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Wire Charge" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.sys.businessobject.WireCharge&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>        
    </ul>
</div>
<channel:portalChannelBottom />
