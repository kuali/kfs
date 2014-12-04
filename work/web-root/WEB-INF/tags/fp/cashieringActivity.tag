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

<c:set var="isDrawerOpen" value="${KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT] && (KualiForm.document.rawCashDrawerStatus == KFSConstants.CashDrawerConstants.STATUS_OPEN)}" />

<c:if test="${isDrawerOpen}">
	<kul:tab tabTitle="Cashiering Transactions" defaultOpen="true" tabErrorKey="${KFSConstants.EDIT_CASH_MANAGEMENT_CASHIERING_TRANSACTION_ERRORS}" >
		<div class="tab-container" align="center">
			<fp:cashieringTransaction />
      <p style="padding: 10px">
        <html:image src="${ConfigProperties.externalizable.images.url}buttonsmall_applytrans.gif" style="border: none" property="methodToCall.applyCashieringTransaction" title="Apply Cashiering Transaction" alt="Apply Cashiering Transaction" />
        &nbsp;&nbsp;&nbsp;&nbsp;
        <html:image src="${ConfigProperties.externalizable.images.url}buttonsmall_calculate.gif" style="border: none" title="Calculate Cashiering Transaction" alt="Calculate Cashiering Transaction" />
      </p>
		</div>
	</kul:tab>
</c:if>
