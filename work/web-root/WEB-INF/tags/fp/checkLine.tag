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
<c:set var="tabindexOverrideBase" value="200" />

<tr>
    <kul:htmlAttributeHeaderCell literalLabel="${rowHeading}:" scope="row">
        <c:if test="${displayHidden}" >
			 <bean:write  name="KualiForm" property="${propertyName}.documentNumber" />
        	 <bean:write  name="KualiForm" property="${propertyName}.sequenceId" />
        	 <bean:write  name="KualiForm" property="${propertyName}.financialDocumentDepositLineNumber" />
             <bean:write  name="KualiForm" property="${propertyName}.versionNumber" />
             <bean:write  name="KualiForm" property="${propertyName}.financialDocumentTypeCode" />
             <bean:write  name="KualiForm" property="${propertyName}.cashieringRecordSource" />

        	<c:if test="${!empty baselinePropertyName}">
            	<bean:write  name="KualiForm" property="${baselinePropertyName}.documentNumber" />
            	<bean:write  name="KualiForm" property="${baselinePropertyName}.sequenceId" />
            	<bean:write  name="KualiForm" property="${baselinePropertyName}.financialDocumentDepositLineNumber" />
            	<bean:write  name="KualiForm" property="${baselinePropertyName}.versionNumber" />
        	</c:if>
		</c:if>
       
    </kul:htmlAttributeHeaderCell>
    <td class="${cssClass}" nowrap>
    	<c:if test="${!readOnly}" >
        	<kul:htmlControlAttribute property="${propertyName}.checkNumber"
        	tabindexOverride="${tabindexOverrideBase}"
        	 attributeEntry="${checkBaseAttributes.checkNumber}" />
        </c:if>
        <c:if test="${readOnly}">
			<bean:write  name="KualiForm" property="${propertyName}.checkNumber" />
        </c:if>
        <c:if test="${!empty baselinePropertyName}">
			 <c:if test="${displayHidden}" >
				<bean:write  name="KualiForm" property="${baselinePropertyName}.checkNumber" />
			 </c:if>
        </c:if>
    </td>
    <td class="${cssClass}" nowrap>
    	<c:if test="${!readOnly}" >
	    	<kul:htmlControlAttribute property="${propertyName}.checkDate" 
	    	attributeEntry="${checkBaseAttributes.checkDate}"  
	    	tabindexOverride="${tabindexOverrideBase} + 5"
	    	datePicker="true"/>
        </c:if>
        <c:if test="${readOnly}">
			<bean:write  name="KualiForm" property="${propertyName}.checkDate" />
        </c:if>
        <c:if test="${!empty baselinePropertyName}">
			<c:if test="${displayHidden}" >
				<bean:write  name="KualiForm" property="${baselinePropertyName}.checkDate" />
			 </c:if>
        </c:if>
    </td>
    <td class="${cssClass}" nowrap>
    	<c:if test="${!readOnly}" >
        	<kul:htmlControlAttribute property="${propertyName}.description" 
        	tabindexOverride="${tabindexOverrideBase} + 10"
        	attributeEntry="${checkBaseAttributes.description}" />
        </c:if>
        <c:if test="${readOnly}">
			<bean:write  name="KualiForm" property="${propertyName}.description" />
        </c:if>
        <c:if test="${!empty baselinePropertyName}">
			<c:if test="${displayHidden}" >
				<bean:write  name="KualiForm" property="${baselinePropertyName}.description" />
			 </c:if>

        </c:if>
    </td>
    <td class="${cssClass}" nowrap>
    	<c:if test="${!readOnly}" >
	    	<kul:htmlControlAttribute property="${propertyName}.amount" 
	    	tabindexOverride="${tabindexOverrideBase} + 15"
	    	attributeEntry="${checkBaseAttributes.amount}" styleClass="amount" />
        </c:if>
        <c:if test="${readOnly}">
			<bean:write  name="KualiForm" property="${propertyName}.amount" />
        </c:if>
        <c:if test="${!empty baselinePropertyName}">
			<c:if test="${displayHidden}" >
				<bean:write  name="KualiForm" property="${baselinePropertyName}.amount" />
			 </c:if>
        </c:if>
    </td>
    
    <c:if test="${!readOnly}">
        <td class="${cssClass}" nowrap>
            <div align="center">
                <html:image property="methodToCall.${actionMethod}" tabindex="${tabindexOverrideBase} + 20" styleClass="tinybutton" src="${actionImage}" title="${actionAlt}" alt="${actionAlt}"/>
            </div>
        </td>
    </c:if>
</tr>
