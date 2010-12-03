<%--
 Copyright 2009 The Kuali Foundation
 
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
<%@ attribute name="camsItemIndex" required="true" description="cams item index"%>
<%@ attribute name="camsSystemAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="camsAssetAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="camsLocationAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="ctr" required="true" description="item count"%>
<%@ attribute name="camsAssetSystemProperty" required="true" description="String that represents the prefix of the property name to store into the document on the form."%>
<%@ attribute name="availability" required="true" description="Determines if this is a capture once or each tag or for each"%>
<%@ attribute name="isRequisition" required="false" description="Determines if this is a requisition document"%>
<%@ attribute name="isPurchaseOrder" required="false" description="Determines if this is a requisition document"%>
<%@ attribute name="poItemInactive" required="false" description="True if the item this is part of is inactive."%>


<c:set var="fullEntryMode" value="${KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT] && (empty KualiForm.editingMode['restrictFiscalEntry'])}" />
<c:set var="addItemAssetUrl" value="methodToCall.addItemCapitalAssetByItem.line${ctr}" />
<c:set var="deleteItemAssetUrl" value="methodToCall.deleteItemCapitalAssetByItem.line${camsItemIndex}.(((${ctr})))" />
<c:set var="setManufacturerFromVendorUrl" value="methodToCall.setManufacturerFromVendorByItem.line${ctr}" />
<c:set var="selectNotCurrentYearUrl" value="methodToCall.selectNotCurrentYearByItem.line${ctr}" />
<c:set var="clearNotCurrentYearUrl" value="methodToCall.clearNotCurrentYearByItem.line${ctr}" />
<c:if test="${PurapConstants.CapitalAssetAvailability.ONCE eq availability}">
	<c:set var="addItemAssetUrl" value="methodToCall.addItemCapitalAssetByDocument.line${ctr}" />
	<c:set var="deleteItemAssetUrl" value="methodToCall.deleteItemCapitalAssetByDocument.line${camsItemIndex}.(((${ctr})))" />
	<c:set var="setManufacturerFromVendorUrl" value="methodToCall.setManufacturerFromVendorByDocument.line${ctr}" />	
    <c:set var="selectNotCurrentYearUrl" value="methodToCall.selectNotCurrentYearByDocument.line${ctr}" />    
    <c:set var="clearNotCurrentYearUrl" value="methodToCall.clearNotCurrentYearByDocument.line${ctr}" />    
</c:if>
<c:set var="tabindexOverrideBase" value="60" />

