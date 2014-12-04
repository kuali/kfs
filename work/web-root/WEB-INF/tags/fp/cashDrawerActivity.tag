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

<c:set var="cmDocAttributes" value="${DataDictionary.CashManagementDocument.attributes}" />
<c:set var="drawerAttributes" value="${DataDictionary.CashManagementDocument.attributes}" />
<c:set var="dummyAttributes" value="${DataDictionary.DummyBusinessObject.attributes}" />
<c:set var="cashDrawerAttributes" value="${DataDictionary.CashDrawer.attributes}" />

<c:set var="allowOpen" value="${(KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]) && KualiForm.allowOpenCashDrawer}" />
<c:set var="allowRefresh" value="${KualiForm.document.rawCashDrawerStatus != KFSConstants.CashDrawerConstants.STATUS_CLOSED}" />

<c:set var="drawer" value="${KualiForm.document.cashDrawer}" />

<style>
    td {white-space: nowrap}
    th {white-space: nowrap}
</style>

<c:set var="subheading" value="Cash Drawer Activity" />
<c:if test="${!empty KualiForm.cashDrawerSummary}">
    <c:set var="subheading" value="${subheading} as of ${KualiForm.cashDrawerSummary.timeRefreshed}" />
</c:if>


<kul:tab tabTitle="Cash Drawer Activity" defaultOpen="true" tabErrorKey="${KFSConstants.EDIT_CASH_MANAGEMENT_CASHIERING_TRANSACTION_ERRORS}" >
    <div class="tab-container" align=center>
	  <table cellspacing="0" cellpadding="0" border="0" class="datatable">
		  <tr>
			  <td width="50%" valign="top">
            			  <h3>${subheading}</h3>

        			  <table cellspacing=0 cellpadding=0 border=0 class="datatable">
            			<tr>
			                  <td class="infoline" colspan=3>
                    				<kul:htmlAttributeLabel labelFor="document.campusCode" attributeEntry="${cmDocAttributes.campusCode}" readOnly="true" skipHelpUrl="true" noColon="true" />
                				</td>
                				<td colspan=2>
                    				<kul:htmlControlAttribute property="document.campusCode" attributeEntry="${cmDocAttributes.campusCode}" readOnly="true" />
                				</td>
            			</tr>

            			<tr>
                				<td class="infoline" colspan=3>
                    				<kul:htmlAttributeLabel labelFor="document.cashDrawerStatus" attributeEntry="${cmDocAttributes.cashDrawerStatus}" readOnly="true" skipHelpUrl="true" noColon="true" />
                				</td>
                				<td colspan=2>
                    				<kul:htmlControlAttribute property="document.cashDrawerStatus" attributeEntry="${cmDocAttributes.cashDrawerStatus}" readOnly="true"/>
                    				<c:if test="${KualiForm.document.rawCashDrawerStatus == KFSConstants.CashDrawerConstants.STATUS_OPEN}">
                        				(opened at ${KualiForm.cashDrawerSummary.timeOpened})
                    				</c:if>
                				</td>
            			</tr>

            			<c:if test="${KualiForm.document.rawCashDrawerStatus != KFSConstants.CashDrawerConstants.STATUS_CLOSED && KualiForm.cashDrawerSummary.remainingSumTotal != null}">
                				<tr>
                    				<td colspan=5 class="tab-subhead">Cash Drawer Activity: <bean:write name="KualiForm" property="cashDrawerSummary.overallReceiptStats.receiptCount"/> available Cash Receipts</td>
                				</tr>
                
                				<tr>
                    				<td rowspan=4 >&nbsp;&nbsp;</td>
                    				<td colspan=2 class="infoline">Checks</td>
                    				<td style="text-align: right">$<bean:write name="KualiForm" property="cashDrawerSummary.overallReceiptStats.checkTotal"/></td>
                    				<td rowspan=4 width=100%>&nbsp;</td>
                				</tr>
                				<tr>
                    				<td colspan="2" class="infoline">Currency</td>
                    				<td style="text-align: right">$<bean:write name="KualiForm" property="cashDrawerSummary.overallReceiptStats.currencyTotal"/></td>
                				</tr>
                				<tr>
                    				<td colspan="2" class="infoline">Coin</td>
                    					<td style="text-align: right">$
                    					<bean:write name="KualiForm" property="cashDrawerSummary.overallReceiptStats.coinTotal"/></td>
                				</tr>
                				<tr>
                    				<th colspan=2 style="text-align: left; padding-left: 0px">TOTAL</th>
                    				<td style="text-align: right">$
                    				<bean:write name="KualiForm" property="cashDrawerSummary.overallReceiptStats.sumTotal"/></td>
                				</tr>
                				<tr>
                    				<td colspan=5 class="tab-subhead">Deposit Activity: <bean:write name="KualiForm" property="cashDrawerSummary.depositedReceiptCount"/>  Deposited Cash Receipts</td>
                				</tr>
                				<tr>
                    				<td rowspan="8">&nbsp;&nbsp;</td>
                    				<td colspan=2 class="infoline">Operating</td>
                    				<td style="text-align: right">$
                    				<bean:write name="KualiForm" property="cashDrawerSummary.overallReceiptSumTotal"/></td>
                    				<td rowspan="8" width=100%>&nbsp;</td>
                				</tr>
                				<tr>
                    				<td colspan=2 class="infoline">- Interim</td>
                    				<td style="text-align: right">$
                    				<bean:write name="KualiForm" property="cashDrawerSummary.interimReceiptSumTotal"/></td>
                				</tr>
                				<tr>
                    				<td colspan=2 class="infoline">- Final</td>
                    				<td style="text-align: right">$
                    				<bean:write name="KualiForm" property="cashDrawerSummary.finalReceiptSumTotal"/></td>
                				</tr>
                				<tr>
                    				<th colspan=2 style="text-align: left; padding-left: 0px">= Remaining</th>
                    				<td style="text-align: right">$
                    				<bean:write name="KualiForm" property="cashDrawerSummary.remainingSumTotal"/></td>
                				</tr>
                				<tr>
                    				<td rowspan="3">&nbsp;&nbsp;</td>
                    				<td class="infoline">Checks</td>
                    				<td style="text-align: right">$
                    				<bean:write name="KualiForm" property="cashDrawerSummary.remainingCheckTotal"/></td>
                				</tr>
                				<tr>
                   	 			<td class="infoline">Currency</td>
                    				<td style="text-align: right">$
                    				<bean:write name="KualiForm" property="cashDrawerSummary.remainingCurrencyTotal"/></td>
                				</tr>
                				<tr>
                    				<td class="infoline">Coin</td>
                    				<td style="text-align: right">$
                    				<bean:write name="KualiForm" property="cashDrawerSummary.remainingCoinTotal"/></td>
                				</tr>
		    
            			</c:if>
        			</table>
			</td>
			<td width="50%" valign="top">
				<fp:cashDrawerCurrencyCoin cashDrawerProperty="document.cashDrawer" readOnly="true" showCashDrawerSummary="${KualiForm.document.rawCashDrawerStatus != KFSConstants.CashDrawerConstants.STATUS_CLOSED && KualiForm.cashDrawerSummary.cashDrawerTotal != null}" />
			</td>
		</tr>
		<tr>
			<td colspan="2">
       	 		<div style="padding: 10px; text-align: center;">
            			<c:if test="${allowOpen}">
                				<html:image src="${ConfigProperties.externalizable.images.url}buttonsmall_openCashDrawer.gif" style="border: none" property="methodToCall.openCashDrawer" title="Open Cash Drawer" alt="Open Cash Drawer" />
            			</c:if>
            
            			<c:if test="${allowRefresh}">
                				<html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_refresh.gif" style="border: none" property="methodToCall.refreshSummary" title="Refresh Cash Drawer Summary" alt="Refresh Cash Drawer Summary" />
            			</c:if>
        			</div>
			</td>
		</tr>
	</table>
    </div>
</kul:tab>
