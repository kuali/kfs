<%--
 Copyright 2005-2007 The Kuali Foundation.
 
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

<%@ tag description="render the given field in the given detail line" %>

<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>

<%@ attribute name="index" required="true"
              description="the order of the detail line that contains the field being rendered" %>
<%@ attribute name="fieldFormName" required="true"
              description="the form name of the field" %>
<%@ attribute name="attributeEntry" required="true" type="java.util.Map"
			  description="The DataDictionary entry containing attribute for the field"%>              
<%@ attribute name="fieldValue" required="true"
              description="the value of the field that will be displayed" %>
              
<%@ attribute name="fieldInfo" required="false"
              description="the descriptive information of the field that will be displayed" %>                         
<%@ attribute name="inquirableUrl" required="false"
              description="determine if the given field is inquirable" %>
<%@ attribute name="readOnly" required="false"
              description="determine if the field woulb be rendered as read-only or not" %>   
              
<c:set var="inquirable" value="${not empty inquirableUrl}" />  
<c:set var="numericFormatter" value="org.kuali.core.web.format.CurrencyFormatter,org.kuali.core.web.format.IntegerFormatter"/> 
<c:set var="entryFormatter" value="${attributeEntry.formatterClass}" /> 
<c:set var="styleClass" value="${empty entryFormatter || !fn:contains(numericFormatter, entryFormatter) ? 'left' : 'right' }" />   
                   
<kul:htmlControlAttribute
		property="${fieldFormName}"
		attributeEntry="${attributeEntry}"
		readOnly="${readOnly}"
		readOnlyBody="${readOnly}">
      	
      	<span class="${styleClass}">
			<c:if test="${inquirable}">
	    		<a href="${ConfigProperties.kr.url}/${inquirableUrl}" target="_blank">${fieldValue}</a>
			</c:if>
			
			<c:if test="${!inquirable}">
	    		<c:out value="${fieldValue}"/>
			</c:if>
		</span><br/>
    	<div id="${fieldFormName}${index}" class="fineprint">${fieldInfo}</div>
    	
    	<html:hidden write="false" property="${fieldFormName}" style="${textStyle}" />
		
</kul:htmlControlAttribute>          