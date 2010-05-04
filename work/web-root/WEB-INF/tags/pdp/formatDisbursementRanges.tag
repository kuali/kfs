<%--
 Copyright 2006-2008 The Kuali Foundation
 
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
