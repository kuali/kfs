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

<c:set var="docPrivacyAttributes" value="${DataDictionary.PersonDocumentPrivacy.attributes}" />

<c:set var="canModifyPrivacyPreferences" scope="request" value="${KualiForm.canOverrideEntityPrivacyPreferences && !readOnly}" />

<kul:tab tabTitle="Privacy Preferences" defaultOpen="false" tabErrorKey="document.privacy*">
	<div class="tab-container" align="center">
		<table cellpadding="0" cellspacing="0" summary=""> 
	 		<tr>
	 		    <kim:cell cellWidth="30%" inquiry="${inquiry}" isLabel="true" textAlign="right" attributeEntry="${docPrivacyAttributes.suppressName}" /> 
		 		<kim:cell inquiry="${inquiry}" cellWidth="20%" textAlign="center" property="document.privacy.suppressName" attributeEntry="${docPrivacyAttributes.suppressName}" readOnly="${!canModifyPrivacyPreferences}" />
  				<kim:cell cellWidth="30%" inquiry="${inquiry}" isLabel="true" textAlign="right" attributeEntry="${docPrivacyAttributes.suppressAddress}" /> 
		 		<kim:cell inquiry="${inquiry}" cellWidth="20%" textAlign="center" property="document.privacy.suppressAddress" attributeEntry="${docPrivacyAttributes.suppressAddress}" readOnly="${!canModifyPrivacyPreferences}" />
	 		</tr>
	 		<tr>
	 		    <kim:cell cellWidth="30%" inquiry="${inquiry}" isLabel="true" textAlign="right" attributeEntry="${docPrivacyAttributes.suppressPersonal}" />
		 		<kim:cell inquiry="${inquiry}" cellWidth="20%" textAlign="center" property="document.privacy.suppressPersonal" attributeEntry="${docPrivacyAttributes.suppressPersonal}" readOnly="${!canModifyPrivacyPreferences}" />
  				<kim:cell cellWidth="30%" inquiry="${inquiry}" isLabel="true" textAlign="right" attributeEntry="${docPrivacyAttributes.suppressEmail}" />
		 		<kim:cell inquiry="${inquiry}" cellWidth="20%" textAlign="center" property="document.privacy.suppressEmail" attributeEntry="${docPrivacyAttributes.suppressEmail}" readOnly="${!canModifyPrivacyPreferences}" />
	 		</tr>
	 		<tr>
  				<kim:cell cellWidth="30%" inquiry="${inquiry}" isLabel="true" textAlign="right" attributeEntry="${docPrivacyAttributes.suppressPhone}" />
		 		<kim:cell inquiry="${inquiry}" cellWidth="20%" textAlign="center" property="document.privacy.suppressPhone" attributeEntry="${docPrivacyAttributes.suppressPhone}" readOnly="${!canModifyPrivacyPreferences}" />
		 		<th></th>
		 		<td></td>
	 		</tr>
		</table> 		
	</div>
</kul:tab>
