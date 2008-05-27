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
	<portal:portalLink displayTitle="true" title="Financial Transactions" url="${ConfigProperties.workflow.url}/DocumentSearch.do?criteria.docTypeFullName=KualiFinancialDocument" /><br /><br />
	<strong>Financial Processing</strong><br/>
    <ul class="chan">
		<li><portal:portalLink displayTitle="true" title="Disbursement Vouchers" url="${ConfigProperties.workflow.url}/DocumentSearch.do?criteria.docTypeFullName=DisbursementVoucherDocument" /></li>
	</ul>
	<strong>Post Award</strong><br/>
    <ul class="chan">
		<li><portal:portalLink displayTitle="true" title='Proposals' url='${ConfigProperties.workflow.url}/DocumentSearch.do?criteria.docTypeFullName=ProposalMaintenanceDocument'/></li>
	</ul>
	<strong>Purchasing/Accounts Payable</strong><br/>
    <ul class="chan">
        <li><portal:portalLink displayTitle="true" title='Credit Memos' url='${ConfigProperties.workflow.url}/DocumentSearch.do?criteria.docTypeFullName=CreditMemoDocument'/></li>
        <li><portal:portalLink displayTitle="true" title='Payment Requests' url='${ConfigProperties.workflow.url}/DocumentSearch.do?criteria.docTypeFullName=PaymentRequestDocument'/></li>
        <li><portal:portalLink displayTitle="true" title='Purchase Orders' url='${ConfigProperties.workflow.url}/DocumentSearch.do?criteria.docTypeFullName=PurchaseOrderDocument'/></li>
        <li><portal:portalLink displayTitle="true" title='Requisitions' url='${ConfigProperties.workflow.url}/DocumentSearch.do?criteria.docTypeFullName=RequisitionDocument'/></li>
     </ul>
 	<strong>Accounts Receivable</strong><br/>
    <ul class="chan">
        <li><portal:portalLink displayTitle="true" title='Customer Invoices' url='${ConfigProperties.workflow.url}/DocumentSearch.do?criteria.docTypeFullName=CustomerInvoiceDocument'/></li>
     </ul>
    </div>
<channel:portalChannelBottom />