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

<channel:portalChannelTop channelTitle="Workflow" />
<div class="body">

  	<ul class="chan">				
		<li><portal:portalLink displayTitle="true" title="Preferences" url="${ConfigProperties.workflow.url}/Preferences.do" /></li>
		<li><portal:portalLink displayTitle="true" title="Routing Report" url="${ConfigProperties.workflow.url}/RoutingReport.do" /></li>
		<li><portal:portalLink displayTitle="true" title="Rules" url="${ConfigProperties.workflow.url}/Lookup.do?lookupableImplServiceName=RuleBaseValuesLookupableImplService" /></li>
		<li><portal:portalLink displayTitle="true" title="Rule QuickLinks" url="${ConfigProperties.workflow.url}/RuleQuickLinks.do" /></li>
		<li><portal:portalLink displayTitle="true" title="Workgroup" url="${ConfigProperties.workflow.url}/Lookup.do?lookupableImplServiceName=WorkGroupLookupableImplService" /></li>
	</ul>
</div>
<channel:portalChannelBottom />