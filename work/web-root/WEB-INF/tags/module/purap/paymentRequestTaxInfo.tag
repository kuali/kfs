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

<%@ attribute name="documentAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for this row's fields." %> 
<c:set var="tabindexOverrideBase" value="45" />

<kul:tab tabTitle="Tax Information" defaultOpen="true" tabErrorKey="${PurapConstants.PAYMENT_REQUEST_TAX_TAB_ERRORS}">
    <div class="tab-container" align=center>
    	<c:if test="${taxAreaEditable}">
    		<h3>Tax Area Edits</h3>  
    	</c:if>  	
    	<c:if test="${!taxAreaEditable}">
    		<h3>Tax Information</h3>
    	</c:if>  	

        <table cellpadding="0" cellspacing="0" class="datatable" summary="Tax Info Section">

        	<tr>
            	<th align=right valign=middle class="bord-l-b">
                	<div align="right"><kul:htmlAttributeLabel forceRequired = "true" attributeEntry="${documentAttributes.taxClassificationCode}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                	<kul:htmlControlAttribute 
                		attributeEntry="${documentAttributes.taxClassificationCode}" property="document.taxClassificationCode" 
                		readOnly="${not taxAreaEditable}" tabindexOverride="${tabindexOverrideBase + 0}"/>
                </td>
                <th align=right valign=middle class="bord-l-b">
                	<div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.taxForeignSourceIndicator}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                	<kul:htmlControlAttribute 
                		attributeEntry="${documentAttributes.taxForeignSourceIndicator}" property="document.taxForeignSourceIndicator" 
                		readOnly="${not taxAreaEditable}" tabindexOverride="${tabindexOverrideBase + 3}"/>
                </td>
            </tr>
            
            <tr>
                <th align=right valign=middle class="bord-l-b">
                	<div align="right"><kul:htmlAttributeLabel forceRequired = "true" attributeEntry="${documentAttributes.taxFederalPercent}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                	<kul:htmlControlAttribute 
                		attributeEntry="${documentAttributes.taxFederalPercent}" property="document.taxFederalPercent" 
                		readOnly="${not taxAreaEditable}" tabindexOverride="${tabindexOverrideBase + 0}"/>
					&nbsp;                
                    <c:if test="${taxAreaEditable}">
                   		<kul:lookup boClassName="org.kuali.kfs.fp.businessobject.NonResidentAlienTaxPercent"
                    		lookupParameters="document.taxClassificationCode:incomeClassCode,'F':incomeTaxTypeCode,'Y':active"
                        	fieldConversions="incomeTaxPercent:document.taxFederalPercent"/>   
                    </c:if>                
                </td>
                <th align=right valign=middle class="bord-l-b">
                	<div align="right"><kul:htmlAttributeLabel  attributeEntry="${documentAttributes.taxExemptTreatyIndicator}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                	<kul:htmlControlAttribute 
                		attributeEntry="${documentAttributes.taxExemptTreatyIndicator}" property="document.taxExemptTreatyIndicator" 
                		readOnly="${not taxAreaEditable}" tabindexOverride="${tabindexOverrideBase + 3}"/>
                </td>
            </tr>
                        
            <tr>
                <th align=right valign=middle class="bord-l-b">
                	<div align="right"><kul:htmlAttributeLabel forceRequired = "true" attributeEntry="${documentAttributes.taxStatePercent}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                	<kul:htmlControlAttribute 
                		attributeEntry="${documentAttributes.taxStatePercent}" property="document.taxStatePercent" 
                		readOnly="${not taxAreaEditable}" tabindexOverride="${tabindexOverrideBase + 0}"/>
					&nbsp;                
                    <c:if test="${taxAreaEditable}">
                   		<kul:lookup boClassName="org.kuali.kfs.fp.businessobject.NonResidentAlienTaxPercent"
                    		lookupParameters="document.taxClassificationCode:incomeClassCode,'S':incomeTaxTypeCode,'Y':active"
                        	fieldConversions="incomeTaxPercent:document.taxStatePercent"/>   
                    </c:if>                
                </td>
                <th align=right valign=middle class="bord-l-b">
                	<div align="right"><kul:htmlAttributeLabel  attributeEntry="${documentAttributes.taxOtherExemptIndicator}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                	<kul:htmlControlAttribute 
                		attributeEntry="${documentAttributes.taxOtherExemptIndicator}" property="document.taxOtherExemptIndicator" 
                		readOnly="${not taxAreaEditable}" tabindexOverride="${tabindexOverrideBase + 3}"/>
                </td>
            </tr>

            <tr>
                <th align=right valign=middle class="bord-l-b">
                	<div align="right"><kul:htmlAttributeLabel forceRequired = "true" attributeEntry="${documentAttributes.taxCountryCode}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                	<kul:htmlControlAttribute 
                		attributeEntry="${documentAttributes.taxCountryCode}" property="document.taxCountryCode" 
                		readOnly="${not taxAreaEditable}" tabindexOverride="${tabindexOverrideBase + 0}"/>
                </td>
                <th align=right valign=middle class="bord-l-b">
                	<div align="right"><kul:htmlAttributeLabel  attributeEntry="${documentAttributes.taxGrossUpIndicator}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                	<kul:htmlControlAttribute 
                		attributeEntry="${documentAttributes.taxGrossUpIndicator}" property="document.taxGrossUpIndicator" 
                		readOnly="${not taxAreaEditable}" tabindexOverride="${tabindexOverrideBase + 3}"/>
                </td>
            </tr>

            <tr>
                <th align=right valign=middle class="bord-l-b">
                	<div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.taxNQIId}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                	<kul:htmlControlAttribute 
                		attributeEntry="${documentAttributes.taxNQIId}" property="document.taxNQIId" 
                		readOnly="${not taxAreaEditable}" tabindexOverride="${tabindexOverrideBase + 0}"/>
                </td>
                <th align=right valign=middle class="bord-l-b">
                	<div align="right"><kul:htmlAttributeLabel  attributeEntry="${documentAttributes.taxUSAIDPerDiemIndicator}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                	<kul:htmlControlAttribute 
                		attributeEntry="${documentAttributes.taxUSAIDPerDiemIndicator}" property="document.taxUSAIDPerDiemIndicator" 
                		readOnly="${not taxAreaEditable}" tabindexOverride="${tabindexOverrideBase + 3}"/>
                </td>
            </tr>

            <tr>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right">&nbsp;</div>
                </th>
                <td align=left valign=middle class="datacell">
                    &nbsp;
                </td>                
                <th align=right valign=middle class="bord-l-b">
                	<div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.taxSpecialW4Amount}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                	<kul:htmlControlAttribute 
                		attributeEntry="${documentAttributes.taxSpecialW4Amount}" property="document.taxSpecialW4Amount" 
                		readOnly="${not taxAreaEditable}" tabindexOverride="${tabindexOverrideBase + 3}"/>
                </td>
            </tr>
			<tr>
				<td class="infoline" colspan="4">
					<center>	
						<html:image src="${ConfigProperties.externalizable.images.url}tinybutton-clearall.gif" styleClass="tinybutton" property="methodToCall.clearTaxInfo" title="Clear All Info From NRA Tax Entries" alt="Clear All Info From NRA Tax Entries"/>
	    			</center>
   				</td>
    		</tr>
		</table> 				

    </div>
</kul:tab>
