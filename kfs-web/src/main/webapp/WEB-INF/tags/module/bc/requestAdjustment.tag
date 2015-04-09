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

<%@ attribute name="attributes" required="true" type="java.util.Map"
	description="The DataDictionary entry containing attributes for all detail line fields."%>
<%@ attribute name="adjustmentAmountFieldName" required="true"
	description="The name of the adjustment amount field"%>
<%@ attribute name="methodToCall" required="true"
	description="The name of the action method that adjusts by amount/percent"%>
<%@ attribute name="lineIndex" required="false"
	description="the index of the line to be adjusted"%>
<%@ attribute name="anchor" required="false"
    description="The anchor to go to after refresh" %>

<div style="nowrap">
	<kul:htmlAttributeLabel attributeEntry="${attributes.adjustmentAmount}" forceRequired="false" useShortLabel="true" />
	
	<kul:htmlControlAttribute styleClass="amount" attributeEntry="${attributes.adjustmentAmount}" property="${adjustmentAmountFieldName}"/>			
	
    <c:set var="anchorString" value=""/>
	<c:if test="${!empty anchor}">
	    <c:set var="anchorString" value=".${anchor}"/>
	</c:if>
	<html:image property="methodToCall.${methodToCall}.line${lineIndex}${anchorString}" 
		src="${ConfigProperties.externalizable.images.url}tinybutton-apply.gif" 
		title="Percent Adjustment For Line ${lineIndex}" alt="Percent Adjustment For Line ${lineIndex}" styleClass="tinybutton" />
</div>	
