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
		<li><portal:portalLink displayTitle="true" title="Application Constants" url="${ConfigProperties.workflow.url}/ApplicationConstants.do" /></li>
		<li><portal:portalLink displayTitle="true" title="Document Operation" url="${ConfigProperties.workflow.url}/DocumentOperation.do" /></li>
		<li><portal:portalLink displayTitle="true" title="Document Type" url="${ConfigProperties.workflow.url}/Lookup.do?lookupableImplServiceName=DocumentTypeLookupableImplService" /></li>
		<li><portal:portalLink displayTitle="true" title="Rule Attribute" url="${ConfigProperties.workflow.url}/Lookup.do?lookupableImplServiceName=RuleAttributeLookupableImplService" /></li>
		<li><portal:portalLink displayTitle="true" title="Rule Template" url="${ConfigProperties.workflow.url}/Lookup.do?lookupableImplServiceName=RuleTemplateLookupableImplService" /></li>
		<li><portal:portalLink displayTitle="true" title="Statistics Report" url="${ConfigProperties.workflow.url}/Stats.do" /></li>
  		<li><portal:portalLink displayTitle="true" title="Workgroup Type" url="${ConfigProperties.workflow.url}/Lookup.do?lookupableImplServiceName=WorkgroupTypeLookup" /></li>
		<li><portal:portalLink displayTitle="true" title="XML Ingester" url="${ConfigProperties.workflow.url}/Ingester.do" /></li>
	</ul>
</div>
<channel:portalChannelBottom />

