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

<channel:portalChannelTop channelTitle="Accounts Receivable" />
<div class="body">
    <ul class="chan">
	      <li><portal:portalLink displayTitle="true" title="Payment Application" url="arPaymentApplicationDocument.do?methodToCall=docHandler&command=initiate&docTypeName=PaymentApplicationDocument" /></li>
	      <li><portal:portalLink displayTitle="true" title="Cash Control" url="arCashControlDocument.do?methodToCall=docHandler&command=initiate&docTypeName=CashControlDocument" /></li>
	      <li><portal:portalLink displayTitle="true" title="Credit Memo" url="arCustomerCreditMemoDocument.do?methodToCall=docHandler&command=initiate&docTypeName=CustomerCreditMemoDocument" /></li>
	      <li>Customer</li>
	      <li><portal:portalLink displayTitle="true" title="Customer Invoice" url="arCustomerInvoiceDocument.do?methodToCall=docHandler&command=initiate&docTypeName=CustomerInvoiceDocument" /></li>
	      <li>Organization Accounting Defaults</li>
	      <li>Organization Options</li>
	</ul>
</div>
<channel:portalChannelBottom />
