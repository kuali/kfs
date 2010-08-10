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

<script type='text/javascript' src="dwr/interface/RegistrationCodeService.js"></script>
<script type='text/javascript' src="dwr/interface/EndowmentTransactionDocumentService.js"></script>
<script language="JavaScript" type="text/javascript" src="scripts/module/endow/securityObjectInfo.js"></script>
<script language="JavaScript" type="text/javascript" src="scripts/module/endow/registrationObjectInfo.js"></script>

<%@ attribute name="editingMode" required="false" description="used to decide if items may be edited" type="java.util.Map"%>
<%@ attribute name="showSource" required="true" %>
<%@ attribute name="showTarget" required="true" %>
<%@ attribute name="showRegistrationCode" required="true" %>
<%@ attribute name="openTabByDefault" required="true" %>

<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />

<kul:tab tabTitle="Security Details" defaultOpen="${openTabByDefault}" tabErrorKey="${EndowConstants.TRANSACTION_SECURITY_TAB_ERRORS}">


 <c:set var="ClassCodeAttributes" value="${DataDictionary.ClassCode.attributes}" />

 <div class="tab-container" align=center>
	<h3>Security Details</h3>
	<table cellpadding="0" cellspacing="0" class="datatable" summary="Security Details2" style='table-layout:fixed'>
	
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
		
		<tr>
         	<kul:htmlAttributeHeaderCell
				attributeEntry="${securityTransactionAttributes.securityID}"
				forceRequired="true"
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
					useShortLabel="false"
				/>            
			</c:if>					

		</tr>
       
        <tr> 
            <td class="infoline">
	            <kul:htmlControlAttribute attributeEntry="${securityTransactionAttributes.securityID}" 
	            	property="document.${securityType}.securityID" 
	            	onblur="loadSecurityInfo(this.name);"
	            	readOnly="${readOnly}"
	            	/>
	            &nbsp;
				<c:if test="${not readOnly}">
					<kul:lookup boClassName="org.kuali.kfs.module.endow.businessobject.Security"
						fieldConversions="id:document.${securityType}.securityID" />
				</c:if>
				<br/>
				<div id="security.description" class="fineprint">
            		 <kul:htmlControlAttribute attributeEntry="${sourceSecurityTransactionAttributes.securityID}" property="document.${securityType}.security.description" readOnly="true" />
            	</div>	
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
            			onblur="loadRegistrationInfo(this.name);"
            			readOnly="${readOnly}" 
            	/>
                &nbsp;
				<c:if test="${not readOnly}">
					<kul:lookup boClassName="org.kuali.kfs.module.endow.businessobject.RegistrationCode"
						fieldConversions="code:document.${securityType}.registrationCode" 
						/>
				</c:if>
				<br/>
				<div id="registration.description" class="fineprint">
            		<kul:htmlControlAttribute attributeEntry="${securityTransactionAttributes.registrationCode}" property="document.${securityType}.registrationCodeObj.name" readOnly="true" />
            	</div>		
            </td> 
            </c:if>
        </tr>
       
    <%--
    <c:if  test="${showTarget}">  
		<tr>
            <kul:htmlAttributeHeaderCell attributeEntry="${securityTransactionAttributes.securityId}"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${securityTransactionAttributes.registrationCode}"/>
		</tr>
      
        <tr>
            <td class="infoline"><kul:htmlControlAttribute attributeEntry="${securityTransactionAttributes.securityId}" property="targetTransactionSecurity.securityId" /></td>
            <td class="infoline"><kul:htmlControlAttribute attributeEntry="${securityTransactionAttributes.registrationCode}" property="targetTransactionSecurity.registrationCode" /></td> 
        </tr>
    </c:if>
	--%>
	</table>
</div>
</kul:tab>