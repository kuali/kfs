<%--
 Copyright 2006-2009 The Kuali Foundation
 
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

<%@ attribute name="documentAttributes" required="true"
	type="java.util.Map"
	description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="readOnly" required="true"
	description="If document is in read only mode"%>	

<kul:tab tabTitle="Asset Increase Details" defaultOpen="true"
	tabErrorKey="${KFSConstants.CASH_CONTROL_DOCUMENT_ERRORS}">
	<div class="tab-container" align=center>
			<h3>Asset Increase Details</h3>
		<table cellpadding="0" cellspacing="0" summary="Asset Increase Details">

			<tr>
				<kul:htmlAttributeHeaderCell
					attributeEntry="${documentAttributes.transactionSubTypeCode}"
					horizontal="true" width="50%" />

				<td class="datacell-nowrap">
					<kul:htmlControlAttribute
						attributeEntry="${documentAttributes.transactionSubTypeCode}"
						property="document.transactionSubTypeCode"/>
				</td>
			</tr>
			
			<tr>
				<kul:htmlAttributeHeaderCell
					attributeEntry="${documentAttributes.transactionSourceTypeCode}"
					horizontal="true" width="50%" />

				<td class="datacell-nowrap">
					<kul:htmlControlAttribute
						attributeEntry="${documentAttributes.transactionSourceTypeCode}"
						property="document.transactionSourceTypeCode"
						readOnly="true"/>
				</td>
			</tr>
			
		</table>
	</div>
</kul:tab>
