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
<%@ attribute name="subTypeReadOnly" required="true"
	description="If sub type code filed is in read only mode"%>	
<%@ attribute name="tabTitle" required="true"
	description="This is displayed as Tab title."%>
<%@ attribute name="summaryTitle" required="true"
	description="This is displayed as summary title."%>
<%@ attribute name="headingTitle" required="true"
	description="This is displayed as heading in H3 title."%>
		
<kul:tab tabTitle="${tabTitle}" defaultOpen="true"
	tabErrorKey="${EndowConstants.TRANSACTION_DETAILS_ERRORS}">
	<div class="tab-container" align=center>
			<h3>${headingTitle}</h3>
		<table cellpadding="0" cellspacing="0" summary="${summaryTitle}">

			<tr>
				<kul:htmlAttributeHeaderCell
					attributeEntry="${documentAttributes.transactionSubTypeCode}"
					useShortLabel="false"
					forceRequired="true"
					horizontal="true" width="50%" />

				<td class="datacell-nowrap">
					<kul:htmlControlAttribute
						attributeEntry="${documentAttributes.transactionSubTypeCode}"
						property="document.transactionSubTypeCode"
						extraReadOnlyProperty="document.transactionSubType.name"
						readOnly="${readOnly or subTypeReadOnly}"
						/>
				</td>
			</tr>
			
			<tr>
				<kul:htmlAttributeHeaderCell
					attributeEntry="${documentAttributes.transactionSourceTypeCode}"
					useShortLabel="false"
					horizontal="true" width="50%" />

				<td class="datacell-nowrap">
					<kul:htmlControlAttribute
						attributeEntry="${documentAttributes.transactionSourceTypeCode}"
						property="document.transactionSourceTypeCode"
						extraReadOnlyProperty="document.transactionSourceType.name"
						readOnly="true"/>
				</td>
			</tr>
			
		</table>
	</div>
</kul:tab>
