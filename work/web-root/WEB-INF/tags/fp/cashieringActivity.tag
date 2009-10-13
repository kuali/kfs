<%--
 Copyright 2007-2009 The Kuali Foundation
 
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

<c:set var="isDrawerOpen" value="${KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT] && (KualiForm.document.rawCashDrawerStatus == KFSConstants.CashDrawerConstants.STATUS_OPEN)}" />

<c:if test="${isDrawerOpen}">
	<kul:tab tabTitle="Cashiering Transactions" defaultOpen="true" tabErrorKey="${KFSConstants.EDIT_CASH_MANAGEMENT_CASHIERING_TRANSACTION_ERRORS}" >
		<div class="tab-container" align="center">
			<fp:cashieringTransaction />
      <p style="padding: 10px">
        <html:image src="${ConfigProperties.externalizable.images.url}buttonsmall_applytrans.gif" style="border: none" property="methodToCall.applyCashieringTransaction" title="Apply Cashiering Transaction" alt="Apply Cashiering Transaction" />
      </p>
		</div>
	</kul:tab>
</c:if>
