<%--
 Copyright 2009 The Kuali Foundation
 
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

<%@ attribute name="awardAttributes" required="true" type="java.util.Map"
	description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="propertyName" required="true" description="The DataDictionary entry containing attributes for this row's fields."%>

<tr>
	<td><kul:htmlControlAttribute attributeEntry="${awardAttributes.proposalNumber}" property="${propertyName}.proposalNumber" readOnly="true" /></td>
	<td><kul:htmlControlAttribute attributeEntry="${awardAttributes.awardBeginningDate}" property="${propertyName}.awardBeginningDate"
			readOnly="true" /></td>
	<td><kul:htmlControlAttribute attributeEntry="${awardAttributes.awardEndingDate}" property="${propertyName}.awardEndingDate" readOnly="true" />
	</td>
	<td><kul:htmlControlAttribute attributeEntry="${awardAttributes.preferredBillingFrequency}" property="${propertyName}.preferredBillingFrequency"
			readOnly="true" /></td>
	<td><kul:htmlControlAttribute attributeEntry="${awardAttributes.contractGrantType}" property="${propertyName}.contractGrantType" readOnly="true" />
	</td>
	<td><kul:htmlControlAttribute attributeEntry="${awardAttributes.invoicingOptions}" property="${propertyName}.invoicingOptions" readOnly="true" />
	</td>
	<td><kul:htmlControlAttribute attributeEntry="${awardAttributes.awardTotalAmount}" property="${propertyName}.awardTotalAmount" readOnly="true" />
	</td>
</tr>
