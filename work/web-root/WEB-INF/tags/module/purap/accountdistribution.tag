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

<%@ attribute name="accountingLineAttributes" required="true" type="java.util.Map"
	description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="itemAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="displayCommodityCodeFields" required="true" description="Boolean to indicate if commodity code relatedfields should be displayed"%>

<script language="JavaScript" type="text/javascript" src="scripts/vnd/objectInfo.js"></script>

<c:set var="fullEntryMode" value="${KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />
<c:set var="amendmentEntry"	value="${(not empty KualiForm.editingMode['amendmentEntry'])}" />
<c:set var="clearAllTaxes" value="${(not empty KualiForm.editingMode['clearAllTaxes'])}" />
<c:set var="tabindexOverrideBase" value="50" />

<br />
<c:if test="${(fullEntryMode or amendmentEntry)}">
	<div align="center">
	<c:if test="${KualiForm.hideDistributeAccounts and !KualiForm.editingMode['disableSetupAccountDistribution']}">
		<html:image
		property="methodToCall.setupAccountDistribution"
		src="${ConfigProperties.externalizable.images.url}tinybutton-setdist.gif"
		alt="setup distribution" title="setup distribution"
		styleClass="tinybutton" />
	</c:if>
	<c:if test="${!KualiForm.hideDistributeAccounts and !KualiForm.editingMode['disableSetupAccountDistribution']}">
		<img src="${ConfigProperties.externalizable.images.url}tinybutton-setdist1.gif"
		alt="setup account distribution" border="0"
		styleClass="tinybutton" />
	</c:if>
	<c:if test="${!KualiForm.editingMode['disableRemoveAccounts']}">
	<html:image
	property="methodToCall.removeAccounts"
	src="${ConfigProperties.externalizable.images.url}tinybutton-remaccitems.gif"
	alt="remove accounts from all items"
	title="remove accounts from all items" styleClass="tinybutton" />
	</c:if>
	
	<c:if test="${displayCommodityCodeFields}">	
        <html:image
        property="methodToCall.clearItemsCommodityCodes"
        src="${ConfigProperties.externalizable.images.url}tinybutton-remcomcod.gif"
        alt="remove commodity codes from all items"
        title="remove commodity codes from all items" styleClass="tinybutton" />
    </c:if>
    
	<html:image 
	property="methodToCall.showAllAccounts" 
	src="${ConfigProperties.externalizable.images.url}tinybutton-expandallacc.gif" 
	alt="expand all accounts" 
	title="expand all accounts" 
	styleClass="tinybutton"  />

	<html:image 
	property="methodToCall.hideAllAccounts" 
	src="${ConfigProperties.externalizable.images.url}tinybutton-collallacc.gif" 
	alt="collapse all accounts" 
	title="collapse all accounts" 
	styleClass="tinybutton"  />
</c:if>

<c:if test="${(fullEntryMode or amendmentEntry) and (clearAllTaxes)}">
	<html:image 
	    property="methodToCall.clearAllTaxes" 
	    src="${ConfigProperties.externalizable.images.url}tinybutton-clearalltax.gif" 
	    alt="Clear all tax" 
	    title="Clear all tax" styleClass="tinybutton" />
    </div>
</c:if>
	
<c:if test="${!KualiForm.hideDistributeAccounts}">

    <c:choose>
        <c:when test="${(not empty KualiForm.editingMode['amendmentEntry'])}">
            <c:set target="${KualiForm.accountingLineEditingMode}" property="fullEntry" value="true" />
            <c:set var="accountingLineEditingMode" value = "${KualiForm.accountingLineEditingMode}"/>
        </c:when>
        <c:otherwise>
            <c:set var="accountingLineEditingMode" value = "${KualiForm.editingMode}"/>
        </c:otherwise>
    </c:choose>
    
    <table width="100%" border="0" cellpadding="0" cellspacing="0"
        class="datatable">
        <c:if test="${displayCommodityCodeFields}">	
	    <tr>
	        <td colspan="2" class="subhead">
	            <span class="subhead-left">Commodity Code</span>
	        </td>
	    </tr>
	    <tr>
	        <th align=right valign=middle class="bord-l-b">
	            <div align="right"><kul:htmlAttributeLabel attributeEntry="${itemAttributes.purchasingCommodityCode}" /></div>
	        </th>
	        <td align=left valign=middle class="datacell">
                <c:set var="commodityCodeField"  value="distributePurchasingCommodityCode" />
                <c:set var="commodityDescriptionField"  value="distributePurchasingCommodityDescription" />
	            <kul:htmlControlAttribute 
	            	attributeEntry="${itemAttributes.purchasingCommodityCode}" 
	                property="distributePurchasingCommodityCode"
	                readOnly="${not (fullEntryMode or amendmentEntry)}"
	                tabindexOverride="${tabindexOverrideBase + 0}"
	                onblur="loadCommodityCodeDescription( '${commodityCodeField}', '${commodityDescriptionField}' );${onblur}"/>
                <c:if test="${fullEntryMode}">   
                	<kul:lookup boClassName="org.kuali.kfs.vnd.businessobject.CommodityCode" 
                                fieldConversions="purchasingCommodityCode:distributePurchasingCommodityCode,commodityDescription:distributePurchasingCommodityDescription"
                                lookupParameters="'Y':active"/>    
                </c:if>
                <div id="distributePurchasingCommodityDescription.div" class="fineprint">
                    <html:hidden write="true" property="${commodityDescriptionField}"/>&nbsp;        
                </div>    
	        </td>
	    </tr>
	    </c:if>
        <tr>
            <th colspan="2">&nbsp;</td>
        </tr>
    </table>


    <sys-java:accountingLines>
        <sys-java:accountingLineGroup newLinePropertyName="accountDistributionnewSourceLine" collectionPropertyName="accountDistributionsourceAccountingLines" collectionItemPropertyName="accountDistributionsourceAccountingLine" attributeGroupName="distributeSource" />
    </sys-java:accountingLines>
		        
	<div align="center">
		<html:image
		property="methodToCall.doDistribution"
		src="${ConfigProperties.externalizable.images.url}tinybutton-disttoitems.gif"
		alt="do account distribution"
		title="do account distribution" styleClass="tinybutton" />
		<html:image
		property="methodToCall.cancelAccountDistribution"
		src="${ConfigProperties.kr.externalizable.images.url}tinybutton-cancel.gif"
		alt="cancel account distribution"
		title="cancel account distribution" styleClass="tinybutton" />
	</div>
</c:if>
<br />
