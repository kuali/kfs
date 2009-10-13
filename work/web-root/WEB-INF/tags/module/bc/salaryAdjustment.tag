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
<%@ attribute name="adjustmentMeasurementFieldName" required="true"
	description="The name of the adjustment measurement field"%>
<%@ attribute name="adjustmentAmountFieldName" required="true"
	description="The name of the adjustment amount field"%>
<%@ attribute name="methodToCall" required="true"
	description="The name of the action method that adjusts the amount/percent"%>
<%@ attribute name="lineIndex" required="false"
	description="the index of the line to be adjuested"%>

<div style="nowrap">
	<kul:htmlAttributeLabel attributeEntry="${attributes.adjustmentMeasurement}" forceRequired="false" useShortLabel="true" />
	
	<kul:htmlControlAttribute attributeEntry="${attributes.adjustmentMeasurement}" property="${adjustmentMeasurementFieldName}"/>

	<kul:htmlControlAttribute attributeEntry="${attributes.adjustmentAmount}" property="${adjustmentAmountFieldName}"/>			
	
	<html:image property="methodToCall.${methodToCall}.line${lineIndex}.anchorsalaryexistingLineLineAnchor${lineIndex}" 
		src="${ConfigProperties.externalizable.images.url}tinybutton-apply.gif" 
		title="Percent Adjustment For Line ${lineIndex}" alt="Percent Adjustment For Line ${lineIndex}" styleClass="tinybutton" />
</div>	
