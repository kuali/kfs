<%--
 Copyright 2005-2009 The Kuali Foundation
 
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

<%@ tag description="render the given field in the given detail line" %>

<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<%@ attribute name="fieldFormName" required="true"
    description="the form name of the field" %> 
<%@ attribute name="infoFieldFormName" required="false"
    description="the form name of the field that holds the descriptive information" %>                     
<%@ attribute name="fieldValue" required="true"
    description="the value of the field that will be displayed" %>
<%@ attribute name="attributeEntry" required="true" type="java.util.Map"
	description="The DataDictionary entry containing attribute for the field"%>
			  
<%@ attribute name="onblur" required="false"
	description="The JavaScript function names associated with the field when onBlur event occurs"%> 
<%@ attribute name="onchange" required="false"
	description="The JavaScript function names associated with the field when onChange event occurs"%> 
              
<%@ attribute name="fieldInfo" required="false"
    description="the descriptive information of the field that will be displayed" %>                         
<%@ attribute name="inquirableUrl" required="false" type="java.lang.Object"
    description="The inquirable url if the given field is inquirable" %>

<%@ attribute name="fieldFormNamePrefix" required="true"
    description="the form name prefix of the field" %>              
<%@ attribute name="relationshipMetadata" required="false" type="java.lang.Object"
	description="The DataDictionary entry containing attributes for the line fields."%>	
			  		  
<%@ attribute name="readOnlySection" required="false"
    description="determine if the container of current detail line is read-only or not" %>		                
<%@ attribute name="readOnly" required="false"
    description="determine if the field woulb be rendered as read-only or not" %>  
<%@ attribute name="withHiddenForm" required="false"
    description="determine if the field woulb be rendered with a hidden form" %>  
              
<%@ attribute name="index" required="false"
    description="the index of the detail line that contains the field being rendered" %>                           
  
<c:if test="${!scriptsLoaded}">
	<SCRIPT type="text/javascript">
	    var kualiForm = document.forms['KualiForm'];
	    var kualiElements = kualiForm.elements;
	</SCRIPT>
		
	<script language="JavaScript" type="text/javascript" src="scripts/sys/objectInfo.js"></script>
	<script language="JavaScript" type="text/javascript" src="scripts/module/ec/objectInfo.js"></script>
	
	<c:set var="scriptsLoaded" value="true" scope="request" />
</c:if>              
              
<c:set var="inquirable" value="${not empty inquirableUrl}" />  
<c:set var="currencyFormatter" value="org.kuali.rice.core.web.format.CurrencyFormatter"/>
<c:set var="integerFormatter" value="org.kuali.rice.core.web.format.IntegerFormatter"/> 

<c:set var="entryFormatter" value="${attributeEntry.formatterClass}" /> 
<c:set var="isCurrency" value="${not empty entryFormatter && fn:contains(currencyFormatter, entryFormatter)}" />
<c:set var="isInteger" value="${not empty entryFormatter && fn:contains(integerFormatter, entryFormatter)}" />

<c:set var="styleClass" value="${(isCurrency || isInteger) ? 'right' : '' }" /> 
<c:set var="readonlySuffix" value="${readOnlySection ? '.readonly' : ''}" /> 
<c:set var="percent" value="${fn:contains(fieldFormName, 'Percent') ? '%' : '' }" />
                   
<kul:htmlControlAttribute
	property="${fieldFormName}"
	attributeEntry="${attributeEntry}"
	onblur="${onblur}"
	onchange="${onchange}"
	readOnly="${readOnly}"
	readOnlyBody="${readOnly}"
	styleClass="${styleClass}">
	
	<c:set var="spanName" value="${fieldFormName}.span${readonlySuffix}" />
	<c:set var="divName" value="${fieldFormName}.div${readonlySuffix}" />
	<c:set var="formatNumber" value="${fieldValue}" />
	
	<c:choose>
		<c:when test="${isCurrency}">
		    <fmt:formatNumber var="formatNumber" value="${fieldValue}" currencySymbol="" type="currency"/>
		</c:when>
		<c:when test="${isInteger}">
		    <fmt:formatNumber var="formatNumber" value="${fieldValue}" type="number"/>
		</c:when>
	</c:choose>

    <c:choose>
      	<c:when test="${inquirable}">
      		<a href="${inquirableUrl.href}" target="_blank">
	    		<span class="${styleClass}" style="text-decoration: underline;" id="${spanName}" name="${spanName}">
	    			${formatNumber}${percent}
	    		</span>
    		</a>
		</c:when>
		<c:otherwise>
    		<span class="${styleClass}" id="${spanName}" name="${spanName}">
    			${formatNumber}${percent}
    		</span>
		</c:otherwise>
	</c:choose>
	
	<br/>
	
	<span class="${styleClass}">
   		<div id="${divName}" name="${divName}" class="fineprint">${fieldInfo}</div>
   	</span>
   	
   	<c:if test="${withHiddenForm}">
   		<html:hidden write="false" property="${fieldFormName}"/>
   	</c:if>		
</kul:htmlControlAttribute>  

<c:if test="${!readOnly && not empty relationshipMetadata}">
	<c:forEach var="field" items="${relationshipMetadata.parentToChildReferences}" varStatus="status">
		<c:choose>
			<c:when test="${status.index == 0}">
				<c:set var="fieldConversions" value="${field.value}:${fieldFormNamePrefix}.${field.key}" />
				<c:set var="lookupParameters" value="${fieldFormNamePrefix}.${field.key}:${field.value}" />
			</c:when>
			<c:otherwise>			
				<c:set var="fieldConversions" value="${field.value}:${fieldFormNamePrefix}.${field.key},${fieldConversions}" />
				<c:set var="lookupParameters" value="${fieldFormNamePrefix}.${field.key}:${field.value},${lookupParameters}" />
			</c:otherwise>
		</c:choose>
	</c:forEach>
	
	<kul:lookup boClassName="${relationshipMetadata.relatedClass.name}"
				fieldConversions="${fieldConversions}"
				lookupParameters="${lookupParameters}"
				fieldLabel="${attributeEntry.label}" /> 
</c:if>
              	
<c:if test="${!readOnly && not empty infoFieldFormName}">
	<div id="${infoFieldFormName}.div" name="${infoFieldFormName}.div" class="fineprint">
		<bean:write name="KualiForm" property="${infoFieldFormName}"/>
	</div>
	
	<c:if test="${not empty relationshipMetadata}">
		<html:hidden write="false" property="${infoFieldFormName}"/>
	</c:if>
	
</c:if>

       
