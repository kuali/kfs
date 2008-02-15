<%--
 Copyright 2006-2007 The Kuali Foundation.
 
 Licensed under the Educational Community License, Version 1.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl1.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>
              
<%@ attribute name="cashControlDetailAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for cash control detail fields." %>              
              
<%@ attribute name="readOnly" required="true" description="determines whether the check  will be displayed readonly" %>
<%@ attribute name="rowHeading" required="true" %>
<%@ attribute name="propertyName" required="true" description="name of form property containing the customer invoice document" %>
<%@ attribute name="actionMethod" required="true" description="methodToCall value for actionImage" %>
<%@ attribute name="actionImage" required="true" description="path to image to be displayed in Action column" %>
<%@ attribute name="actionAlt" required="true" description="alt value for actionImage" %>
<%@ attribute name="cssClass" required="true" %>

<tr>
    <kul:htmlAttributeHeaderCell literalLabel="${rowHeading}:" scope="row">
        <%-- Outside this th, these hidden fields would be invalid HTML. --%>
        <html:hidden property="${propertyName}.documentNumber" />
        <html:hidden property="${propertyName}.versionNumber" />
        <html:hidden property="${propertyName}.objectId" />
    </kul:htmlAttributeHeaderCell>

	<td align=left class="${cssClass}">
		<kul:htmlControlAttribute attributeEntry="${cashControlDetailAttributes.referenceFinancialDocumentNumber }" property="${propertyName}.referenceFinancialDocumentNumber" readOnly="true"/>
	</td>
	<td align=left class="${cssClass}">
		<kul:htmlControlAttribute attributeEntry="${cashControlDetailAttributes.customerNumber }" property="${propertyName}.customerNumber"/>
		&nbsp;
		<kul:lookup boClassName="org.kuali.module.ar.bo.Customer" fieldConversions="customer:${propertyName}.customerNumber" />
	</td>
	<td align=left class="${cssClass}">
    	<kul:htmlControlAttribute attributeEntry="${cashControlDetailAttributes.customerPaymentMediumIdentifier }" property="${propertyName}.customerPaymentMediumIdentifier"/>
	</td> 
	<td align=left class="${cssClass}">
		<kul:dateInput attributeEntry="${cashControlDetailAttributes.customerPaymentDate}" property="${propertyName}.customerPaymentDate"/>
	</td>               
	<td align=left class="${cssClass}">
		<kul:htmlControlAttribute attributeEntry="${cashControlDetailAttributes.financialDocumentLineAmount }" property="${propertyName}.financialDocumentLineAmount"/>
	</td>
	<c:if test="${not readOnly}">
        <td class="${cssClass}">
        	<div align="center">
        		<html:image property="methodToCall.${actionMethod}" src="${actionImage}" alt="${actionAlt}" title="${actionAlt}" styleClass="tinybutton"/>
        	</div>
        </td>
    </c:if>
</tr>