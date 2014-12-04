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
<%@ attribute name="disbursementNumberRangeAttributes" required="true"
	type="java.util.Map"
	description="The DataDictionary entry containing attributes for disbursement number range."%>
	
<%@ attribute name="customerProfileAttributes" required="true"
	type="java.util.Map"
	description="The DataDictionary entry containing attributes for customer profile."%>

<%@ attribute name="formatResultAttributes" required="true"
	type="java.util.Map"
	description="The DataDictionary entry containing attributes for format result."%>
<kul:tabTop tabTitle="Payments Selected for Format Process" defaultOpen="true" tabErrorKey="ranges*">
	<div id="disbursementRanges" class="tab-container" align=center>
		<table cellpadding="0" cellspacing="0" class="datatable"
			summary="Payments Selected for Format Process">
			<tr>
				<td colspan="4" class="subhead">
					Your Default Campus Code is <kul:htmlControlAttribute attributeEntry="${disbursementNumberRangeAttributes.physCampusProcCode}" property="campus" readOnly="true" />
				</td>
			</tr>
			<tr>
				<kul:htmlAttributeHeaderCell
					attributeEntry="${formatResultAttributes.sortGroupName}" />
				<kul:htmlAttributeHeaderCell
					attributeEntry="${customerProfileAttributes.customerShortName}" />
				<kul:htmlAttributeHeaderCell
					attributeEntry="${formatResultAttributes.payments}" />
				<kul:htmlAttributeHeaderCell
					attributeEntry="${formatResultAttributes.amount}" />
				
			</tr>
			
			<logic:iterate id="result" name="KualiForm" property="formatProcessSummary.processSummaryList" indexId="ctr">
            <tr>
               <td class="${dataCell}"><kul:htmlControlAttribute attributeEntry="${formatResultAttributes.sortGroupName}" property="formatProcessSummary.processSummary[${ctr}].sortGroupName" readOnly="true" /></td>
               <td class="${dataCell}"><kul:htmlControlAttribute attributeEntry="${customerProfileAttributes.customerShortName}" property="formatProcessSummary.processSummary[${ctr}].customer.customerShortName" readOnly="true" /></td>
               <td class="${dataCell}"><kul:htmlControlAttribute attributeEntry="${formatResultAttributes.payments}" property="formatProcessSummary.processSummary[${ctr}].processTotalCount" readOnly="true" /></td>
               <td class="${dataCell}"><kul:htmlControlAttribute attributeEntry="${formatResultAttributes.amount}" property="formatProcessSummary.processSummary[${ctr}].processTotalAmount" readOnly="true" /></td>
            </tr>
            </logic:iterate>
            
         <tr>
            <td class="total-line">&nbsp;</td>
            <td class="total-line">Total</td>
            <td class="total-line">${KualiForm.formatProcessSummary.totalCount}</td>
            <td class="total-line"><b>${KualiForm.currencyFormattedTotalAmount}</b></td>
         </tr>

		</table>
	</div>
</kul:tabTop>
