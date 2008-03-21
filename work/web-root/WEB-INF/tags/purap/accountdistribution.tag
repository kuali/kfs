<%--
 Copyright 2007 The Kuali Foundation.
 
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
<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>

<%@ attribute name="accountingLineAttributes" required="true" type="java.util.Map"
	description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="itemAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields."%>

<script language="JavaScript" type="text/javascript" src="dwr/interface/CommodityCodeService.js"></script>
<script language="JavaScript" type="text/javascript" src="scripts/vendor/objectInfo.js"></script>

<html:hidden property="hideDistributeAccounts" />
<c:set var="amendmentEntry"	value="${(not empty KualiForm.editingMode['amendmentEntry'])}" />

<br />
<c:if test="${(fullEntryMode or amendmentEntry)}">
	<div align="center">
	<c:if test="${KualiForm.hideDistributeAccounts}">
		<html:image
		property="methodToCall.setupAccountDistribution"
		src="${ConfigProperties.externalizable.images.url}tinybutton-setaccdist.gif"
		alt="setup account distribution" title="setup account distribution"
		styleClass="tinybutton" />
	</c:if>
	<c:if test="${!KualiForm.hideDistributeAccounts}">
		<img src="${ConfigProperties.externalizable.images.url}tinybutton-setaccdist-dis.gif"
		alt="setup account distribution" border="0"
		styleClass="tinybutton" />
	</c:if>
	
	<html:image
	property="methodToCall.removeAccounts"
	src="${ConfigProperties.externalizable.images.url}tinybutton-remaccitems.gif"
	alt="remove accounts from all items"
	title="remove accounts from all items" styleClass="tinybutton" />
	
    <html:image
    property="methodToCall.clearItemsCommodityCodes"
    src="${ConfigProperties.externalizable.images.url}tinybutton-clear1.gif"
    alt="remove commodity codes from all items"
    title="remove commodity codes from all items" styleClass="tinybutton" />
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
	    <tr>
	        <td colspan="2" class="subhead">
	            <span class="subhead-left">Commodity Code</span>
	        </td>
	    </tr>
	    <tr>
	        <th align=right valign=middle class="bord-l-b">
	            <div align="right"><kul:htmlAttributeLabel attributeEntry="${itemAttributes.commodityCode}" /></div>
	        </th>
	        <td align=left valign=middle class="datacell">
                    <c:set var="commodityCodeField"  value="distributePurchasingCommodityCode" />
                    <c:set var="commodityDescriptionField"  value="distributePurchasingCommodityDescription" />	        
	                        <kul:htmlControlAttribute 
	                            attributeEntry="${itemAttributes.commodityCode}" 
	                            property="distributePurchasingCommodityCode"
	                            readOnly="${not (fullEntryMode or amendmentEntry)}"
	                            onblur="loadCommodityCodeInfo( '${commodityCodeField}', '${commodityDescriptionField}' );${onblur}"/>
                        <c:if test="${fullEntryMode}">   
                            <kul:lookup boClassName="org.kuali.module.vendor.bo.CommodityCode" 
                                fieldConversions="purchasingCommodityCode:distributePurchasingCommodityCode"
                                lookupParameters="'Y':active"/>    
                        </c:if>
                    <div id="distributePurchasingCommodityDescription.div" class="fineprint">
                        <html:hidden write="true" property="${commodityDescriptionField}"/>&nbsp;        
                    </div>    
	        </td>
	    </tr>
        <tr>
            <th colspan="2">&nbsp;</td>
        </tr>
    </table>

	<purap:puraccountingLines editingMode="${accountingLineEditingMode}"
		editableAccounts="${KualiForm.editableAccounts}"
		sourceAccountingLinesOnly="true"
		optionalFields="accountLinePercent"
		accountingLineAttributes="${accountingLineAttributes}"
		accountPrefix="accountDistribution" hideTotalLine="true"
		hideFields="amount" accountingAddLineIndex="-2" suppressBaseline="true"/>

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
