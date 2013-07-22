<%--
 Copyright 2006-2008 The Kuali Foundation
 
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

<c:set var="documentAttributes"	value="${DataDictionary.TemCorporateCardApplicationDocument.attributes}" />

<c:set var="documentTypeName" value="TemCorporateCardApplicationDocument"/>
<c:set var="canEdit" value="${KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" scope="request" />

<c:set var="fullEntryMode" value="${KualiForm.editingMode['fullEntry']}" scope="request" />
<c:set var="documentTitle" value="" />
<kul:documentPage showDocumentInfo="true"
    documentTypeName="TemCorporateCardApplicationDocument"
    htmlFormAction="temCorporateCardApplication" renderMultipart="true"
    showTabButtons="true">
    <sys:documentOverview editingMode="${KualiForm.editingMode}" />
    <tem:profileTab />
    <tem:agreementTab property="userAgreement" documentAttribute="${documentAttributes.userAgreement}" text="${KualiForm.document.userAgreementText}" title="User Agreement" enable="${KualiForm.initiator && canEdit}" open="${KualiForm.initiator}" />
    <tem:agreementTab property="departmentHeadAgreement" documentAttribute="${documentAttributes.departmentHeadAgreement}" text="${KualiForm.document.departmentHeadAgreementText}" title="Department Head Agreement" enable="${KualiForm.fiscalOfficer && canEdit}" open="${KualiForm.fiscalOfficer}" />
    <c:if test="${KualiForm.appliedToBank }" >
    <kul:tab tabTitle="Banking Information" defaultOpen="true">
    <div class="tab-container" align="center">
		<table cellpadding=0 class="datatable" summary="Banking Information">
			<tr>
		    	<td colspan="2" class="tab-subhead">&nbsp;</td>
		    </tr>
		    <tr>
		    	<th scope=row class="bord-l-b">
		    		<div align="right">
		    			<kul:htmlAttributeLabel attributeEntry="${documentAttributes.pseudoNumber}"/>
					</div>
				</th>
				<td class="datacell" width="50%">${KualiForm.document.pseudoNumber }</td>
		    </tr>
		    </table>
		   </div>
	</kul:tab>
	</c:if>
    <kul:notes />
	<kul:adHocRecipients />
	<kul:routeLog />
	<kul:superUserActions />
	<kul:panelFooter />
    <sys:documentControls transactionalDocument="${documentEntry.transactionalDocument}" extraButtons="${KualiForm.extraButtons}"/>
</kul:documentPage>