<%--
 Copyright 2006 The Kuali Foundation.
 
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

<%@ attribute name="readOnly" required="true" description="determines whether the checkLineGroup will be displayed readonly" %>
<%@ attribute name="rowHeading" required="true" %>
<%@ attribute name="propertyName" required="true" description="name of form property containing the Check" %>
<%@ attribute name="baselinePropertyName" description="name of form property containing the baselineCheck" %>
<%@ attribute name="actionMethod" required="true" description="methodToCall value for actionImage" %>
<%@ attribute name="actionImage" required="true" description="path to image to be displayed in Action column" %>
<%@ attribute name="actionAlt" required="true" description="alt value for actionImage" %>
<%@ attribute name="cssClass" required="true" %>
<%@ attribute name="displayHidden" required="true" %>

<c:set var="checkBaseAttributes" value="${DataDictionary.CheckBase.attributes}" />

<tr>
    <kul:htmlAttributeHeaderCell literalLabel="${rowHeading}:" scope="row">
        <%-- hidden fields inside th to generate valid HTML --%>
        <html:hidden property="${propertyName}.documentNumber" write="${displayHidden}" />
        <html:hidden property="${propertyName}.sequenceId" write="${displayHidden}" />
        <html:hidden property="${propertyName}.interimDepositAmount" write="${displayHidden}" />
        <html:hidden property="${propertyName}.versionNumber" write="${displayHidden}" />

        <c:if test="${!empty baselinePropertyName}">
            <html:hidden property="${baselinePropertyName}.documentNumber" write="${displayHidden}" />
            <html:hidden property="${baselinePropertyName}.sequenceId" write="${displayHidden}" />
            <html:hidden property="${baselinePropertyName}.interimDepositAmount" write="${displayHidden}" />
            <html:hidden property="${baselinePropertyName}.versionNumber" write="${displayHidden}" />
        </c:if>
    </kul:htmlAttributeHeaderCell>
    <td class="${cssClass}" nowrap>
    	<c:if test="${!readOnly}" >
        	<kul:htmlControlAttribute property="${propertyName}.checkNumber" attributeEntry="${checkBaseAttributes.checkNumber}" />
        </c:if>
        <c:if test="${readOnly}">
        	<html:hidden property="${propertyName}.checkNumber" write="true" />
        </c:if>
        <c:if test="${!empty baselinePropertyName}">
            <html:hidden property="${baselinePropertyName}.checkNumber" write="${displayHidden}" />
        </c:if>
    </td>
    <td class="${cssClass}" nowrap>
    	<c:if test="${!readOnly}" >
	    	<kul:dateInput property="${propertyName}.checkDate" attributeEntry="${checkBaseAttributes.checkDate}" />
        </c:if>
        <c:if test="${readOnly}">
        	<html:hidden property="${propertyName}.checkDate" write="true" />
        </c:if>
        <c:if test="${!empty baselinePropertyName}">
            <html:hidden property="${baselinePropertyName}.checkDate" write="${displayHidden}" />
        </c:if>
    </td>
    <td class="${cssClass}" nowrap>
    	<c:if test="${!readOnly}" >
        	<kul:htmlControlAttribute property="${propertyName}.description" attributeEntry="${checkBaseAttributes.description}" />
        </c:if>
        <c:if test="${readOnly}">
        	<html:hidden property="${propertyName}.description" write="true" />
        </c:if>
        <c:if test="${!empty baselinePropertyName}">
            <html:hidden property="${baselinePropertyName}.description" write="${displayHidden}" />
        </c:if>
    </td>
    <td class="${cssClass}" nowrap>
    	<c:if test="${!readOnly}" >
	    	<kul:htmlControlAttribute property="${propertyName}.amount" attributeEntry="${checkBaseAttributes.amount}" styleClass="amount" />
        </c:if>
        <c:if test="${readOnly}">
        	<html:hidden property="${propertyName}.amount" write="true" />
        </c:if>
        <c:if test="${!empty baselinePropertyName}">
            <html:hidden property="${baselinePropertyName}.amount" write="${displayHidden}" />
        </c:if>
    </td>
    
    <c:if test="${!readOnly}">
        <td class="${cssClass}" nowrap>
            <div align="center">
                <html:image property="methodToCall.${actionMethod}" styleClass="tinybutton" src="${actionImage}" title="${actionAlt}" alt="${actionAlt}"/>
            </div>
        </td>
    </c:if>
</tr>