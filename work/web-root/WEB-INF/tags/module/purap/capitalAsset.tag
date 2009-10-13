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

<%@ attribute name="documentAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for this row's fields." %>

<%@ attribute name="displayRequisitionFields" required="false"
              description="Boolean to indicate if REQ specific fields should be displayed" %>

<%@ attribute name="displayPurchaseOrderFields" required="false"
              description="Boolean to indicate if PO specific fields should be displayed" %>

<c:set var="fullEntryMode" value="${KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT] && (empty KualiForm.editingMode['restrictFiscalEntry'])}" />
<c:set var="systemSelected" value="${KualiForm.document.capitalAssetSystemTypeCode}"/>

<kul:tab tabTitle="Capital Asset" defaultOpen="false" tabErrorKey="${PurapConstants.CAPITAL_ASSET_TAB_ERRORS}">
    <div class="tab-container" align=center>
        <table cellpadding="0" cellspacing="0" class="datatable" summary="Choose System Type Section">
        	<tr>
                <td colspan="2" class="subhead">System Type Choice</td>
            </tr>
         	<tr>
        		<th colspan="2" align=left valign=middle class="bord-l-b">
        			<div align="left" class="annotate">
        				<c:out value="${PurapConstants.CapitalAssetTabStrings.SYSTEM_DEFINITION}"/>
        			</div>
        		</td>
			</tr>
			<tr>
	        	<c:choose>
	        		<c:when test="${systemSelected eq PurapConstants.CapitalAssetTabStrings.INDIVIDUAL_ASSETS}">
	        			<th align=right valign=middle class="bord-l-b" width="50%">
	        				<c:out value="${PurapConstants.CapitalAssetTabStrings.INDIVIDUAL_ASSETS_DESC}"/>
	        			</th>
	        			<td align=left valign=middle class="datacell" width="50%">
	        				(selected)
	        			</td>
	        		</c:when>
	        		<c:otherwise>
			    		<th style="font-weight:normal" align="right" valign="middle" width="50%">
	        					<c:out value="${PurapConstants.CapitalAssetTabStrings.INDIVIDUAL_ASSETS_DESC}"/>
	        			</th>
	        			<td align=left valign=middle class="datacell" width="50%">
	        				<html:image property="methodToCall.selectSystemType.${PurapConstants.CapitalAssetTabStrings.INDIVIDUAL_ASSETS}" src="${ConfigProperties.externalizable.images.url}tinybutton-select.gif" alt="Select Individual Assets" title="Select Individual Assets" styleClass="tinybutton" />
	        			</td>
	        		</c:otherwise>
	        	</c:choose>
			</tr>
			<tr>
				<c:choose>
					<c:when test="${systemSelected eq PurapConstants.CapitalAssetTabStrings.ONE_SYSTEM}">
						<th align=right valign=middle class="bord-l-b" width="50%">
			        		<c:out value="${PurapConstants.CapitalAssetTabStrings.ONE_SYSTEM_DESC}"/>
			    		</th>
			    		<td align=left valign=middle class="datacell" width="50%">
			    			(selected)
			    		</td>
			    	</c:when>
			    	<c:otherwise>
			    		<th style="font-weight:normal" align="right" valign="middle" width="50%">
	        					<c:out value="${PurapConstants.CapitalAssetTabStrings.ONE_SYSTEM_DESC}"/>
	        			</th>
	        			<td align=left valign=middle class="datacell" width="50%">
	        				<html:image property="methodToCall.selectSystemType.${PurapConstants.CapitalAssetTabStrings.ONE_SYSTEM}" src="${ConfigProperties.externalizable.images.url}tinybutton-select.gif" alt="Select One System" title="Select One System" styleClass="tinybutton"/>
	        			</td>
			    	</c:otherwise>
			    </c:choose>

			</tr>
			<tr>
				<c:choose>
					<c:when test="${systemSelected eq PurapConstants.CapitalAssetTabStrings.MULTIPLE_SYSTEMS}">
						<th align=right valign=middle class="bord-l-b" width="50%">	        			
			        		<c:out value="${PurapConstants.CapitalAssetTabStrings.MULTIPLE_SYSTEMS_DESC}"/>
			    		</th>
			    		<td align=left valign=middle class="datacell" width="50%">
			    			(selected)
			    		</td>
			    	</c:when>
			    	<c:otherwise>
			    		<th style="font-weight:normal" align="right" valign="middle" width="50%">
	        				<c:out value="${PurapConstants.CapitalAssetTabStrings.MULTIPLE_SYSTEMS_DESC}"/>
	        			</th>
	        			<td align=left valign=middle class="datacell" width="50%">
	        				<html:image property="methodToCall.selectSystemType.${PurapConstants.CapitalAssetTabStrings.MULTIPLE_SYSTEMS}" src="${ConfigProperties.externalizable.images.url}tinybutton-select.gif" alt="Select Multiple Systems" title="Select Multiple Systems" styleClass="tinybutton"/>
	        			</td>
			    	</c:otherwise>
			    </c:choose>
			</tr>
			<tr>
                <td colspan="2" class="subhead">Asset Information</td>
            </tr>
            <tr>
            	<c:choose>           		
            		<c:when test="${systemSelected eq PurapConstants.CapitalAssetTabStrings.INDIVIDUAL_ASSETS}">
            			<tr>
            				<th colspan="2" align=center valign=middle class="bord-l-b">
            					<c:out value="${PurapConstants.CapitalAssetTabStrings.ASSET_DATA}"/>
            				</th>
            			</tr>
            		</c:when>
            		<c:otherwise>
            			<tr>       
			            	<th align=right valign=middle class="bord-l-b">
			            		<div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.systemDescription}" /></div>
			            	</th>
			            	<td align=left valign=middle class="datacell">
			            		<kul:htmlControlAttribute attributeEntry="${documentAttributes.systemDescription}" property="document.systemDescription" readOnly="${not (fullEntryMode)}" />
			            	</td>			            
            				<c:if test="${systemSelected eq PurapConstants.CapitalAssetTabStrings.ONE_SYSTEM}">
								<td>
	            					<!-- Here is where the hook to the Blue Box goes. -->
	            				</td>
	            			</c:if>
	            		</tr>			            			
            		</c:otherwise>
            	</c:choose>
            </tr>      
      	</table>   
	</div>
</kul:tab>
