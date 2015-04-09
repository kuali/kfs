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
