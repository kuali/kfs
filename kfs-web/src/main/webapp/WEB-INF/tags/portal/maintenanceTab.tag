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


<td class="content" valign="top">
      <maintenanceChannel:chartOfAccounts />
      <maintenanceChannel:financialProcessing />
      <maintenanceChannel:preDisbursementProcessor />
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
    <c:if test="${fn:trim(ConfigProperties.environment) != fn:trim(ConfigProperties.production.environment.code)}">
	    <c:if test="${ConfigProperties.module.travel.enabled == 'true'}"> 
	      <maintenanceChannel:tem />
	    </c:if>
    </c:if>
</td>
