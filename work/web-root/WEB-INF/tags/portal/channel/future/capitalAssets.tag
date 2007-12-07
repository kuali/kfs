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

<channel:portalChannelTop channelTitle="Capital Asset Management" />
<div class="body">
    <ul class="chan">
	      <%--
	          TODO: these will eventually be portal links like the example below:
	               <portal:portalLink displayTitle="true" title="Application" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.gl.bo.CashBalance&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" />
	      --%> 
	      <li><portal:portalLink displayTitle="true" title="Asset Addition" url="camsAsset.do?methodToCall=docHandler&command=initiate&docTypeName=AssetDocument" /></li>
	      <li>Asset Location Maintenance</li>
	      <li>Asset Maintenance</li>
	      <li>Asset Merge</li>
	      <li><portal:portalLink displayTitle="true" title="Asset Payment" url="camsPayment.do?methodToCall=docHandler&command=initiate&docTypeName=PaymentDocument" /></li>
	      <li>Asset Retirement</li>
	      <li>Asset Security</li>
	      <li>Asset Separation</li>
	      <li>Asset System Manager Maintenance</li>
	      <li>Asset Tagging</li>
	      <li>Asset Transfer</li>
	      <li>Bar Code Inventory</li>
	      <li>Equipment Loan/Return</li>
	      <li>Fabrication Request</li>
	      <li>Pre-Asset Tagging</li>
    </ul>

    <strong>Capital Asset Builder</strong>
    <ul class="chan">
           <li>Movable Asset Additions - Purchase Order</li>
           <li>Movable Asset Additions - General Ledger</li>
           <li>Asset Create/View</li>
    </ul>
    </div>
<channel:portalChannelBottom />