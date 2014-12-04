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
<kul:page showDocumentInfo="false"
	headerTitle="Balance Inquiry Report Menu" docTitle=""
	transactionalDocument="false"
	htmlFormAction="${KFSConstants.MAPPING_BALANCE_INQUIRY_REPORT_MENU}">
	<html:hidden property="backLocation" write="false" />
	<html:hidden property="docFormKey" write="false" />
	<html:hidden property="balanceInquiryReportMenuCallerDocFormKey"
		write="false" />
	<html:hidden property="chartOfAccountsCode" write="false" />
	<html:hidden property="universityFiscalYear" write="false" />
	<html:hidden property="accountNumber" write="false" />
	<html:hidden property="subAccountNumber" write="false" />
	<html:hidden property="financialObjectCode" write="false" />
	<html:hidden property="financialSubObjectCode" write="false" />
	<html:hidden property="objectTypeCode" write="false" />
	<html:hidden property="debitCreditCode" write="false" />
	<html:hidden property="referenceOriginCode" write="false" />
	<html:hidden property="referenceTypeCode" write="false" />
	<html:hidden property="referenceNumber" write="false" />
	<html:hidden property="projectCode" write="false" />
	<div class="topblurb">
	<div align="center"><b><font size="+1">Balance Inquiry Report Menu</font></b>
	<br />
	<br />
	<br />
	<table cellpadding=0 class="container2">
		<tr>
			<td><b>Available Balances</b>&nbsp;</td>
			<td><gl:balanceInquiryLookup
				boClassName="org.kuali.kfs.gl.businessobject.AccountBalance"
				actionPath="${KFSConstants.GL_MODIFIED_INQUIRY_ACTION}"
				lookupParameters="${KualiForm.availableBalancesBalanceInquiryLookupParameters}"
				hideReturnLink="true" /></td>
		</tr>
		<tr>
			<td><b>Balances by Consolidation</b>&nbsp;</td>
			<td><gl:balanceInquiryLookup
				boClassName="org.kuali.kfs.gl.businessobject.AccountBalanceByConsolidation"
				actionPath="${KFSConstants.GL_ACCOUNT_BALANCE_BY_CONSOLIDATION_LOOKUP_ACTION}"
				lookupParameters="${KualiForm.balancesByConsolidationBalanceInquiryLookupParameters}"
				hideReturnLink="true" /></td>
		</tr>
		<tr>
			<td><b>Cash Balances</b>&nbsp;</td>
			<td><gl:balanceInquiryLookup
				boClassName="org.kuali.kfs.gl.businessobject.CashBalance"
				actionPath="${KFSConstants.GL_MODIFIED_INQUIRY_ACTION}"
				lookupParameters="${KualiForm.cashBalancesBalanceInquiryLookupParameters}"
				hideReturnLink="true" /></td>
		</tr>
		<tr>
			<td><b>General Ledger Balance</b>&nbsp;</td>
			<td><gl:balanceInquiryLookup
				boClassName="org.kuali.kfs.gl.businessobject.Balance"
				actionPath="${KFSConstants.GL_BALANCE_INQUIRY_ACTION}"
				lookupParameters="${KualiForm.generalLedgerBalanceBalanceInquiryLookupParameters}"
				hideReturnLink="true" /></td>
		</tr>
		<tr>
			<td><b>General Ledger Entry</b>&nbsp;</td>
			<td><gl:balanceInquiryLookup
				boClassName="org.kuali.kfs.gl.businessobject.Entry"
				actionPath="${KFSConstants.GL_MODIFIED_INQUIRY_ACTION}"
				lookupParameters="${KualiForm.generalLedgerEntryBalanceInquiryLookupParameters}"
				hideReturnLink="true" /></td>
		</tr>
		<tr>
			<td><b>General Ledger Pending Entry</b>&nbsp;</td>
			<td><gl:balanceInquiryLookup
				boClassName="org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry"
				actionPath="${KFSConstants.GL_MODIFIED_INQUIRY_ACTION}"
				lookupParameters="${KualiForm.generalLedgerPendingEntryBalanceInquiryLookupParameters}"
				hideReturnLink="true" /></td>
		</tr>
		<tr>
			<td><b>Open Encumbrances</b>&nbsp;</td>
			<td><gl:balanceInquiryLookup
				boClassName="org.kuali.kfs.gl.businessobject.Encumbrance"
				actionPath="${KFSConstants.GL_MODIFIED_INQUIRY_ACTION}"
				lookupParameters="${KualiForm.openEncumbrancesBalanceInquiryLookupParameters}"
				hideReturnLink="true" /></td>
		</tr>
		<tr>
			<td colspan="2">&nbsp;</td>
		</tr>
		<tr>
			<td align="center" colspan="2"><html:image
				src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_cancel.gif" styleClass="globalbuttons"
				property="methodToCall.cancel" alt="cancel" title="cancel" /></td>
		</tr>
	</table>
	</div>
	</div>
</kul:page>
