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


<td class="content" valign="top">
      <maintenanceChannel:chartOfAccounts />
      <maintenanceChannel:financialProcessing />
      <maintenanceChannel:preDisbursementProcessor />
    <c:if test="${ConfigProperties.module.endowment.enabled == 'true'}">
      <maintenanceChannel:endowment />
    </c:if>
</td>
<td class="content" valign="top">
	<c:if test="${ConfigProperties.module.accounts.receivable.enabled == 'true'}">
      <maintenanceChannel:accountsReceivable />
    </c:if>
      <maintenanceChannel:vendor />
    <c:if test="${ConfigProperties.module.purchasing.enabled == 'true'}">
      <maintenanceChannel:purchasingAccountsPayable />
    </c:if>
    <c:if test="${ConfigProperties.module.capital.asset.enabled == 'true'}">
      <maintenanceChannel:capitalAsset />
    </c:if>
</td>
<td class="content" valign="top">
	<c:choose>
    <c:when test="${ConfigProperties['module.contracts.and.grants.enabled'] == 'true'}">
    	<maintenanceChannel:contractsAndGrants />
    </c:when>
    <c:when test="${ConfigProperties['module.external.kuali.coeus.enabled'] == 'true'}">
     	<maintenanceChannel:contractsAndGrants />
    </c:when>
   	</c:choose>
    <c:if test="${ConfigProperties.module.labor.distribution.enabled == 'true'}"> 
      <maintenanceChannel:effortCertification />
      <maintenanceChannel:laborDistribution /> 
      <maintenanceChannel:budgetConstruction /> 
    </c:if>
      <maintenanceChannel:system /> 
    <c:if test="${ConfigProperties.module.travel.enabled == 'true'}"> 
      <maintenanceChannel:tem />
    </c:if>
</td>
