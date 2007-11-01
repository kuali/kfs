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

<channel:portalChannelTop channelTitle="Pre-Disbursement Processor" />
<div class="body">
    <ul class="chan">
      <li><portal:portalLink displayTitle="true" title="ACH Bank" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.pdp.bo.AchBank&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true"/></li>
      <li><portal:portalLink displayTitle="true" title="Bank Maintenance" url="pdp/bank.do"/></li>
      <li><portal:portalLink displayTitle="true" title="Customer Profile Maintenance" url="pdp/customerprofile.do"/></li>
      <li><portal:portalLink displayTitle="true" title="Disbursement Range Maintenance" url="pdp/disbursementmaint.do"/></li>
      <li><portal:portalLink displayTitle="true" title="Format Checks/ACH" url="pdp/formatselection.do"/></li>
      <li><portal:portalLink displayTitle="true" title="Format Summary Review" url="pdp/formatsummary.do"/></li>
      <li><portal:portalLink displayTitle="true" title="Manually Upload Payment File" url="pdp/manualupload.do"/></li>
	  <li><portal:portalLink displayTitle="true" title="Payee ACH Account" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.pdp.bo.PayeeAchAccount&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true"/></li>
      <li><portal:portalLink displayTitle="true" title="Search for Batch" url="pdp/batchsearch.do"/></li>
      <li><portal:portalLink displayTitle="true" title="Search for Payment" url="pdp/paymentsearch.do"/></li>
    </ul>
</div>
<channel:portalChannelBottom />
