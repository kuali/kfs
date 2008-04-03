<%--
 Copyright 2006-2007 The Kuali Foundation.
 
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

<%@ attribute name="documentAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for this row's fields." %>

<%@ attribute name="displayRequisitionFields" required="false"
              description="Boolean to indicate if REQ specific fields should be displayed" %>

<%@ attribute name="displayPurchaseOrderFields" required="false"
              description="Boolean to indicate if PO specific fields should be displayed" %>

<c:set var="systemSelected" value="ONE"/>

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
					<c:when test="${not empty systemSelected}">
						<th colspan="2" align=center valign=middle class="bord-l-b">
							<c:choose>
								<c:when test="${systemSelected eq PurapConstants.CapitalAssetTabStrings.INDIVIDUAL_ASSETS}">	        			
			        				<c:out value="${PurapConstants.CapitalAssetTabStrings.INDIVIDUAL_ASSETS_DESC}"/>
			        			</c:when>
								<c:when test="${systemSelected eq PurapConstants.CapitalAssetTabStrings.ONE_SYSTEM}">
			        				<c:out value="${PurapConstants.CapitalAssetTabStrings.ONE_SYSTEM_DESC}"/>
			        			</c:when>
			        			<c:when test="${systemSelected eq PurapConstants.CapitalAssetTabStrings.MULTIPLE_SYSTEMS}">
			        				<c:out value="${PurapConstants.CapitalAssetTabStrings.MULTIPLE_SYSTEMS_DESC}"/>
			        			</c:when>
			        		</c:choose>
			        	</th>
			        </c:when>	        		
	        		<c:otherwise>
	        			<th align=right valign=middle class="bord-l-b" width="50%">
	        				<c:out value="${PurapConstants.CapitalAssetTabStrings.INDIVIDUAL_ASSETS_DESC}"/>
	        			</th>
	        			<td align=left valign=middle class="datacell" width="50%">
	        				<input type="image" name="methodToCall.selectSystemType" src="${ConfigProperties.externalizable.images.url}tinybutton-select.gif" class="tinybutton" title="Select System Type" alt="Select System Type">
	        			</td>
	        		</c:otherwise>
	        	</c:choose>
			</tr>
			<tr>
				<th align=right valign=middle class="bord-l-b" width="50%">
					<c:choose>
						<c:when test="${systemSelected eq PurapConstants.CapitalAssetTabStrings.ONE_SYSTEM}">							
							<c:out value="${PurapConstants.CapitalAssetTabStrings.INDIVIDUAL_ASSETS_DESC}"/>	        						        		
			        	</c:when>
						<c:otherwise>
			        		<c:out value="${PurapConstants.CapitalAssetTabStrings.ONE_SYSTEM_DESC}"/>
			        	</c:otherwise>
			        </c:choose>
				</th>
				<td align=left valign=middle class="datacell" width="50%">
	        		<input type="image" name="methodToCall.selectSystemType" src="${ConfigProperties.externalizable.images.url}tinybutton-select.gif" class="tinybutton" title="Select System Type" alt="Select System Type">
	        	</td>
			</tr>
			<tr>
				<th align=right valign=middle class="bord-l-b" width="50%">
					<c:choose>
						<c:when test="${systemSelected eq PurapConstants.CapitalAssetTabStrings.MULTIPLE_SYSTEMS}">
			        		<c:out value="${PurapConstants.CapitalAssetTabStrings.ONE_SYSTEM_DESC}"/>
			        	</c:when>
						<c:otherwise>	        			
			        		<c:out value="${PurapConstants.CapitalAssetTabStrings.MULTIPLE_SYSTEMS_DESC}"/>
			        	</c:otherwise>
	        		</c:choose>
				</th>
				<td align=left valign=middle class="datacell" width="50%">
	        		<input type="image" name="methodToCall.selectSystemType" src="${ConfigProperties.externalizable.images.url}tinybutton-select.gif" class="tinybutton" title="Select System Type" alt="Select System Type">
	        	</td>			
			</tr>
			<tr>
                <td colspan="2" class="subhead">Asset Information</td>
            </tr>
            <tr>
            	<c:choose>
            		<c:when test="${systemSelected eq PurapConstants.CapitalAssetTabStrings.INDIVIDUAL_ASSETS}">
            			<c:out value="${PurapConstants.CapitalAssetTabStrings.ASSET_DATA}"/>
            		</c:when>
            		<c:otherwise>
           				<tr>
			            	<th align=right valign=middle class="bord-l-b">
			            		<div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.systemDescription}" /></div>
			            	</th>
			            	<td align=left valign=middle class="datacell">
			            		<kul:htmlControlAttribute attributeEntry="${documentAttributes.systemDescription}" property="document.systemDescription" readOnly="${not (fullEntryMode)}" />
			            	</td>
			            </tr>
            			<c:if test="${systemSelected eq PurapConstants.CapitalAssetTabStrings.ONE_SYSTEM}">
							<tr>
	            				<!-- Here is where the hook to the Blue Box goes. -->
	            			</tr>
	            		</c:if>
            		</c:otherwise>
            	</c:choose>
            </tr>      
      	</table>   
	</div>
</kul:tab>