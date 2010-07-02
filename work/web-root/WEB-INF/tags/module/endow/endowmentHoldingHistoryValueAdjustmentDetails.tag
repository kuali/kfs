<%--
 Copyright 2006-2009 The Kuali Foundation
 
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

<%@ attribute name="documentAttributes" required="true"
	type="java.util.Map"
	description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="readOnly" required="true"
	description="If document is in read only mode"%>	
<%@ attribute name="tabTitle" required="true"
	description="This is displayed as Tab title."%>
<%@ attribute name="summaryTitle" required="true"
	description="This is displayed as summary title."%>
<%@ attribute name="headingTitle" required="true"
	description="This is displayed as heading in H3 title."%>
	
<script type='text/javascript' src="dwr/interface/EndowmentTransactionDocumentService.js"></script>	
<script language="JavaScript" type="text/javascript" src="scripts/module/endow/historyValueAdjustmentSecurityObjectInfo.js"></script>
	
<kul:tab tabTitle="${tabTitle}" defaultOpen="true"
	tabErrorKey="${EndowConstants.HistoryHoldingValueAdjustmentValuationCodes.HISTORY_VALUE_ADJUSTMENT_DETAILS_ERRORS}">
	<c:set var="ClassCodeAttributes" value="${DataDictionary.ClassCode.attributes}" />
	<c:set var="SecurityValuationMethodAttributes" value="${DataDictionary.SecurityValuationMethod.attributes}" />
	
	<div class="tab-container" align=center>
			<h3>${headingTitle}</h3>
		<table cellpadding="0" cellspacing="0" summary="${summaryTitle}">
		<tr>
         	<kul:htmlAttributeHeaderCell
				attributeEntry="${documentAttributes.securityId}"
				forceRequired="true"
				useShortLabel="false"
				/>

			<kul:htmlAttributeHeaderCell attributeEntry="${ClassCodeAttributes.code}"
				hideRequiredAsterisk="true"
				useShortLabel="false"/>

			<kul:htmlAttributeHeaderCell attributeEntry="${SecurityValuationMethodAttributes.code}" 
				hideRequiredAsterisk="true"
				useShortLabel="false" />
			
	         <kul:htmlAttributeHeaderCell
				attributeEntry="${documentAttributes.holdingMonthEndDate}"
				hideRequiredAsterisk="true"				
				useShortLabel="false" />   
				         
	         <kul:htmlAttributeHeaderCell
				attributeEntry="${documentAttributes.securityUnitValue}"
				useShortLabel="false" />   
				         
	         <kul:htmlAttributeHeaderCell
				attributeEntry="${documentAttributes.securityMarketValue}"
				useShortLabel="false" />            
		</tr>
        <tr> 
            <td class="infoline">
	            <kul:htmlControlAttribute attributeEntry="${documentAttributes.securityId}" 
	            	property="document.securityId" 
	            	onblur="loadHistoryValueAdjustmentSecurityInfo(this.name);" 
	            	readOnly="${readOnly}"
	            	/>
	            &nbsp;
				<c:if test="${not readOnly}">
					<kul:lookup boClassName="org.kuali.kfs.module.endow.businessobject.Security"
						fieldConversions="id:document.securityId" />
				</c:if>
				<br/>
				<div id="security.description" class="fineprint">
            		 <kul:htmlControlAttribute attributeEntry="${documentAttributes.securityId}" property="document.security.description" readOnly="true" />
            	</div>	
            </td>
            <td>
            	<div id="document.security.securityClassCode" >
            		<kul:htmlControlAttribute attributeEntry="${ClassCodeAttributes.code}" property="document.security.classCode.codeAndDescription" readOnly="true" />
            	</div>
            </td>
            <td>
            	<div id="document.security.classCode.valuationMethod">
            		<kul:htmlControlAttribute attributeEntry="${SecurityValuationMethodAttributes.code}" property="document.security.classCode.securityValuationMethod.codeAndDescription" readOnly="true" />
            	</div>	
            </td>
            <td>
            	<div id="document.holdingMonthEndDate">
            		<kul:htmlControlAttribute attributeEntry="${documentAttributes.holdingMonthEndDate}" property="document.holdingMonthEndDate" readOnly="${readOnly}" />
            	</div>	
            </td>
            <td>
            	<div id="document.securityUnitValue">
            		<kul:htmlControlAttribute attributeEntry="${documentAttributes.securityUnitValue}" property="document.securityUnitValue" readOnly="${readOnly}" />
            	</div>	
            </td>
            <td>
            	<div id="document.securityMarketValue">
            		<kul:htmlControlAttribute attributeEntry="${documentAttributes.securityMarketValue}" property="document.securityMarketValue" readOnly="${readOnly}" />
            	</div>	
            </td>
        </tr>
			
		</table>
	</div>
</kul:tab>
