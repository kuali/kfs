<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>
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

<html:hidden property="${camsAssetSystemProperty}.capitalAssetSystemIdentifier" />
<html:hidden property="${camsAssetSystemProperty}.versionNumber" />
<html:hidden property="${camsAssetSystemProperty}.objectId" />

<c:if test="${isRequisition}">
	<html:hidden property="${camsAssetSystemProperty}.purapDocumentIdentifier" />
</c:if>
<c:if test="${isPurchaseOrder}">
	<html:hidden property="${camsAssetSystemProperty}.documentNumber" />
</c:if>

<c:set var="lockCamsEntry"	value="${(not empty KualiForm.editingMode['lockCamsEntry'])}" /> 
	
<c:set var="addItemAssetUrl" value="methodToCall.addItemCapitalAssetByItem.line${ctr}" />
<c:set var="deleteItemAssetUrl" value="methodToCall.deleteItemCapitalAssetByItem.line${camsItemIndex}.(((${ctr})))" />
<c:set var="setManufacturerFromVendorUrl" value="methodToCall.setManufacturerFromVendorByItem.line${ctr}" />
<c:if test="${PurapConstants.CapitalAssetAvailability.ONCE eq availability}">
	<c:set var="addItemAssetUrl" value="methodToCall.addItemCapitalAssetByDocument.line${ctr}" />
	<c:set var="deleteItemAssetUrl" value="methodToCall.deleteItemCapitalAssetByDocument.line${camsItemIndex}.(((${ctr})))" />
	<c:set var="setManufacturerFromVendorUrl" value="methodToCall.setManufacturerFromVendorByDocument.line${ctr}" />	
</c:if>

