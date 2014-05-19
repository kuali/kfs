<%--
 Copyright 2007 The Kuali Foundation
 
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

<%-- 
	Check a parameter to determine if the Contracts & Grants Billing enhancement is enabled. Certain links will be hidden in
    the portal if it is disabled.
--%>
<c:set var="contractsGrantsBillingEnabled" scope="session" value="<%=org.kuali.rice.coreservice.framework.CoreFrameworkServiceLocator.getParameterService().getParameterValueAsBoolean(org.kuali.kfs.sys.service.impl.KfsParameterConstants.ACCOUNTS_RECEIVABLE_ALL.class, org.kuali.kfs.module.ar.ArConstants.CG_BILLING_IND)%>"/>
<td class="content" valign="top">
      <mainChannel:transactions />
      <mainChannel:administrativeTransactions />
</td>
<td class="content" valign="top">
      <mainChannel:customDocumentSearches />
      <mainChannel:lookupAndMaintenance />
</td>
<td class="content" valign="top">
      <mainChannel:balanceInquiries />
      <mainChannel:reports />
      <mainChannel:yearEndTransactions />
</td> 
