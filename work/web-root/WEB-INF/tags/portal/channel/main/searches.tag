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

<channel:portalChannelTop channelTitle="Searches" />
<div class="body">
	<portal:portalLink displayTitle="true" title="Financial Transactions" url="${ConfigProperties.workflow.url}/lookup.do?criteria.docTypeFullName=KualiFinancialDocument" /><br /><br />
	<strong>Contracts & Grants</strong><br/>
    <ul class="chan">
		<li><portal:portalLink displayTitle="true" title='Proposals' url='kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kew.docsearch.DocSearchCriteriaDTO&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true&docTypeFullName=PRPL'/></li>
	</ul>
	<strong>Financial Processing</strong><br/>
    <ul class="chan">
		<li><portal:portalLink displayTitle="true" title="Disbursement Vouchers" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kew.docsearch.DocSearchCriteriaDTO&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true&docTypeFullName=DV" /></li>
	</ul>
	<strong>Purchasing/Accounts Payable</strong><br/>
    <ul class="chan">
        <li><portal:portalLink displayTitle="true" title='Electronic Invoice Rejects' url='kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kew.docsearch.DocSearchCriteriaDTO&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true&docTypeFullName=EIRT'/></li>
        <li><portal:portalLink displayTitle="true" title='Payment Requests' url='kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kew.docsearch.DocSearchCriteriaDTO&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true&docTypeFullName=PREQ'/></li>
        <li><portal:portalLink displayTitle="true" title='Purchase Orders' url='kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kew.docsearch.DocSearchCriteriaDTO&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true&docTypeFullName=PO'/></li>
        <li><portal:portalLink displayTitle="true" title='Receiving' url='kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kew.docsearch.DocSearchCriteriaDTO&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true&docTypeFullName=RCVL'/></li>
        <li><portal:portalLink displayTitle="true" title='Requisitions' url='kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kew.docsearch.DocSearchCriteriaDTO&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true&docTypeFullName=REQS'/></li>
        <li><portal:portalLink displayTitle="true" title='Vendor Credit Memos' url='kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kew.docsearch.DocSearchCriteriaDTO&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true&docTypeFullName=CM'/></li>
     </ul>
 	<strong>Accounts Receivable</strong><br/>
    <ul class="chan">
        <li><portal:portalLink displayTitle="true" title='Customer Invoices' url='kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kew.docsearch.DocSearchCriteriaDTO&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true&docTypeFullName=INV'/></li>
        <li><portal:portalLink displayTitle="true" title='Customer Credit Memos' url='kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kew.docsearch.DocSearchCriteriaDTO&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true&docTypeFullName=CRM'/></li>
        <li><portal:portalLink displayTitle="true" title='Customer Invoice Writeoffs' url='kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kew.docsearch.DocSearchCriteriaDTO&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true&docTypeFullName=INVW'/></li>
        <li><portal:portalLink displayTitle="true" title='Cash Controls' url='kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kew.docsearch.DocSearchCriteriaDTO&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true&docTypeFullName=CTRL'/></li>
        <li><portal:portalLink displayTitle="true" title='Payment Applications' url='kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kew.docsearch.DocSearchCriteriaDTO&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true&docTypeFullName=APP'/></li>
    </ul>
 	<strong>Effort Certification</strong><br/>
    <ul class="chan">
        <li><portal:portalLink displayTitle="true" title='Effort Certification' url='kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kew.docsearch.DocSearchCriteriaDTO&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true&docTypeFullName=ECD'/></li>
    </ul>    
    </div>
<channel:portalChannelBottom />
