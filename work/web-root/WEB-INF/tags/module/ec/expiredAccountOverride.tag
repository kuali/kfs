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

<%@ tag description="render the override widget for the expired account" %>

<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<%@ attribute name="detailLineFormName" required="true"
	description="The name  of the detail line"%>              
<%@ attribute name="attributes" required="true" type="java.util.Map"
	description="The DataDictionary entry containing attributes for all detail line fields."%>
<%@ attribute name="readOnly" required="false"
	description="determine whether the expired account override is read-only or not"%>		

<c:set var="accountExpiredOverride" value="accountExpiredOverride" />
<c:set var="accountExpiredOverrideNeeded" value="accountExpiredOverrideNeeded" />
			  
<span class="nowrap" style="font-weight: normal">
	<c:choose>
		<c:when test="${readOnly}">
			<html:hidden property="${detailLineFormName}.${accountExpiredOverride}" />
		</c:when>
		<c:otherwise>
			<kul:htmlAttributeLabel 
				attributeEntry="${attributes[accountExpiredOverride]}"
				useShortLabel="true" 
				labelFor="${detailLineFormName}.${accountExpiredOverride}" forceRequired="true" />
		            
		    <kul:htmlControlAttribute
				property="${detailLineFormName}.${accountExpiredOverride}"
				attributeEntry="${attributes[accountExpiredOverride]}"
				readOnly="false" forceRequired="true"
				readOnlyBody="false"/>
		</c:otherwise>
	</c:choose>
	
</span>			
