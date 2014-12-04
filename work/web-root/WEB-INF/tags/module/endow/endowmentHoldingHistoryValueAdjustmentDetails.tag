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
