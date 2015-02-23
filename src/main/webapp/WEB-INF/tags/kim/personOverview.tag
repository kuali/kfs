<%--
 Copyright 2008-2009 The Kuali Foundation

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
<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<c:set var="personAttributes" value="${DataDictionary.IdentityManagementPersonDocument.attributes}" />

<kul:tab tabTitle="Overview" defaultOpen="true" transparentBackground="${!KualiForm.hasWorkflowDocument}" tabErrorKey="document.pr*,document.univ*,document.active,document.affiliations*">
    <c:set var="principalNameNote" value="" />
    <c:if test="${not readOnlyEntity}">
      <c:set var="principalNameNote" value="<br/><label class='fineprint'>(${personAttributes.principalName.label} must be lower case)</label>"/>
    </c:if>  
	<div class="tab-container" align="center">
		<table cellpadding="0" cellspacing="0" summary="">
		 	<tr>
		 	    <kim:cell inquiry="${inquiry}" isLabel="true" textAlign="right" attributeEntry="${personAttributes.entityId}" /> 
		 		<kim:cell inquiry="${inquiry}" property="document.entityId" attributeEntry="${personAttributes.entityId}" readOnly="true" />
				<kim:cell inquiry="${inquiry}" isLabel="true" textAlign="right" attributeEntry="${personAttributes.principalId}" /> 
		 		<kim:cell inquiry="${inquiry}" property="document.principalId" attributeEntry="${personAttributes.principalId}" readOnly="true" />
		 	</tr>
			<tr>
				<kim:cell inquiry="${inquiry}" isLabel="true" textAlign="right" attributeEntry="${personAttributes.principalName}" /> 
		 		<kim:cell inquiry="${inquiry}" property="document.principalName" attributeEntry="${personAttributes.principalName}" readOnly="${readOnlyEntity}" 
		 		          postText="${principalNameNote}" />
				<kim:cell inquiry="${inquiry}" isLabel="true" textAlign="right" attributeEntry="${personAttributes.active}" /> 
		 		<kim:cell inquiry="${inquiry}" property="document.active" attributeEntry="${personAttributes.active}" readOnly="${readOnlyEntity}" />
		 	</tr>
		</table>
		<kim:personAffln />
	</div>
</kul:tab>

