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

<channel:portalChannelTop channelTitle="Balance Inquiries" />
<div class="body">
 	<strong>General Ledger</strong><br />
    <ul class="chan">
		<li><portal:portalLink displayTitle="true" title="Available Balances" url="${KFSConstants.GL_MODIFIED_INQUIRY_ACTION}?methodToCall=start&businessObjectClassName=org.kuali.module.gl.bo.AccountBalance&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
	    <li><portal:portalLink displayTitle="true" title="Balances by Consolidation" url="${KFSConstants.GL_ACCOUNT_BALANCE_BY_CONSOLIDATION_LOOKUP_ACTION}?methodToCall=start&businessObjectClassName=org.kuali.module.gl.bo.AccountBalanceByConsolidation&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
	   	<li><portal:portalLink displayTitle="true" title="Cash Balances" url="${KFSConstants.GL_MODIFIED_INQUIRY_ACTION}?methodToCall=start&businessObjectClassName=org.kuali.module.gl.bo.CashBalance&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
	    <li><portal:portalLink displayTitle="true" title="General Ledger Balance" url="${KFSConstants.GL_BALANCE_INQUIRY_ACTION}?methodToCall=start&businessObjectClassName=org.kuali.module.gl.bo.Balance&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="General Ledger Entry" url="${KFSConstants.GL_MODIFIED_INQUIRY_ACTION}?methodToCall=start&businessObjectClassName=org.kuali.module.gl.bo.Entry&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="General Ledger Pending Entry" url="${KFSConstants.GL_MODIFIED_INQUIRY_ACTION}?methodToCall=start&businessObjectClassName=org.kuali.kfs.bo.GeneralLedgerPendingEntry&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
	   	<li><portal:portalLink displayTitle="true" title="Open Encumbrances" url="${KFSConstants.GL_MODIFIED_INQUIRY_ACTION}?methodToCall=start&businessObjectClassName=org.kuali.module.gl.bo.Encumbrance&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
    </ul>
    
 	<strong>Labor Distribution</strong><br />
        <ul class="chan">
	<li><portal:portalLink displayTitle="true" title="Account Status (Base Funds)" url="${KFSConstants.GL_MODIFIED_INQUIRY_ACTION}?methodToCall=start&businessObjectClassName=org.kuali.module.labor.bo.AccountStatusBaseFunds&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>    
	<li><portal:portalLink displayTitle="true" title="Account Status (Current Funds)" url="${KFSConstants.GL_MODIFIED_INQUIRY_ACTION}?methodToCall=start&businessObjectClassName=org.kuali.module.labor.bo.AccountStatusCurrentFunds&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>     
	<li><portal:portalLink displayTitle="true" title="Calculated Salary Foundation" url="${KFSConstants.GL_MODIFIED_INQUIRY_ACTION}?methodToCall=start&businessObjectClassName=org.kuali.module.labor.bo.LaborCalculatedSalaryFoundationTracker&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
	<li><portal:portalLink displayTitle="true" title="Employee Funding" url="${KFSConstants.GL_MODIFIED_INQUIRY_ACTION}?methodToCall=start&businessObjectClassName=org.kuali.module.labor.bo.EmployeeFunding&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>     
    <li><portal:portalLink displayTitle="true" title="July 1 Position Funding" url="${KFSConstants.GL_MODIFIED_INQUIRY_ACTION}?methodToCall=start&businessObjectClassName=org.kuali.module.labor.bo.July1PositionFunding&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>  
	<li><portal:portalLink displayTitle="true" title="Labor Ledger View" url="${KFSConstants.GL_BALANCE_INQUIRY_ACTION}?methodToCall=start&businessObjectClassName=org.kuali.module.labor.bo.LedgerBalance&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true&financialBalanceTypeCode=AC" /></li>  
	<li><portal:portalLink displayTitle="true" title="Labor Ledger Pending Entry" url="${KFSConstants.GL_MODIFIED_INQUIRY_ACTION}?methodToCall=start&businessObjectClassName=org.kuali.module.labor.bo.LaborLedgerPendingEntry&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>  
	<li><portal:portalLink displayTitle="true" title="Position Inquiry" url="${KFSConstants.GL_MODIFIED_INQUIRY_ACTION}?methodToCall=start&businessObjectClassName=org.kuali.module.labor.bo.PositionData&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>  
    </ul>
</div>
<channel:portalChannelBottom />