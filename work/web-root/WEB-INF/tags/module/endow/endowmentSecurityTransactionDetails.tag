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
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<script type='text/javascript' src="dwr/interface/RegistrationCodeService.js"></script>
<script type='text/javascript' src="dwr/interface/EndowmentTransactionDocumentService.js"></script>
<script language="JavaScript" type="text/javascript" src="scripts/module/endow/securityObjectInfo.js"></script>
<script language="JavaScript" type="text/javascript" src="scripts/module/endow/registrationObjectInfo.js"></script>

<%@ attribute name="editingMode" required="false" description="used to decide if items may be edited" type="java.util.Map"%>
<%@ attribute name="showSource" required="true" %>
<%@ attribute name="showTarget" required="true" %>
<%@ attribute name="showRegistrationCode" required="true" %>
<%@ attribute name="showLabels" required="true" %>
<%@ attribute name="showRequired" required="true" %>

<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />

	<table cellpadding="0" cellspacing="0" class="datatable" summary="Security Details2" >
	
	<c:choose>
		<c:when  test="${showSource}">  
			<c:set var="securityTransactionAttributes" value="${DataDictionary.EndowmentSourceTransactionSecurity.attributes}" /> 
			<c:set var="securityType" value="sourceTransactionSecurity" />
		</c:when>
		<c:when  test="${showTarget}">  
			<c:set var="securityTransactionAttributes" value="${DataDictionary.EndowmentTargetTransactionSecurity.attributes}" />
			<c:set var="securityType" value="targetTransactionSecurity" /> 
		</c:when>
	</c:choose>
	<c:set var="ClassCodeAttributes" value="${DataDictionary.ClassCode.attributes}" />
	 
	<c:if test="${showLabels}">
		<tr>
	            <td colspan="1" class="tab-subhead" style="border-right: none;" align="left">
	            <c:if test="${showSource}">From</c:if>
	            <c:if test="${showTarget}">To</c:if>
	            </td>    
	            <td colspan="4" class="tab-subhead" style="border-right: none;border-left: none;" >
	            &nbsp;
	            </td>
	    </tr>
    </c:if>
		
		<tr>
         	<kul:htmlAttributeHeaderCell
				attributeEntry="${securityTransactionAttributes.securityID}"
				forceRequired="${showRequired}"
				useShortLabel="false"
				/>

			<kul:htmlAttributeHeaderCell attributeEntry="${ClassCodeAttributes.code}"
				hideRequiredAsterisk="true"
				useShortLabel="false"/>

			<kul:htmlAttributeHeaderCell attributeEntry="${ClassCodeAttributes.securityEndowmentTransactionCode}"
				hideRequiredAsterisk="true"
				useShortLabel="false"/>
				
			<kul:htmlAttributeHeaderCell attributeEntry="${ClassCodeAttributes.taxLotIndicator}" 
				hideRequiredAsterisk="true"
				useShortLabel="false" />
			
			<c:if test="${showRegistrationCode}">
	          	<kul:htmlAttributeHeaderCell
					attributeEntry="${securityTransactionAttributes.registrationCode}"
                    forceRequired="${showRequired}"
					useShortLabel="false"
				/>            
			</c:if>					

		</tr>
       
        <tr> 
            <td class="infoline">
	            <kul:htmlControlAttribute attributeEntry="${securityTransactionAttributes.securityID}" 
	            	property="document.${securityType}.securityID" 
	            	onblur="loadSecurityInfoFromTo(this.name);"
	            	readOnly="${readOnly}"
	            	/>
	            &nbsp;
				<c:if test="${not readOnly}">
					<kul:lookup boClassName="org.kuali.kfs.module.endow.businessobject.Security"
						fieldConversions="id:document.${securityType}.securityID" />
				</c:if>
				<br/>
				<%-- 
				<div id="${securityType}.security.description" class="fineprint">
            		 <kul:htmlControlAttribute attributeEntry="${sourceSecurityTransactionAttributes.securityID}" property="document.${securityType}.security.description" readOnly="true" />
            	</div>	
            	--%>
            </td>
            <td>
            	<div id="document.${securityType}.security.securityClassCode" >
            		<kul:htmlControlAttribute attributeEntry="${ClassCodeAttributes.code}" property="document.${securityType}.security.securityClassCode" readOnly="true" />
            		-
            		<kul:htmlControlAttribute attributeEntry="${ClassCodeAttributes.code}" property="document.${securityType}.security.classCode.name" readOnly="true" />
            	</div>
            </td>
            <td>
            	<div id="document.${securityType}.security.classCode.securityEndowmentTransactionCode">
            		<kul:htmlControlAttribute attributeEntry="${ClassCodeAttributes.code}" property="document.${securityType}.security.classCode.securityEndowmentTransactionCode" readOnly="true" />
            		-
            		<kul:htmlControlAttribute attributeEntry="${ClassCodeAttributes.code}" property="document.${securityType}.security.classCode.endowmentTransactionCode.name" readOnly="true" />
            	</div>	
            </td>
            <td>
            	<div id="document.${securityType}.security.classCode.taxLotIndicator">
            		<kul:htmlControlAttribute attributeEntry="${ClassCodeAttributes.code}" property="document.${securityType}.security.classCode.taxLotIndicator" readOnly="true" />
            	</div>	
            </td>
            <c:if test="${showRegistrationCode}">
            <td class="infoline">
            	<kul:htmlControlAttribute attributeEntry="${securityTransactionAttributes.registrationCode}" 
            			property="document.${securityType}.registrationCode"
            			onblur="loadRegistrationInfoFromTo(this.name);"
            			readOnly="${readOnly}" 
            	/>
                &nbsp;
				<c:if test="${not readOnly}">
					<kul:lookup boClassName="org.kuali.kfs.module.endow.businessobject.RegistrationCode"
						fieldConversions="code:document.${securityType}.registrationCode" 
						/>
				</c:if>
				<br/>
				<div id="${securityType}.registration.description" class="fineprint">
            		<kul:htmlControlAttribute attributeEntry="${securityTransactionAttributes.registrationCode}" property="document.${securityType}.registrationCodeObj.name" readOnly="true" />
            	</div>		
            </td> 
            </c:if>
        </tr>
      
	</table>
