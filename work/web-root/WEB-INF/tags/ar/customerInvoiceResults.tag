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

<c:set var="documentAttributes" value="${DataDictionary.CustomerInvoiceDocument.attributes}" />

<tr>
	<td colspan="5">
		<br />
			<center>
			<table class="datatable-80" cellspacing="0" cellpadding="0" width="100%">
				<thead>
					<tr>
						<th class="sortable">Writeoff?</th>
						<th class="sortable"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.age}" /></th>
						<th class="sortable"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.documentHeader.documentNumber}" /></th>
						<th class="sortable"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.documentFinalDate}" /></th>
						<th class="sortable"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.sourceTotal}" /></th>
						<th class="sortable"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.balance}" /></th>
					</tr>
				</thead>

				<tr class="odd">
					<td class="infocell"><input type="checkbox" /></td>
					<td class="infocell">123</td>
					<td class="infocell">123456</td>
					<td class="infocell">01/01/2008</td>
					<td class="infocell">$100.00</td>
					<td class="infocell">$100.00</td>
				</tr>
			</table>
			</center>
		<br />
	</td>
</tr>
