<%--
 Copyright 2007 The Kuali Foundation.
 
 Licensed under the Educational Community License, Version 1.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl1.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>


<td class="content" valign="top">
      <maintenanceChannel:workflow />
      <maintenanceChannel:chartOfAccounts />
      <maintenanceChannel:laborDistribution /> 
      <maintenanceChannel:budgetConstruction /> 
      <maintenanceChannel:capitalAsset /> 
</td>
<td class="content" valign="top">
      <maintenanceChannel:financialProcessing />
      <maintenanceChannel:preAward />
      <maintenanceChannel:postAward />
      <maintenanceChannel:effortCertification /> 
      <maintenanceChannel:capitalAssetBuilder /> 
</td>
<td class="content" valign="top">
      <maintenanceChannel:vendor />
      <maintenanceChannel:purchasingAccountsPayable />
      <maintenanceChannel:purchasingAccountsReceivable />
      <maintenanceChannel:preDisbursementProcessor />
</td>
