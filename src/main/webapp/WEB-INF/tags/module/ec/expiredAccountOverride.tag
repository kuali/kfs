<%--
   - The Kuali Financial System, a comprehensive financial management system for higher education.
   - 
   - Copyright 2005-2014 The Kuali Foundation
   - 
   - This program is free software: you can redistribute it and/or modify
   - it under the terms of the GNU Affero General Public License as
   - published by the Free Software Foundation, either version 3 of the
   - License, or (at your option) any later version.
   - 
   - This program is distributed in the hope that it will be useful,
   - but WITHOUT ANY WARRANTY; without even the implied warranty of
   - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   - GNU Affero General Public License for more details.
   - 
   - You should have received a copy of the GNU Affero General Public License
   - along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
