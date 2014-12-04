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

<c:set var="documentAttributes" value="${DataDictionary.ContractsGrantsLetterOfCreditReviewDocument.attributes}" />

<kul:tabTop tabTitle="Contracts Grants LOC Review Initiation" defaultOpen="true" tabErrorKey="*">
	<div class="tab-container" align=center>
		<h3>Contracts Grants LOC Review Initiation</h3>
		<table cellpadding="0" cellspacing="0" class="datatable" summary="Credit Memo Init Section">
			<tr>
				<th align=right valign=middle class="bord-l-b">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${documentAttributes.letterOfCreditFundGroupCode}" forceRequired="true" />
					</div>
				</th>
				<td align=left valign=middle class="datacell" style="width: 50%;"><kul:htmlControlAttribute
						attributeEntry="${documentAttributes.letterOfCreditFundGroupCode}" property="document.letterOfCreditFundGroupCode" readOnly="false"
						forceRequired="true" /></td>
			</tr>
			<tr>
				<th align=right valign=middle class="bord-l-b">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${documentAttributes.letterOfCreditFundCode}" />
					</div>
				</th>
				<td align=left valign=middle class="datacell" style="width: 50%;"><kul:htmlControlAttribute
						attributeEntry="${documentAttributes.letterOfCreditFundCode}" property="document.letterOfCreditFundCode" readOnly="false" /></td>
			</tr>
		</table>
	</div>
</kul:tabTop>
