<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>
<%@ attribute name="camsItemAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="camsSystemAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="camsAssetAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="camsLocationAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="ctr" required="true" description="item count"%>

<table width="100%" border="0" cellpadding="0" cellspacing="0" class="datatable">
  <tr>
    <kul:htmlAttributeHeaderCell attributeEntry="${camsItemAttributes.capitalAssetTransactionTypeCode}" align="right" />
    <td class="datacell">
		<kul:htmlControlAttribute attributeEntry="${camsItemAttributes.capitalAssetTransactionTypeCode}" property="document.purchasingCapitalAssetItems[${ctr}].capitalAssetTransactionTypeCode" readOnly="${false}"/>		
	</td>
	<kul:htmlAttributeHeaderCell attributeEntry="${camsAssetAttributes.capitalAssetNumber}" align="right" />    
    <td class="datacell" align="left">
		<kul:htmlControlAttribute attributeEntry="${camsAssetAttributes.capitalAssetNumber}" property="newPurchasingItemCapitalAssetLine.capitalAssetNumber" />		
      	&nbsp;
		<html:image property="methodToCall.addItemCapitalAsset.line${ctr}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" alt="Insert an Item Capital Asset" title="Add an Item Capital Asset" styleClass="tinybutton" />
	</td>
  </tr>
  <tr>
	<kul:htmlAttributeHeaderCell attributeEntry="${camsSystemAttributes.capitalAssetNoteText}" align="right" />    
    <td class="datacell">
		<kul:htmlControlAttribute attributeEntry="${camsSystemAttributes.capitalAssetNoteText}" property="document.purchasingCapitalAssetItems[${ctr}].purchasingCapitalAssetSystem.capitalAssetNoteText" readOnly="${false}"/>
	</td>
    <kul:htmlAttributeHeaderCell attributeEntry="${camsAssetAttributes.capitalAssetNumber}" align="right" />
    <td class="datacell" valign="top">
		<logic:iterate indexId="idx" name="KualiForm" property="document.purchasingCapitalAssetItems[${ctr}].purchasingCapitalAssetSystem.purchasingItemCapitalAssets" id="asset">
			<kul:htmlControlAttribute attributeEntry="${camsAssetAttributes.capitalAssetNumber}" property="document.purchasingCapitalAssetItems[${ctr}].purchasingCapitalAssetSystem.purchasingItemCapitalAssets[${idx}].capitalAssetNumber" readOnly="true" />
			<html:image property="methodToCall.deleteItemCapitalAsset.(((${ctr}))).((#${idx}#))" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" alt="Delete an Asset Number" title="Delete an Asset Number" styleClass="tinybutton" />
			<br/>
		</logic:iterate>
	</td>
  </tr>
  <tr>
    <td colspan="4"  align="right" valign="middle" style="padding:0px">
      <table class="datatable" summary="System Detail" cellpadding=0 cellspacing=0 style="width:100%">
        <tr>
          <td colspan="4" class="subhead">System Detail</td>
        </tr>

        <tr>
          <kul:htmlAttributeHeaderCell attributeEntry="${camsSystemAttributes.capitalAssetSystemDescription}" align="right" />
          <td align="right" colspan="3" class="datacell">
			<kul:htmlControlAttribute attributeEntry="${camsSystemAttributes.capitalAssetSystemDescription}" property="document.purchasingCapitalAssetItems[${ctr}].purchasingCapitalAssetSystem.capitalAssetSystemDescription" readOnly="${false}"/>
		  </td>
        </tr>
        <tr>
		  <kul:htmlAttributeHeaderCell attributeEntry="${camsSystemAttributes.capitalAssetNotReceivedCurrentFiscalYearIndicator}" align="right" />
          <td align="right" class="datacell">
			<kul:htmlControlAttribute attributeEntry="${camsSystemAttributes.capitalAssetNotReceivedCurrentFiscalYearIndicator}" property="document.purchasingCapitalAssetItems[${ctr}].purchasingCapitalAssetSystem.capitalAssetNotReceivedCurrentFiscalYearIndicator" readOnly="${false}"/>
		  </td>
          <kul:htmlAttributeHeaderCell attributeEntry="${camsSystemAttributes.capitalAssetManufacturerName}" align="right" />
          <td align="right" class="datacell">
            <kul:htmlControlAttribute attributeEntry="${camsSystemAttributes.capitalAssetManufacturerName}" property="document.purchasingCapitalAssetItems[${ctr}].purchasingCapitalAssetSystem.capitalAssetManufacturerName" readOnly="${false}"/>
          </td>
        </tr>
        <tr>
          <th align="right" valign="middle" class="datacell">Asset Type:</th>
          <td align="right" valign="middle" class="datacell">&nbsp;</td>
          <kul:htmlAttributeHeaderCell attributeEntry="${camsSystemAttributes.capitalAssetModelDescription}" align="right" />
          <td align="right" class="datacell">
            <kul:htmlControlAttribute attributeEntry="${camsSystemAttributes.capitalAssetModelDescription}" property="document.purchasingCapitalAssetItems[${ctr}].purchasingCapitalAssetSystem.capitalAssetModelDescription" readOnly="${false}"/>
		  </td>
        </tr>
        <tr>
          <td colspan="4" align="right" valign="middle" style="padding:0px" >
              <table class="datatable" summary="" border="0" cellpadding="0" cellspacing="0" style="width:100%">
                  <tr>
                    <td colspan="4" class="subhead">Locations</td>
                  </tr>
                  <tr>
                    <kul:htmlAttributeHeaderCell attributeEntry="${camsLocationAttributes.itemQuantity}" align="right" />
                    <td class="datacell">
						<kul:htmlControlAttribute attributeEntry="${camsLocationAttributes.itemQuantity}" property="newPurchasingCapitalAssetLocationLine.itemQuantity" readOnly="${false}"/>
					</td>
                    <kul:htmlAttributeHeaderCell attributeEntry="${camsLocationAttributes.capitalAssetLine1Address}" align="right" />
                    <td class="datacell">
						<kul:htmlControlAttribute attributeEntry="${camsLocationAttributes.capitalAssetLine1Address}" property="newPurchasingCapitalAssetLocationLine.capitalAssetLine1Address" readOnly="${false}"/>
					</td>
                  </tr>
                  <tr>
                    <kul:htmlAttributeHeaderCell attributeEntry="${camsLocationAttributes.buildingCode}" align="right" />
                    <td class="datacell">
						<kul:htmlControlAttribute attributeEntry="${camsLocationAttributes.buildingCode}" property="newPurchasingCapitalAssetLocationLine.buildingCode" readOnly="${true}"/>
                    	<kul:lookup boClassName="org.kuali.kfs.sys.businessobject.Building"
                    		lookupParameters="newPurchasingCapitalAssetLocationLine.campusCode:campusCode"
                    		fieldConversions="buildingCode:newPurchasingCapitalAssetLocationLine.buildingCode,campusCode:newPurchasingCapitalAssetLocationLine.campusCode,buildingStreetAddress:newPurchasingCapitalAssetLocationLine.capitalAssetLine1Address,buildingAddressCityName:newPurchasingCapitalAssetLocationLine.capitalAssetCityName,buildingAddressStateCode:newPurchasingCapitalAssetLocationLine.capitalAssetStateCode,buildingAddressZipCode:newPurchasingCapitalAssetLocationLine.capitalAssetPostalCode"/>
					</td>
                    <kul:htmlAttributeHeaderCell attributeEntry="${camsLocationAttributes.capitalAssetCityName}" align="right" />
                    <td class="datacell">
						<kul:htmlControlAttribute attributeEntry="${camsLocationAttributes.capitalAssetCityName}" property="newPurchasingCapitalAssetLocationLine.capitalAssetCityName" readOnly="${false}"/>
					</td>
                  </tr>
                  <tr>
                    <kul:htmlAttributeHeaderCell attributeEntry="${camsLocationAttributes.campusCode}" align="right" />
                    <td class="datacell">
						<kul:htmlControlAttribute attributeEntry="${camsLocationAttributes.campusCode}" property="newPurchasingCapitalAssetLocationLine.campusCode" readOnly="${true}"/>
					</td>
                    <kul:htmlAttributeHeaderCell attributeEntry="${camsLocationAttributes.capitalAssetStateCode}" align="right" />
					<td class="datacell">
                    	<kul:htmlControlAttribute attributeEntry="${camsLocationAttributes.capitalAssetStateCode}" property="newPurchasingCapitalAssetLocationLine.capitalAssetStateCode" readOnly="${false}"/>
					</td>
                  </tr>
                  <tr>
                    <kul:htmlAttributeHeaderCell attributeEntry="${camsLocationAttributes.buildingRoomNumber}" align="right" />
                    <td class="datacell">
						<kul:htmlControlAttribute attributeEntry="${camsLocationAttributes.buildingRoomNumber}" property="newPurchasingCapitalAssetLocationLine.buildingRoomNumber" readOnly="${false}"/>
					</td>
                    <kul:htmlAttributeHeaderCell attributeEntry="${camsLocationAttributes.capitalAssetPostalCode}" align="right" />
                    <td class="datacell">
						<kul:htmlControlAttribute attributeEntry="${camsLocationAttributes.capitalAssetPostalCode}" property="newPurchasingCapitalAssetLocationLine.capitalAssetPostalCode" readOnly="${false}"/>
					</td>
                  </tr>
                  <tr>
                    <td colspan="4" class="datacell" style="text-align:center;"> 
                        <html:image property="methodToCall.addCapitalAssetLocation.line${ctr}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" alt="Add a Asset Location" title="Add a Asset Location" styleClass="tinybutton" />
                     </td>
                  </tr>

				  <logic:iterate indexId="ctr2" name="KualiForm" property="document.purchasingCapitalAssetItems[${ctr}].purchasingCapitalAssetSystem.purchasingCapitalAssetLocations" id="location">
					
					
						<c:set var="currentTabIndex" value="${KualiForm.currentTabIndex}" scope="request" />
						<c:set var="topLevelTabIndex" value="${KualiForm.currentTabIndex}" scope="request" />
						
						<c:choose>
							    <c:when test="${location.objectId == null}">
							        <c:set var="newObjectId" value="<%= (new org.kuali.core.util.Guid()).toString()%>" />
					                     <c:set var="tabKey" value="Item-${newObjectId}" />
					                     <html:hidden property="document.item[${ctr}].objectId" value="${newObjectId}" />
						    </c:when>
						    <c:when test="${location.objectId != null}">
						        <c:set var="tabKey" value="Item-${location.objectId}" />
						        <html:hidden property="document.item[${ctr}].objectId" /> 
						    </c:when>
						</c:choose>
						
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
										
						<c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
							<tbody style="display: none;" id="tab-${tabKey}-div">
						</c:if>										
					
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
									<purap:camsLocation camsItemAttributes="${camsItemAttributes}" ctr="${ctr}" ctr2="${ctr2}" camsSystemAttributes="${camsSystemAttributes}" camsAssetAttributes="${camsAssetAttributes}" camsLocationAttributes="${camsLocationAttributes}" />
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
      </table>
      </td>
  </tr>
</table>

