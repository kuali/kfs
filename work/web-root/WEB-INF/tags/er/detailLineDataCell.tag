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

<%@ attribute name="index" required="false"
              description="the order of the detail line that contains the field being rendered" %>
<%@ attribute name="fieldFormName" required="true"
              description="the form name of the field" %> 
<%@ attribute name="infoFieldFormName" required="false"
              description="the form name of the field that holds the descriptive information" %>                     
<%@ attribute name="fieldValue" required="true"
              description="the value of the field that will be displayed" %>
<%@ attribute name="attributeEntry" required="true" type="java.util.Map"
			  description="The DataDictionary entry containing attribute for the field"%>
			  
<%@ attribute name="onblur" required="false"
			  description="The DataDictionary entry containing attribute for the field"%> 
<%@ attribute name="onchange" required="false"
			  description="The DataDictionary entry containing attribute for the field"%> 
              
<%@ attribute name="fieldInfo" required="false"
              description="the descriptive information of the field that will be displayed" %>                         
<%@ attribute name="inquirableUrl" required="false"
              description="determine if the given field is inquirable" %>

<%@ attribute name="fieldFormNamePrefix" required="true"
              description="the form name of the field" %>              
<%@ attribute name="relationshipMetadata" required="false" type="java.lang.Object"
			  description="The DataDictionary entry containing attributes for the line fields."%>			  
			                
<%@ attribute name="readOnly" required="false"
              description="determine if the field woulb be rendered as read-only or not" %>   
  
<c:if test="${!scriptsLoaded}">
	<SCRIPT type="text/javascript">
	    var kualiForm = document.forms['KualiForm'];
	    var kualiElements = kualiForm.elements;
	</SCRIPT>
	
	<script type='text/javascript' src="dwr/interface/ChartService.js"></script>
	<script type='text/javascript' src="dwr/interface/AccountService.js"></script>
	<script type='text/javascript' src="dwr/interface/SubAccountService.js"></script>
	<script type='text/javascript' src="dwr/interface/ObjectCodeService.js"></script>
	<script type='text/javascript' src="dwr/interface/ObjectTypeService.js"></script>
	<script type='text/javascript' src="dwr/interface/SubObjectCodeService.js"></script>
	<script type='text/javascript' src="dwr/interface/ProjectCodeService.js"></script>
	<script type='text/javascript' src="dwr/interface/OriginationCodeService.js"></script>
	<script type='text/javascript' src="dwr/interface/DocumentTypeService.js"></script>
	<script type='text/javascript' src="dwr/interface/PayrollAmountUtil.js"></script>
	<script language="JavaScript" type="text/javascript" src="scripts/kfs/objectInfo.js"></script>
	<script language="JavaScript" type="text/javascript" src="scripts/effort/objectInfo.js"></script>
	<c:set var="scriptsLoaded" value="true" scope="request" />
</c:if>              
              
<c:set var="inquirable" value="${not empty inquirableUrl}" />  
<c:set var="numericFormatter" value="org.kuali.core.web.format.CurrencyFormatter,org.kuali.core.web.format.IntegerFormatter"/> 
<c:set var="entryFormatter" value="${attributeEntry.formatterClass}" /> 
<c:set var="styleClass" value="${empty entryFormatter || !fn:contains(numericFormatter, entryFormatter) ? 'left' : 'right' }" />   
                   
<kul:htmlControlAttribute
		property="${fieldFormName}"
		attributeEntry="${attributeEntry}"
		onblur="${onblur}"
		onchange="${onchange}"
		readOnly="${readOnly}"
		readOnlyBody="${readOnly}">
      	
      	<span class="${styleClass}">
			<c:if test="${inquirable}">
	    		<a href="${inquirableUrl}" target="_blank">${fieldValue}</a>
			</c:if>
			
			<c:if test="${!inquirable}">
	    		<c:out value="${fieldValue}"/>
			</c:if>
		</span><br/>
    	<span class="${styleClass}"><div id="${fieldFormName}${index}" class="fineprint">${fieldInfo}</div></span>
    	
    	<html:hidden write="false" property="${fieldFormName}" style="${textStyle}" />
		
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
	<html:hidden write="false" property="${infoFieldFormName}"/>
</c:if>
       