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

<channel:portalChannelTop channelTitle="Configuration" />
<div class="body">
	<strong>KFS</strong></br>
	<ul class="chan">
		<li><portal:portalLink displayTitle="true"
				title="Data Mapping Field Definition"
				url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.sys.businessobject.DataMappingFieldDefinition&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true"
				title="Functional Field Description"
				url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.sys.businessobject.FunctionalFieldDescription&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true"
				title="Message Of The Day"
				url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.fp.businessobject.MessageOfTheDay&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="System Options"
				url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.sys.businessobject.SystemOptions&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
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
	    <li><portal:portalLink displayTitle="true" title="Cache Admin" url="${ConfigProperties.application.url}/kr-krad/core/admin/cache?viewId=CacheAdmin-view1&methodToCall=start"/></li>
	</ul>
</div>
<channel:portalChannelBottom />
