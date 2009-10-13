<%--
 Copyright 2005-2008 The Kuali Foundation
 
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
<c:set var="generalLedgerPendingEntriesList"
	value="${KualiForm.document.newMaintainableObject.businessObject.generalLedgerPendingEntries}" />
<c:if test="${!empty generalLedgerPendingEntriesList}">
	<gl:generalLedgerPendingEntries
		generalLedgerPendingEntries="${generalLedgerPendingEntriesList}"
		generalLedgerPendingEntryProperty="document.newMaintainableObject.businessObject.generalLedgerPendingEntries"
		generalLedgerPendingEntriesProperty="document.newMaintainableObject.businessObject.generalLedgerPendingEntries" />
</c:if>
<c:if test="${empty generalLedgerPendingEntriesList}">
	<kul:tab tabTitle="General Ledger Pending Entries" defaultOpen="false"
		tabErrorKey="${KFSConstants.GENERAL_LEDGER_PENDING_ENTRIES_TAB_ERRORS}">
		<div class="tab-container" align=center>
		<h3>General Ledger Pending Entries <kul:lookup
			boClassName="org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry"
			lookupParameters="document.documentNumber:documentNumber"
			hideReturnLink="true" suppressActions="true" /></h3>
		<table cellpadding="0" cellspacing="0" class="datatable"
			summary="view/edit pending entries">
			<tr>
				<td class="datacell" height="50" colspan="12">
				<div align="center">There are currently no General Ledger Pending Entries associated with this Transaction Processing document.</div>
				</td>
			</tr>
		</table>
		</div>
	</kul:tab>
</c:if>
