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

<c:set var="documentAttributes"	value="${DataDictionary.TemCTSCardApplicationDocument.attributes}" />

<c:set var="documentTypeName" value="TemCTSCardApplicationDocument"/>
<c:set var="canEdit" value="${KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" scope="request" />

<c:set var="fullEntryMode" value="${KualiForm.editingMode['fullEntry']}" scope="request" />
<c:set var="documentTitle" value="" />
<kul:documentPage showDocumentInfo="true"
    documentTypeName="TemCTSCardApplicationDocument"
    htmlFormAction="temCTSCardApplication" renderMultipart="true"
    showTabButtons="true">
    <sys:documentOverview editingMode="${KualiForm.editingMode}" />
    <tem:profileTab />
    <tem:agreementTab property="userAgreement" documentAttribute="${documentAttributes.userAgreement}" text="${KualiForm.document.userAgreementText}" title="User Agreement" enable="${KualiForm.initiator && canEdit}" open="${KualiForm.initiator}" />
    <kul:tab tabTitle="Banking Information" defaultOpen="true">
    <div class="tab-container" align="center">
	    <table cellpadding=0 class="datatable" summary="Banking Information">
			<tr>
		    	<td colspan="2" class="tab-subhead">&nbsp;</td>
		    </tr>
		    <tr>
		    	<th scope=row class="bord-l-b">
		    		<div align="right">
		    			<kul:htmlAttributeLabel attributeEntry="${documentAttributes.bankAppliedDate}"/>
					</div>
				</th>
				<td class="datacell" width="50%">${KualiForm.document.bankAppliedDate }</td>
		    </tr>
		    <tr>
		    	<th scope=row class="bord-l-b">
		    		<div align="right">
		    			<kul:htmlAttributeLabel attributeEntry="${documentAttributes.bankApprovedDate}"/>
					</div>
				</th>
				<td class="datacell" width="50%">${KualiForm.document.bankApprovedDate }</td>
		    </tr>
	    </table>
    </div>
	
	</kul:tab>
    <kul:notes />
	<kul:adHocRecipients />
	<kul:routeLog />
	<kul:superUserActions />
	<kul:panelFooter />
    <sys:documentControls transactionalDocument="${documentEntry.transactionalDocument}" extraButtons="${KualiForm.extraButtons}"/>
</kul:documentPage>
