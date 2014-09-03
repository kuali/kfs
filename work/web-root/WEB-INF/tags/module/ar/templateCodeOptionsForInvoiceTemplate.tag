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

<c:set var="InvoiceTemplateAttributes"
	value="${DataDictionary.InvoiceTemplate.attributes}" />

<td class="infoline"><kul:htmlAttributeLabel
		attributeEntry="${InvoiceTemplateAttributes.invoiceTemplateCode}"
		useShortLabel="false" /> 
	<html:select property="invoiceTemplateCode">
    	<html:optionsCollection property="actionFormUtilMap.getOptionsMap~org|kuali|kfs|module|ar|businessobject|options|InvoiceTemplateValuesFinder" label="value" value="key"/>
    </html:select>
</td>
