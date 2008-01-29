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

<channel:portalChannelTop channelTitle="Pre-Award" />
<div class="body">
	<ul class="chan">
		<li>
			<portal:portalLink displayTitle="true" title="Appointment Type"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.kra.budget.bo.AppointmentType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true" title="Control Attribute Type"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.kra.routingform.bo.ControlAttributeType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true" title="Graduate Assistant"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.kra.budget.bo.GraduateAssistantRate&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true" title="Due Date Type"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.kra.routingform.bo.DueDateType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true" title="Indirect Cost Lookup"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.kra.budget.bo.IndirectCostLookup&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true" title="Keyword"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.kra.routingform.bo.Keyword&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true" title="Non-Personnel Category"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.kra.budget.bo.NonpersonnelCategory&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true"
				title="Non-Personnel Object Code"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.kra.budget.bo.NonpersonnelObjectCode&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true"
				title="Non-Personnel Sub-Category"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.kra.budget.bo.NonpersonnelSubCategory&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true" title="Research Risk Type"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.kra.routingform.bo.ResearchRiskType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true" title="Person Role"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.kra.routingform.bo.PersonRole&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true" title="Project Type"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.kra.routingform.bo.ProjectType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true" title="Purpose"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.kra.routingform.bo.Purpose&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true" title="Research Type"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.kra.routingform.bo.ResearchTypeCode&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
		<li>
			<portal:portalLink displayTitle="true" title="Question Type"
				url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.kra.routingform.bo.QuestionType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
		</li>
	</ul>
</div>
<channel:portalChannelBottom />
