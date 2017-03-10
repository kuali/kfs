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

<channel:portalChannelTop channelTitle="Balance Inquiries" />
<div class="body">
 	<strong>General Ledger</strong><br />
    <ul class="chan">
		<li><portal:portalLink displayTitle="true" title="Available Balances" url="${ConfigProperties.application.url}/${KFSConstants.GL_MODIFIED_INQUIRY_ACTION}?methodToCall=start&businessObjectClassName=org.kuali.kfs.gl.businessobject.AccountBalance&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
	    <li><portal:portalLink displayTitle="true" title="Balances by Consolidation" url="${ConfigProperties.application.url}/${KFSConstants.GL_ACCOUNT_BALANCE_BY_CONSOLIDATION_LOOKUP_ACTION}?methodToCall=start&businessObjectClassName=org.kuali.kfs.gl.businessobject.AccountBalanceByConsolidation&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
	   	<li><portal:portalLink displayTitle="true" title="Cash Balances" url="${ConfigProperties.application.url}/${KFSConstants.GL_MODIFIED_INQUIRY_ACTION}?methodToCall=start&businessObjectClassName=org.kuali.kfs.gl.businessobject.CashBalance&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
	   	<li><portal:portalLink displayTitle="true" title="Current Account Balances" url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.gl.businessobject.CurrentAccountBalance&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
	    <li><portal:portalLink displayTitle="true" title="General Ledger Balance" url="${ConfigProperties.application.url}/${KFSConstants.GL_BALANCE_INQUIRY_ACTION}?methodToCall=start&businessObjectClassName=org.kuali.kfs.gl.businessobject.Balance&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="General Ledger Entry" url="${ConfigProperties.application.url}/${KFSConstants.GL_MODIFIED_INQUIRY_ACTION}?methodToCall=start&businessObjectClassName=org.kuali.kfs.gl.businessobject.Entry&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="General Ledger Pending Entry" url="${ConfigProperties.application.url}/${KFSConstants.GL_MODIFIED_INQUIRY_ACTION}?methodToCall=start&businessObjectClassName=org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
	   	<li><portal:portalLink displayTitle="true" title="Open Encumbrances" url="${ConfigProperties.application.url}/${KFSConstants.GL_MODIFIED_INQUIRY_ACTION}?methodToCall=start&businessObjectClassName=org.kuali.kfs.gl.businessobject.Encumbrance&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
    </ul>
<c:if test="${ConfigProperties.module.labor.distribution.enabled == 'true'}">    
 	<strong>Labor Distribution</strong><br />
        <ul class="chan">
	<li><portal:portalLink displayTitle="true" title="Account Status (Base Funds)" url="${ConfigProperties.application.url}/${KFSConstants.GL_MODIFIED_INQUIRY_ACTION}?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ld.businessobject.AccountStatusBaseFunds&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>    
	<li><portal:portalLink displayTitle="true" title="Account Status (Current Funds)" url="${ConfigProperties.application.url}/${KFSConstants.GL_MODIFIED_INQUIRY_ACTION}?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ld.businessobject.AccountStatusCurrentFunds&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>     
	<c:if test="${fn:trim(ConfigProperties.environment) != fn:trim(ConfigProperties.production.environment.code)}">
		<li><portal:portalLink displayTitle="true" title="Calculated Salary Foundation" url="${ConfigProperties.application.url}/${KFSConstants.GL_MODIFIED_INQUIRY_ACTION}?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ld.businessobject.LaborCalculatedSalaryFoundationTracker&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Employee Funding" url="${ConfigProperties.application.url}/${KFSConstants.GL_MODIFIED_INQUIRY_ACTION}?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ld.businessobject.EmployeeFunding&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>     
	    <li><portal:portalLink displayTitle="true" title="July 1 Position Funding" url="${ConfigProperties.application.url}/${KFSConstants.GL_MODIFIED_INQUIRY_ACTION}?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ld.businessobject.July1PositionFunding&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>  
	</c:if>
	<li><portal:portalLink displayTitle="true" title="Labor Ledger View" url="${ConfigProperties.application.url}/${KFSConstants.GL_BALANCE_INQUIRY_ACTION}?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ld.businessobject.LedgerBalance&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true&financialBalanceTypeCode=AC" /></li>  
	<li><portal:portalLink displayTitle="true" title="Labor Ledger Pending Entry" url="${ConfigProperties.application.url}/${KFSConstants.GL_MODIFIED_INQUIRY_ACTION}?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ld.businessobject.LaborLedgerPendingEntry&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>  
	<c:if test="${fn:trim(ConfigProperties.environment) != fn:trim(ConfigProperties.production.environment.code)}">
		<li><portal:portalLink displayTitle="true" title="Position Inquiry" url="${ConfigProperties.application.url}/${KFSConstants.GL_MODIFIED_INQUIRY_ACTION}?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ld.businessobject.PositionData&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>  
    </c:if>
    </ul>
</c:if>
</div>
<channel:portalChannelBottom />
