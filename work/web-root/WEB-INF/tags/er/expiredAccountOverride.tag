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

<%@ tag description="render the override widget for the expired account" %>

<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>

<%@ attribute name="detailLineFormName" required="true"
	description="The name  of the detail line"%>              
<%@ attribute name="attributes" required="true" type="java.util.Map"
	description="The DataDictionary entry containing attributes for all detail line fields."%>

<c:set var="accountExpiredOverride" value="accountExpiredOverride" />
<c:set var="accountExpiredOverrideNeeded" value="accountExpiredOverrideNeeded" />
			  
<span class="nowrap" style="font-weight: normal">
	<kul:htmlAttributeLabel 
		attributeEntry="${attributes[accountExpiredOverride]}"
		useShortLabel="true" 
		forceRequired="true" />
            
    <kul:htmlControlAttribute
		property="${detailLineFormName}.${accountExpiredOverride}"
		attributeEntry="${attributes[accountExpiredOverride]}"
		readOnly="false"
		readOnlyBody="false"/>
	
	<html:hidden property="${detailLineFormName}.${accountExpiredOverrideNeeded}" />					
</span>			