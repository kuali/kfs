<%--
 Copyright 2006-2007 The Kuali Foundation.
 
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
<%@ attribute name="disbursementNumberRangeAttributes" required="true"
	type="java.util.Map"
	description="The DataDictionary entry containing attributes for disbursement number range."%>
	
<%@ attribute name="customerProfileAttributes" required="true"
	type="java.util.Map"
	description="The DataDictionary entry containing attributes for customer profile."%>

<%@ attribute name="formatResultAttributes" required="true"
	type="java.util.Map"
	description="The DataDictionary entry containing attributes for format result."%>
<kul:tabTop tabTitle="Payments selected for format process" defaultOpen="true" tabErrorKey="ranges*">
	<div id="disbursementRanges" class="tab-container" align=center>
		<table cellpadding="0" cellspacing="0" class="datatable"
			summary="Payments selected for format process">
			<tr>
				<td colspan="3" class="subhead">
					Your Default Campus Code is <kul:htmlControlAttribute attributeEntry="${disbursementNumberRangeAttributes.physCampusProcCode}" property="campus" readOnly="true" />
				</td>
			</tr>
			<tr>
				
				<kul:htmlAttributeHeaderCell
					attributeEntry="${customerProfileAttributes.customerShortName}" />
				<kul:htmlAttributeHeaderCell
					attributeEntry="${formatResultAttributes.payments}" />
				<kul:htmlAttributeHeaderCell
					attributeEntry="${formatResultAttributes.amount}" />
				
			</tr>
			
			<logic:iterate id="result" name="KualiForm" property="results" indexId="ctr">
            <tr>
               <td class="${dataCell}"><kul:htmlControlAttribute attributeEntry="${customerProfileAttributes.customerShortName}" property="result[${ctr}].cust.customerShortName" readOnly="true" /></td>
               <td class="${dataCell}"><kul:htmlControlAttribute attributeEntry="${formatResultAttributes.payments}" property="result[${ctr}].payments" readOnly="true" /></td>
               <td class="${dataCell}"><kul:htmlControlAttribute attributeEntry="${formatResultAttributes.amount}" property="result[${ctr}].amount" readOnly="true" /></td>
            </tr>
            </logic:iterate>
            
             <tr>
            <td class="total-line">Total</td>
            <td class="total-line">${KualiForm.totalPaymentCount}</td>
            <td class="total-line"><b>${KualiForm.currencyFormattedTotalAmount}</b></td>
            <html:hidden property="totalAmount" />
         </tr>

		</table>
	</div>
</kul:tabTop>
