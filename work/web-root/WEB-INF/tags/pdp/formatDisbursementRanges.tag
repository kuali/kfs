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

<%@ attribute name="bankAttributes" required="true"
	type="java.util.Map"
	description="The DataDictionary entry containing attributes for bank."%>
<kul:tabTop tabTitle="Disbursement Ranges" defaultOpen="true" tabErrorKey="ranges*">
	<div id="disbursementRanges" class="tab-container" align=center>
		<table cellpadding="0" cellspacing="0" class="datatable"
			summary="Disbursement Ranges">
			<tr>
				<td colspan="4" class="subhead">
					Your Default Campus Code is <kul:htmlControlAttribute attributeEntry="${disbursementNumberRangeAttributes.physCampusProcCode}" property="campus" readOnly="true" />
				</td>
			</tr>
			<tr>
				
				<kul:htmlAttributeHeaderCell
					attributeEntry="${disbursementNumberRangeAttributes.physCampusProcCode}" />
				<kul:htmlAttributeHeaderCell
					attributeEntry="${bankAttributes.bankName}" />
				<kul:htmlAttributeHeaderCell
					attributeEntry="${disbursementNumberRangeAttributes.disbursementTypeCode}" />					
				<kul:htmlAttributeHeaderCell
					attributeEntry="${disbursementNumberRangeAttributes.lastAssignedDisbNbr}" />
				
			</tr>
			
			<logic:iterate id="range" name="KualiForm" property="ranges" indexId="ctr">
              <tr>
                <td class="${dataCell}"><kul:htmlControlAttribute attributeEntry="${disbursementNumberRangeAttributes.physCampusProcCode}" property="range[${ctr}].physCampusProcCode" readOnly="true" /></th>
                <td class="${dataCell}"><kul:htmlControlAttribute attributeEntry="${bankAttributes.bankName}" property="range[${ctr}].bank.bankName" readOnly="true" /></td>
                <td class="${dataCell}"><kul:htmlControlAttribute attributeEntry="${disbursementNumberRangeAttributes.disbursementTypeCode}" property="range[${ctr}].disbursementTypeCode" readOnly="true" /></th>
                <td class="${dataCell}"><kul:htmlControlAttribute attributeEntry="${disbursementNumberRangeAttributes.lastAssignedDisbNbr}" property="range[${ctr}].lastAssignedDisbNbr" readOnly="true" /></td>
              </tr>
            </logic:iterate>

		</table>
	</div>
</kul:tabTop>
