<%--
 Copyright 2007-2009 The Kuali Foundation
 
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
