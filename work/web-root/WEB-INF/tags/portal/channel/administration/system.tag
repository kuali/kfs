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

<channel:portalChannelTop channelTitle="System" />
<div class="body">
    <ul class="chan">
		<li><portal:portalLink displayTitle="true" title="Data Mapping Field Definition" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.sys.businessobject.DataMappingFieldDefinition&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
		<%-- 
		<li><portal:portalLink displayTitle="true" title="Employee Status" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kns.bo.EmployeeStatus&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Employee Type" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kns.bo.EmployeeType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Financial System User" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kim.bo.Person&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
		--%>
		<li><portal:portalLink displayTitle="true" title="Functional Field Description" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.sys.businessobject.FunctionalFieldDescription&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Message Of The Day" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.fp.businessobject.MessageOfTheDay&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>					
        <li><portal:portalLink displayTitle="true" title="Parameter" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kns.bo.Parameter&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Parameter Component" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kns.bo.ParameterDetailType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Parameter Module" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kns.bo.ParameterNamespace&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Parameter Type" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kns.bo.ParameterType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="System Options" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.sys.businessobject.SystemOptions&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
		<%-- 
		<li><portal:portalLink displayTitle="true" title="Workflow User Removal / Replacement" url="${ConfigProperties.workflow.url}/RemoveReplace.do" /></li>
		--%>
        <li><portal:portalLink displayTitle="true" title="Document Type" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kew.doctype.bo.DocumentType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="KIM person lookup" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kim.bo.Person&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="KIM role lookup" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kim.bo.role.impl.KimRoleImpl&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="KIM permission lookup" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kim.bo.role.impl.KimPermissionImpl&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="KIM responsibility lookup" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kim.bo.role.impl.KimResponsibilityImpl&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
    </ul>
</div>
<channel:portalChannelBottom />       

