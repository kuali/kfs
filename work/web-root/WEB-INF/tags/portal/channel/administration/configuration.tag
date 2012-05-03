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

<channel:portalChannelTop channelTitle="Configuration" />
<div class="body">
	<strong>KFS</strong></br>
	<ul class="chan">
		<li><portal:portalLink displayTitle="true"
				title="Data Mapping Field Definition"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.sys.businessobject.DataMappingFieldDefinition&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true"
				title="Functional Field Description"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.sys.businessobject.FunctionalFieldDescription&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true"
				title="Message Of The Day"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.fp.businessobject.MessageOfTheDay&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="System Options"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.sys.businessobject.SystemOptions&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
	</ul>
	<strong>Workflow</strong> </br>
	<ul class="chan">
		<li><portal:portalLink displayTitle="true" title="Document Type"
				url="${ConfigProperties.kr.url}/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kew.doctype.bo.DocumentType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true"
				title="Routing & Identity Management Document Type Hierarchy"
				url="${ConfigProperties.workflow.url}/RuleQuickLinks.do" /></li>
		<li><portal:portalLink displayTitle="true"
				title="Workflow Attribute"
				url="${ConfigProperties.kr.url}/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kew.rule.bo.RuleAttribute&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="XML Ingester"
				url="${ConfigProperties.core.url}/Ingester.do" /></li>
	</ul>
	<strong>Parameters</strong> </br>
	<ul class="chan">
		<li><portal:portalLink displayTitle="true" title="Namespace"
				url="${ConfigProperties.kr.url}/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.coreservice.impl.namespace.NamespaceBo&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Parameter"
				url="${ConfigProperties.kr.url}/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.coreservice.impl.parameter.ParameterBo&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true"
				title="Parameter Component"
				url="${ConfigProperties.kr.url}/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.coreservice.impl.component.ComponentBo&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Parameter Type"
				url="${ConfigProperties.kr.url}/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.coreservice.impl.parameter.ParameterTypeBo&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
	</ul>
	<strong>Technical</strong> </br>
	<ul class="chan">
		<li><portal:portalLink displayTitle="true"
				title="Configuration Viewer"
				url="${ConfigProperties.ksb.client.url}/ConfigViewer.do" /></li>
		<c:if test="${ConfigProperties.ksb.mode != 'LOCAL'}">
			<li><portal:portalLink displayTitle="true"
					title="Rice Server Configuration Viewer"
					url="${ConfigProperties.ksb.server.url}/ConfigViewer.do" /></li>
		</c:if>
	    <li><portal:portalLink displayTitle="true" title="Cache Admin" url="${ConfigProperties.krad.url}/core/admin/cache?viewId=CacheAdmin-view1&methodToCall=start"/></li>
	</ul>
</div>
<channel:portalChannelBottom />
