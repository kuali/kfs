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

<%@ attribute name="awardAttributes" required="true" type="java.util.Map"
	description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="propertyName" required="true" description="The DataDictionary entry containing attributes for this row's fields."%>

<tr>
	<td><kul:htmlControlAttribute attributeEntry="${awardAttributes.proposalNumber}" property="${propertyName}.proposalNumber" readOnly="true" /></td>
	<td><kul:htmlControlAttribute attributeEntry="${awardAttributes.awardBeginningDate}" property="${propertyName}.awardBeginningDate"
			readOnly="true" /></td>
	<td><kul:htmlControlAttribute attributeEntry="${awardAttributes.awardEndingDate}" property="${propertyName}.awardEndingDate" readOnly="true" />
	</td>
	<td><kul:htmlControlAttribute attributeEntry="${awardAttributes.billingFrequencyCode}" property="${propertyName}.billingFrequencyCode"
			readOnly="true" /></td>
	<td><kul:htmlControlAttribute attributeEntry="${awardAttributes.instrumentTypeCode}" property="${propertyName}.instrumentTypeCode" readOnly="true" />
	</td>
	<td><kul:htmlControlAttribute attributeEntry="${awardAttributes.invoicingOptionCode}" property="${propertyName}.invoicingOptionDescription" readOnly="true" />
	</td>
	<td><kul:htmlControlAttribute attributeEntry="${awardAttributes.awardTotalAmount}" property="${propertyName}.awardTotalAmount" readOnly="true" />
	</td>
</tr>
