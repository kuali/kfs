<%--
 Copyright 2007-2013 The Kuali Foundation
 
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
<c:set var="documentAttributes" value="${DataDictionary.TravelAuthorizationDocument.attributes}" />
<c:set var="travelAdvanceAttributes" value="${DataDictionary.TravelAdvance.attributes}" />
<c:set var="advanceAttributes" value="${DataDictionary.AdvancePaymentReason.attributes}" />
<c:set var="docType" value="${KualiForm.document.dataDictionaryEntry.documentTypeName }" />
<c:set var="policyDisabled" value="${!KualiForm.waitingOnTraveler && !fullEntryMode}" />

<kul:tab tabTitle="Travel Advances" defaultOpen="true" tabErrorKey="${TemKeyConstants.TRVL_AUTH_TRVL_ADVANCE_ERRORS}">
	<div class="tab-container" align="left">
		<h3>Travel Advance</h3>
		<tem-ta:travelAdvance travelAdvanceProperty="document.travelAdvance"/>
		
		<c:if test="${KualiForm.showPolicy}"><div align="right">${KualiForm.policyURL}</div></c:if>
	</div>
</kul:tab>