<table width="100%" border="0" cellpadding="0" cellspacing="0" class="datatable">
  <tr>
    <td colspan="4"  align="right" valign="middle" style="padding:0px">
      <table class="datatable" summary="System Detail" cellpadding=0 cellspacing=0 style="width:100%">

	    <c:if test="${KualiForm.purchasingItemCapitalAssetAvailability eq availability}">
	    <tr>
		  <kul:htmlAttributeHeaderCell attributeEntry="${camsAssetAttributes.capitalAssetNumber}" align="right" width="250px" />    
	      <td class="datacell" align="left" colspan="3">
			<kul:htmlControlAttribute attributeEntry="${camsAssetAttributes.capitalAssetNumber}" property="document.purchasingCapitalAssetItems[${camsItemIndex}].newPurchasingItemCapitalAssetLine.capitalAssetNumber" readOnly="${lockCamsEntry or poItemInactive}"/>		
	      	&nbsp;
			<html:image property="${addItemAssetUrl}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" alt="Insert an Item Capital Asset" title="Add an Item Capital Asset" styleClass="tinybutton" />
		  </td>
	    </tr>
	    <tr>  
	      <kul:htmlAttributeHeaderCell attributeEntry="${camsAssetAttributes.capitalAssetNumber}" align="right"/>
	      <td class="datacell" valign="top" colspan="3">
			<logic:iterate indexId="idx" name="KualiForm" property="${camsAssetSystemProperty}.itemCapitalAssets" id="asset">
			    <html:hidden property="${camsAssetSystemProperty}.itemCapitalAssets[${idx}].itemCapitalAssetIdentifier" />
			    <html:hidden property="${camsAssetSystemProperty}.itemCapitalAssets[${idx}].versionNumber" />
			    <html:hidden property="${camsAssetSystemProperty}.itemCapitalAssets[${idx}].objectId" />
	 			<kul:htmlControlAttribute attributeEntry="${camsAssetAttributes.capitalAssetNumber}" property="${camsAssetSystemProperty}.itemCapitalAssets[${idx}].capitalAssetNumber" readOnly="true" />
  				<html:hidden property="${camsAssetSystemProperty}.itemCapitalAssets[${idx}].itemCapitalAssetIdentifier" />
  				<html:image property="${deleteItemAssetUrl}.((#${idx}#))" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" alt="Delete an Asset Number" title="Delete an Asset Number" styleClass="tinybutton" />
				  <br/>
			</logic:iterate>
		  </td>
	    </tr>
	    </c:if>
	
  	    <c:if test="${KualiForm.purchasingCapitalAssetSystemCommentsAvailability eq availability}">
		<tr>
		  <kul:htmlAttributeHeaderCell attributeEntry="${camsSystemAttributes.capitalAssetNoteText}" align="right" width="250px" />    
	      <td class="datacell" colspan="3">
			<kul:htmlControlAttribute attributeEntry="${camsSystemAttributes.capitalAssetNoteText}" property="${camsAssetSystemProperty}.capitalAssetNoteText" readOnly="${lockCamsEntry or poItemInactive}"/>
		  </td>		
		</tr>
	    </c:if>

		<c:if test="${KualiForm.purchasingCapitalAssetSystemDescriptionAvailability eq availability}">
        <tr>
          <kul:htmlAttributeHeaderCell attributeEntry="${camsSystemAttributes.capitalAssetSystemDescription}" align="right" width="250px"/>
          <td align="right" colspan="3" class="datacell">
			<kul:htmlControlAttribute attributeEntry="${camsSystemAttributes.capitalAssetSystemDescription}" property="${camsAssetSystemProperty}.capitalAssetSystemDescription" readOnly="${lockCamsEntry or poItemInactive}"/>
		  </td>
        </tr>
		</c:if>

		<c:if test="${KualiForm.purchasingCapitalAssetSystemAvailability eq availability}">
        <tr>
		  <kul:htmlAttributeHeaderCell attributeEntry="${camsSystemAttributes.capitalAssetNotReceivedCurrentFiscalYearIndicator}" align="right" />
          <td align="right" class="datacell">
			<kul:htmlControlAttribute attributeEntry="${camsSystemAttributes.capitalAssetNotReceivedCurrentFiscalYearIndicator}" property="${camsAssetSystemProperty}.capitalAssetNotReceivedCurrentFiscalYearIndicator" readOnly="${lockCamsEntry or poItemInactive}"/>
		  </td>
          <kul:htmlAttributeHeaderCell attributeEntry="${camsSystemAttributes.capitalAssetManufacturerName}" align="right" width="250px"/>
          <td align="right" class="datacell">
            <kul:htmlControlAttribute attributeEntry="${camsSystemAttributes.capitalAssetManufacturerName}" property="${camsAssetSystemProperty}.capitalAssetManufacturerName" readOnly="${lockCamsEntry or poItemInactive}"/>
            <c:if test="${not lockCamsEntry}">
            	<html:image property="${setManufacturerFromVendorUrl}" src="${ConfigProperties.externalizable.images.url}tinybutton-sameasvendor.gif" alt="Manufacturer Same as Vendor" styleClass="tinybutton"/>
            </c:if>
          </td>
        </tr>
        <tr>
          <th align="right" valign="middle" class="datacell">Asset Type:</th>
          <!-- td align="right" valign="middle" class="datacell">&nbsp;</td -->
          <td align="right" valign="middle" class="datacell">
            <bean:define id="capitalAssetNotReceivedCurrentFiscalYearIndicator" property="${camsAssetSystemProperty}.capitalAssetNotReceivedCurrentFiscalYearIndicator" name="KualiForm" />
            <c:choose>
                <c:when test="${capitalAssetNotReceivedCurrentFiscalYearIndicator eq 'Yes'}">
                    ${KualiForm.defaultAssetTypeCodeNotThisFiscalYear}&nbsp;
                </c:when>
                <c:otherwise>
                    <kul:htmlControlAttribute attributeEntry="${camsSystemAttributes.capitalAssetTypeCode}" property="${camsAssetSystemProperty}.capitalAssetTypeCode" readOnly="${lockCamsEntry or poItemInactive}"/>		
                    <kul:lookup boClassName="org.kuali.kfs.integration.cam.CapitalAssetManagementAssetType" fieldConversions="capitalAssetTypeCode:${camsAssetSystemProperty}.capitalAssetTypeCode"/> 
                </c:otherwise>
            </c:choose>
          </td>
          <kul:htmlAttributeHeaderCell attributeEntry="${camsSystemAttributes.capitalAssetModelDescription}" align="right" width="250px"/>
          <td align="right" class="datacell">
            <kul:htmlControlAttribute attributeEntry="${camsSystemAttributes.capitalAssetModelDescription}" property="${camsAssetSystemProperty}.capitalAssetModelDescription" readOnly="${lockCamsEntry or poItemInactive}"/>
		  </td>
        </tr>

        <tr>
          <td colspan="4" align="right" valign="middle" style="padding:0px" >

			<c:set var="locationPrefix" value=""/>
			<c:set var="addCapitalAssetLocationUrl" value="methodToCall.addCapitalAssetLocationByDocument.line${ctr}"/>			
			<c:if test="${availability eq PurapConstants.CapitalAssetAvailability.EACH}">
				<c:set var="locationPrefix" value="${camsAssetSystemProperty}."/>
				<c:set var="addCapitalAssetLocationUrl" value="methodToCall.addCapitalAssetLocationByItem.line${ctr}"/>
			</c:if>

			<!-- Cams Location Entry -->
			<purap:camsLocation camsLocationAttributes="${camsLocationAttributes}" ctr="${ctr}" ctr2="${ctr2}" camsAssetLocationProperty="${locationPrefix}newPurchasingCapitalAssetLocationLine" isEditable="true" availability="${availability}" />
			
			<table class="datatable" summary="" border="0" cellpadding="0" cellspacing="0" style="width:100%">
            <tr>
            	<td colspan="4" class="datacell" style="text-align:center;"> 
                	<html:image property="${addCapitalAssetLocationUrl}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" alt="Add a Asset Location" title="Add a Asset Location" styleClass="tinybutton" />
				</td>
            </tr>

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
			            <c:set var="isOpen" value="false" />
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
							<purap:camsLocation camsLocationAttributes="${camsLocationAttributes}" ctr="${ctr}" ctr2="${ctr2}" camsAssetLocationProperty="${camsAssetSystemProperty}.capitalAssetLocations[${ctr2}]" isEditable="false" availability="${availability}" poItemInactive="${poItemInactive}"/>
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