<table width="100%" border="0" cellpadding="0" cellspacing="0" class="datatable">
  <tr>
    <td colspan="4"  align="right" valign="middle" style="padding:0px">
      <table class="datatable" summary="System Detail" cellpadding=0 cellspacing=0 style="width:100%">

	    <c:if test="${KualiForm.purchasingItemCapitalAssetAvailability eq availability}">
	    <tr>
          <th align="right" valign="middle" class="datacell">Add Asset Number:</th>
	      <td class="datacell" align="left" colspan="3">
			<kul:htmlControlAttribute attributeEntry="${camsAssetAttributes.capitalAssetNumber}" property="document.purchasingCapitalAssetItems[${camsItemIndex}].newPurchasingItemCapitalAssetLine.capitalAssetNumber" readOnly="${!(fullEntryMode or amendmentEntry) or poItemInactive}" tabindexOverride="${tabindexOverrideBase + 0}"/>
              <c:if test="${(fullEntryMode or amendmentEntry) and !poItemInactive}">
                  <kul:lookup boClassName="org.kuali.kfs.integration.cam.CapitalAssetManagementAsset" fieldConversions="capitalAssetNumber:document.purchasingCapitalAssetItems[${camsItemIndex}].newPurchasingItemCapitalAssetLine.capitalAssetNumber" lookupParameters="document.purchasingCapitalAssetItems[${camsItemIndex}].newPurchasingItemCapitalAssetLine.capitalAssetNumber:capitalAssetNumber"/>
              </c:if>
					
	      	&nbsp;
	      	<c:if test="${(fullEntryMode or amendmentEntry) and !poItemInactive}">
			    <html:image property="${addItemAssetUrl}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" alt="Insert an Item Capital Asset" title="Add an Item Capital Asset" styleClass="tinybutton" />
			</c:if>
		  </td>
	    </tr>
	    <tr>  
          <th width="20%" align=right valign=middle class="bord-l-b">
             <div align="right"><kul:htmlAttributeLabel attributeEntry="${camsAssetAttributes.capitalAssetNumber}" /></div>
          </th>
	      <td class="datacell" valign="top" colspan="3">
            <table class="datatable" summary="System Detail" cellpadding=0 cellspacing=0 style="width:25%">
	            <logic:iterate indexId="idx" name="KualiForm" property="${camsAssetSystemProperty}.itemCapitalAssets" id="asset">
	               <tr>
	                 <td class="datacell" align="left">
	                     <kul:htmlControlAttribute attributeEntry="${camsAssetAttributes.capitalAssetNumber}" property="${camsAssetSystemProperty}.itemCapitalAssets[${idx}].capitalAssetNumber" readOnly="true" />
	                 </td>
	                 <td class="datacell" align="left">
	                     <c:if test="${(fullEntryMode or amendmentEntry) and !poItemInactive}">
	                         <html:image property="${deleteItemAssetUrl}.((#${idx}#))" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" alt="Delete an Asset Number" title="Delete an Asset Number" styleClass="tinybutton" />
	                     </c:if>
	                 </td>
	               </tr>
	            </logic:iterate>
            </table>
		  </td>
	    </tr>
	    </c:if>
	
	    <c:if test="${(KualiForm.purchasingCapitalAssetSystemCommentsAvailability eq availability) or (KualiForm.purchasingCapitalAssetSystemDescriptionAvailability eq availability)}">
        <tr>
	  	    <c:if test="${KualiForm.purchasingCapitalAssetSystemCommentsAvailability eq availability}">
	            <bean:define id="capitalAssetNoteTextValue" property="${camsAssetSystemProperty}.capitalAssetNoteText" name="KualiForm" />
	            <th width="20%" align=right valign=middle class="bord-l-b">
	               <div align="right"><kul:htmlAttributeLabel attributeEntry="${camsSystemAttributes.capitalAssetNoteText}" /></div>
  	            </th>
		        <td class="datacell">
			   	    <kul:htmlControlAttribute 
			   	        attributeEntry="${camsSystemAttributes.capitalAssetNoteText}" 
			   	        property="${camsAssetSystemProperty}.capitalAssetNoteText" 
			   	        readOnly="${!(fullEntryMode or amendmentEntry) or poItemInactive}" 
			   	        readOnlyAlternateDisplay="${fn:replace(capitalAssetNoteTextValue, Constants.NEWLINE, '<br/>')}"		 
			   	        tabindexOverride="${tabindexOverrideBase + 0}"/>
			    </td>		
		    </c:if>
	        <c:if test="${!(KualiForm.purchasingCapitalAssetSystemCommentsAvailability eq availability)}">
			    <th>&nbsp;</th>
			    <td class="datacell">&nbsp;</td>
	        </c:if>
			<c:if test="${KualiForm.purchasingCapitalAssetSystemDescriptionAvailability eq availability}">
			    <bean:define id="capitalAssetSystemDescriptionValue" property="${camsAssetSystemProperty}.capitalAssetSystemDescription" name="KualiForm" />
                <th width="20%" align=right valign=middle class="bord-l-b">
                   <div align="right"><kul:htmlAttributeLabel attributeEntry="${camsSystemAttributes.capitalAssetSystemDescription}" /></div>
                </th>
	            <td class="datacell">
		    		<kul:htmlControlAttribute 
		    		    attributeEntry="${camsSystemAttributes.capitalAssetSystemDescription}" 
		    		    property="${camsAssetSystemProperty}.capitalAssetSystemDescription" 
		    		    readOnly="${!(fullEntryMode or amendmentEntry) or poItemInactive}" 
		    		    readOnlyAlternateDisplay="${fn:replace(capitalAssetSystemDescriptionValue, Constants.NEWLINE, '<br/>')}"		    		    
		    		    tabindexOverride="${tabindexOverrideBase + 3}"/>
			    </td>
			</c:if>
	        <c:if test="${!(KualiForm.purchasingCapitalAssetSystemDescriptionAvailability eq availability)}">
	            <th>&nbsp;</th>
	            <td class="datacell">&nbsp;</td>
	        </c:if>
        </tr>
        </c:if>

		<c:if test="${KualiForm.purchasingCapitalAssetSystemAvailability eq availability}">
        <tr>
          <th width="20%" align=right valign=middle class="bord-l-b">
             <div align="right"><kul:htmlAttributeLabel attributeEntry="${camsSystemAttributes.capitalAssetNotReceivedCurrentFiscalYearIndicator}" /></div>
          </th>
          <td align="right" class="datacell">
			<kul:htmlControlAttribute attributeEntry="${camsSystemAttributes.capitalAssetNotReceivedCurrentFiscalYearIndicator}" property="${camsAssetSystemProperty}.capitalAssetNotReceivedCurrentFiscalYearIndicator" readOnly="true"/>&nbsp;
            <c:set var="notCurrentYear" value="false" />
            <logic:equal name="KualiForm" property="${camsAssetSystemProperty}.capitalAssetNotReceivedCurrentFiscalYearIndicator" value="Yes">
                <c:set var="notCurrentYear" value="true" />
            </logic:equal>
            <c:if test="${(fullEntryMode or amendmentEntry) and !poItemInactive and !notCurrentYear}">
                <html:image property="${selectNotCurrentYearUrl}" src="${ConfigProperties.externalizable.images.url}tinybutton-select.gif" alt="Select" styleClass="tinybutton"/>
            </c:if>
            <c:if test="${(fullEntryMode or amendmentEntry) and !poItemInactive and notCurrentYear}">
                <html:image property="${clearNotCurrentYearUrl}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-clear1.gif" alt="Clear Selection" styleClass="tinybutton"/>
            </c:if>
		  </td>
          <th width="20%" align=right valign=middle class="bord-l-b">
             <div align="right"><kul:htmlAttributeLabel attributeEntry="${camsSystemAttributes.capitalAssetManufacturerName}" /></div>
          </th>
          <td align="right" class="datacell">
            <kul:htmlControlAttribute attributeEntry="${camsSystemAttributes.capitalAssetManufacturerName}" property="${camsAssetSystemProperty}.capitalAssetManufacturerName" readOnly="${!(fullEntryMode or amendmentEntry) or poItemInactive}" tabindexOverride="${tabindexOverrideBase + 3}"/>
            <c:if test="${(fullEntryMode or amendmentEntry) and !poItemInactive}">
            	<html:image property="${setManufacturerFromVendorUrl}" src="${ConfigProperties.externalizable.images.url}tinybutton-sameasvendor.gif" alt="Manufacturer Same as Vendor" styleClass="tinybutton"/>
            </c:if>
          </td>
        </tr>
        <tr>
          <th width="20%" align=right valign=middle class="bord-l-b">
             <div align="right"><kul:htmlAttributeLabel attributeEntry="${camsSystemAttributes.capitalAssetTypeCode}" /></div>
          </th>
          <td align="right" valign="middle" class="datacell">
              <kul:htmlControlAttribute attributeEntry="${camsSystemAttributes.capitalAssetTypeCode}" property="${camsAssetSystemProperty}.capitalAssetTypeCode" readOnly="${!(fullEntryMode or amendmentEntry) or poItemInactive or notCurrentYear}" tabindexOverride="${tabindexOverrideBase + 0}"/>		
              <c:if test="${(fullEntryMode or amendmentEntry) and !poItemInactive and !notCurrentYear}">
                  <kul:lookup boClassName="org.kuali.kfs.integration.cam.CapitalAssetManagementAssetType" fieldConversions="capitalAssetTypeCode:${camsAssetSystemProperty}.capitalAssetTypeCode" lookupParameters="${camsAssetSystemProperty}.capitalAssetTypeCode:capitalAssetTypeCode"/>
              </c:if>
          </td>
          <th width="20%" align=right valign=middle class="bord-l-b">
             <div align="right"><kul:htmlAttributeLabel attributeEntry="${camsSystemAttributes.capitalAssetModelDescription}" /></div>
          </th>
          <td align="right" class="datacell">
            <kul:htmlControlAttribute attributeEntry="${camsSystemAttributes.capitalAssetModelDescription}" property="${camsAssetSystemProperty}.capitalAssetModelDescription" readOnly="${!(fullEntryMode or amendmentEntry) or poItemInactive}" tabindexOverride="${tabindexOverrideBase + 3}"/>
		  </td>
        </tr>
        <c:if test="${KualiForm.purchasingCapitalAssetCountAssetNumberAvailability eq availability}">
	        <tr>
                <th width="20%" align=right valign=middle class="bord-l-b">
                   <div align="right"><kul:htmlAttributeLabel attributeEntry="${camsSystemAttributes.capitalAssetCountAssetNumber}" /></div>
                </th>
	            <td class="datacell">
	                <kul:htmlControlAttribute attributeEntry="${camsSystemAttributes.capitalAssetCountAssetNumber}" property="${camsAssetSystemProperty}.capitalAssetCountAssetNumber" readOnly="${!(fullEntryMode or amendmentEntry) or poItemInactive}" tabindexOverride="${tabindexOverrideBase + 0}"/>
	            </td>       
	            <th>&nbsp;</th>
	            <td class="datacell">&nbsp;</td>
	        </tr>
        </c:if>

        <tr>
          <td colspan="4" align="right" valign="middle" style="padding:0px" >

			<c:set var="locationPrefix" value=""/>
			<c:set var="addCapitalAssetLocationUrl" value="methodToCall.addCapitalAssetLocationByDocument.line${ctr}"/>			
			<c:if test="${availability eq PurapConstants.CapitalAssetAvailability.EACH}">
				<c:set var="locationPrefix" value="${camsAssetSystemProperty}."/>
				<c:set var="addCapitalAssetLocationUrl" value="methodToCall.addCapitalAssetLocationByItem.line${ctr}"/>
			</c:if>

			<!-- Cams Location Entry -->
            <c:if test="${(fullEntryMode or amendmentEntry) and !poItemInactive}">
    			<purap:camsLocation camsLocationAttributes="${camsLocationAttributes}" ctr="${ctr}" ctr2="new" camsAssetLocationProperty="${locationPrefix}newPurchasingCapitalAssetLocationLine" availability="${availability}" poItemInactive="${poItemInactive}"/>
    	    </c:if>
			
			<table class="datatable" summary="" border="0" cellpadding="0" cellspacing="0" style="width:100%">
            <c:if test="${(fullEntryMode or amendmentEntry) and !poItemInactive}">
	            <tr>
	            	<td colspan="4" class="datacell" style="text-align:center;"> 
	                	<html:image property="${addCapitalAssetLocationUrl}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" alt="Add a Asset Location" title="Add a Asset Location" styleClass="tinybutton" />
					</td>
	            </tr>
	        </c:if>

			<logic:iterate indexId="ctr2" name="KualiForm" property="${camsAssetSystemProperty}.capitalAssetLocations" id="location">				
								
				<!-- Cams Locations -->
			    <c:set var="currentTabIndex" value="${KualiForm.currentTabIndex}" scope="request" />
			    <c:set var="topLevelTabIndex" value="${KualiForm.currentTabIndex}" scope="request" />
			    <c:set var="tabTitle" value="AccountingLines-${currentTabIndex}" />
			    <c:set var="tabKey" value="${kfunc:generateTabKey(tabTitle)}"/>
			    <!--  hit form method to increment tab index -->
			    <c:set var="dummyIncrementer" value="${kfunc:incrementTabIndex(KualiForm, tabKey)}" />
			    <c:set var="currentTab" value="${kfunc:getTabState(KualiForm, tabKey)}"/>
			
			    <%-- default to closed --%>
			    <c:choose>
			        <c:when test="${empty currentTab}">
			            <c:set var="isOpen" value="true" />
			        </c:when>
			        <c:when test="${!empty currentTab}">
			            <c:set var="isOpen" value="${currentTab == 'OPEN'}" />
			        </c:when>
			    </c:choose>
				
			    <html:hidden property="tabStates(${tabKey})" value="${(isOpen ? 'OPEN' : 'CLOSE')}" />
			
				<tr>
					<td class="infoline" valign="middle" colspan="4">
					<table border="0" cellspacing="0" cellpadding="0" width="100%">
					<tr>
						<th colspan="10" style="padding: 0px; border-right: none;">
						    <div align=left>
						  	    <c:if test="${isOpen == 'true' || isOpen == 'TRUE'}">
						         <html:image property="methodToCall.toggleTab.tab${tabKey}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-hide.gif" alt="hide" title="toggle" styleClass="tinybutton" styleId="tab-${tabKey}-imageToggle" onclick="javascript: return toggleTab(document, '${tabKey}'); " />
						     </c:if>
						     <c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
						         <html:image property="methodToCall.toggleTab.tab${tabKey}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-show.gif" alt="show" title="toggle" styleClass="tinybutton" styleId="tab-${tabKey}-imageToggle" onclick="javascript: return toggleTab(document, '${tabKey}'); " />
						     </c:if>
						    	Address ${ctr2+1}
						    </div>
						</th>
					</tr>
				
					<c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
					<tr style="display: none;"  id="tab-${tabKey}-div">
					</c:if>   
				        <th colspan="10" style="padding:0;">
							<!-- Cams Location List -->
							<purap:camsLocation camsLocationAttributes="${camsLocationAttributes}" ctr="${ctr}" ctr2="${ctr2}" camsAssetLocationProperty="${camsAssetSystemProperty}.capitalAssetLocations[${ctr2}]" availability="${availability}" poItemInactive="${poItemInactive}"/>
				        </th>
				    
					<c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
					</tr>
					</c:if>
				
					</table>
					</td>
				</tr>
					
			</logic:iterate>
			</table>
          </td>
        </tr>
	    </c:if>

      </table>
      </td>
  </tr>
</table>

