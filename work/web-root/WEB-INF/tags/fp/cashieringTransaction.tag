<%--
 Copyright 2007-2009 The Kuali Foundation
 
 Licensed under the Educational Community License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl2.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<kul:htmlControlAttribute property="document.currentTransaction.referenceFinancialDocumentNumber" attributeEntry="${DataDictionary.CashieringTransaction.attributes.referenceFinancialDocumentNumber}" />
<h5>Money In</h5>
<div style="padding: 5px;">
    <h3>Currency/Coin</h3>
  <fp:currencyCoinLine currencyProperty="document.currentTransaction.moneyInCurrency" coinProperty="document.currentTransaction.moneyInCoin" editingMode="${KualiForm.editingMode}" />
</div>
<div style="padding: 5px;">
    <h3>New Miscellaneous Advance</h3>
<table border="0" cellspacing="0" cellpadding="0" class="datatable">
  <fp:miscAdvanceHeader itemInProcessProperty="document.currentTransaction.newItemInProcess" creatingItemInProcess="true" />
  <fp:miscAdvanceLine itemInProcessProperty="document.currentTransaction.newItemInProcess" creatingItemInProcess="true" />
</table>
</div>
<div style="padding: 5px;">
    <h3>Cashiering Checks</h3>
  <fp:checkLines checkDetailMode="true" editingMode="${KualiForm.editingMode}" displayHidden="false" />
</div>

<h5>Money Out</h5>
<div style="padding: 5px;">
    <h3>Currency/Coin</h3>
  <fp:currencyCoinLine currencyProperty="document.currentTransaction.moneyOutCurrency" coinProperty="document.currentTransaction.moneyOutCoin" editingMode="${KualiForm.editingMode}" />
</div>
<div style="padding: 5px;">
    <h3>Open Miscellaneous Advances</h3>
  <fp:openMiscAdvanceLines />
</div>
