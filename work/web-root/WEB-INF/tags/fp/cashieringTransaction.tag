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
