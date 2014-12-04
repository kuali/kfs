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
