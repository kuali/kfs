<%--
 Copyright 2006 The Kuali Foundation.
 
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

<channel:portalChannelTop channelTitle="Endowment" />
<div class="body"> 
    <ul class="chan">
	      <%--
	          TODO: these will eventually be portal links like the example below:
	               <portal:portalLink displayTitle="true" title="Budget Construction Control" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.gl.bo.CashBalance&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" />
	      --%>
	    <li>Adjust Pooled Unit Holdings</li>
	    <li>Create Beneficiary Disbursement</li>
	    <li>Distribute Income/Spending Policy</li>
	    <li>Distribute Gains/Losses</li>
	    <li>Establish Pooled Spending Policy Rates</li>
	    <li>Maintain KEM Account (Create, Close, Modify)</li>
	    <li>Mark Investments to Market</li>
	    <li>Post Fees</li>
	    <li>Record Asset Purchase</li>
	    <li>Record Asset Sale</li>
	    <li>Record Income/Accruals</li>
	    <li>Record Management Fees</li>
	    <li>Transfer Funds</li>
	    <li>Update Pooled Unit Valuation</li>
    </ul>
    </div>
<channel:portalChannelBottom />
