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

<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>
<%@ attribute name="documentAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="groupList" required="true" %>
<%@ attribute name="limitByPoId" required="true" %>

<c:set var="isRequisition" value="${KualiForm.document.isReqsDoc}" />

<logic:notEmpty name="KualiForm" property="${groupList}">	   		
	<logic:iterate id="group" name="KualiForm" property="${groupList}" indexId="groupCtr">
		<c:set var="documentTitle" value="${group.lineItemView.documentLabel}${group.lineItemView.documentIdentifierString}"/>
			<c:set var="tabKey" value="${kfunc:generateTabKey(documentTitle)}" />
			<c:set var="currentTab" value="${kfunc:getTabState(KualiForm, tabKey)}" />
			<%-- default to close --%>
			<c:choose>
				<c:when test="${empty currentTab}">
					<c:set var="isOpen" value="false" />
					<html:hidden property="tabStates(${tabKey})" value="CLOSE" />		
				</c:when>
				<c:when test="${!empty currentTab}">
					<c:set var="isOpen" value="${currentTab == 'OPEN'}" />
				</c:when>
			</c:choose>
		<!--  Line Item Receiving View -->
	    <c:choose>
		<c:when test="${group.isLineItemViewCurrentDocument}">
			
    	   	<h3><c:out value="${group.lineItemView.documentLabel}"/> 
			<c:if test="${isOpen == 'true' || isOpen == 'TRUE'}">
				<html:image property="methodToCall.toggleTab.tab${tabKey}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-hide.gif" alt="hide" title="toggle" styleClass="tinybutton" styleId="tab-${tabKey}-imageToggle"
							onclick="javascript: return toggleTab(document, '${tabKey}'); " />
			</c:if>
			<c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
				<html:image property="methodToCall.toggleTab.tab${tabKey}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-show.gif" alt="show" title="toggle" styleClass="tinybutton" styleId="tab-${tabKey}-imageToggle"
							onclick="javascript: return toggleTab(document, '${tabKey}'); " />
			</c:if>
			</h3>
		</c:when>
		<c:when test="${(empty limitByPoId) or (limitByPoId eq group.lineItemView.purchaseOrderIdentifier)}">
			<c:choose>
			<c:when test="${isRequisition}">
		    	<h3>${group.lineItemView.documentLabel} - <a href="<c:out value="${group.lineItemView.url}" />" style="color: #FFF" target="_BLANK"><c:out value="${group.lineItemView.documentIdentifierString}" /></a>
		    		&nbsp;(Purchase Order - ${group.lineItemView.purchaseOrderIdentifier})
		    </c:when>			
			<c:otherwise>
				<h3>${group.lineItemView.documentLabel} - <a href="<c:out value="${group.lineItemView.url}" />" style="color: #FFF" target="_BLANK"><c:out value="${group.lineItemView.documentIdentifierString}" /></a>
			</c:otherwise>
			</c:choose>
			<c:if test="${isOpen == 'true' || isOpen == 'TRUE'}">
				<html:image property="methodToCall.toggleTab.tab${tabKey}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-hide.gif" alt="hide" title="toggle" styleClass="tinybutton" styleId="tab-${tabKey}-imageToggle"
							onclick="javascript: return toggleTab(document, '${tabKey}'); " />
			</c:if>
			<c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
				<html:image property="methodToCall.toggleTab.tab${tabKey}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-show.gif" alt="show" title="toggle" styleClass="tinybutton" styleId="tab-${tabKey}-imageToggle"
							onclick="javascript: return toggleTab(document, '${tabKey}'); " />
			</c:if>
			</h3>

			<c:if test="${isOpen == 'true' || isOpen == 'TRUE'}">
			<div style="display: block;" id="tab-${tabKey}-div">
			</c:if>
			<c:if test="${isOpen != 'true' && isOpen != 'TRUE'}" >
			<div style="display: none;" id="tab-${tabKey}-div">
			</c:if>
			
			<!--  Line Item Receiving View notes -->
			<table cellpadding="0" cellspacing="0" class="datatable" summary="Notes">
				<c:choose>
		    	<c:when test="${!empty group.lineItemView.notes}">
					<tr>
						<kul:htmlAttributeHeaderCell scope="col" width="15%">Date</kul:htmlAttributeHeaderCell>
						<kul:htmlAttributeHeaderCell scope="col" width="15%">User</kul:htmlAttributeHeaderCell>
						<kul:htmlAttributeHeaderCell scope="col" width="70%">Note</kul:htmlAttributeHeaderCell>
		       		</tr>
					<c:forEach items="${group.lineItemView.notes}" var="note" >
		       		<tr>
		       			<td align="center" valign="middle" class="datacell">
		       				<c:out value="${note.notePostedTimestamp}" />
			       		</td>
			       		<td align="center" valign="middle" class="datacell">
		       				<c:out value="${note.authorUniversal.name}" />
			       		</td>
			       		<td align="left" valign="middle" class="datacell">
		       				<c:out value="${note.noteText}" />
			       		</td>
			       	</tr>
					</c:forEach>
				</c:when>	
		    	<c:otherwise>
					<tr>
			    		<th align="center" valign="middle" class="bord-l-b">No Notes</th>
			    	</tr>
				</c:otherwise>	
				</c:choose>
		    </table>
			</div>	
		</c:when>
		</c:choose>
		<c:if test="${isOpen == 'true' || isOpen == 'TRUE'}">
			<div style="display: block;" id="tab-${tabKey}-div">
			</c:if>
			<c:if test="${isOpen != 'true' && isOpen != 'TRUE'}" >
			<div style="display: none;" id="tab-${tabKey}-div">
		</c:if>
		<!--  The associated Correction Receiving Views indented -->
		<c:if test="${(empty limitByPoId) or (limitByPoId eq group.lineItemView.purchaseOrderIdentifier)}">
			<c:forEach items="${group.correctionViews}" var="correctionView" varStatus="viewCtr">
				<c:set var="documentTitle" value="${correctionView.documentLabel}${correctionView.documentIdentifierString}"/>
				<c:set var="tabKey" value="${kfunc:generateTabKey(documentTitle)}" />
				<c:set var="currentTab" value="${kfunc:getTabState(KualiForm, tabKey)}" />
				<%-- default to close --%>
				<c:choose>
					<c:when test="${empty currentTab}">
						<c:set var="isOpen" value="false" />
						<html:hidden property="tabStates(${tabKey})" value="CLOSE" />		
					</c:when>
					<c:when test="${!empty currentTab}">
						<c:set var="isOpen" value="${currentTab == 'OPEN'}" />
					</c:when>
				</c:choose>
					
				<h3>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${correctionView.documentLabel} - <a href="<c:out value="${correctionView.url}" />" style="color: #FFF" target="_BLANK"><c:out value="${correctionView.documentIdentifierString}" /></a>
				<c:if test="${isOpen == 'true' || isOpen == 'TRUE'}">
					<html:image property="methodToCall.toggleTab.tab${tabKey}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-hide.gif" alt="hide" title="toggle" styleClass="tinybutton" styleId="tab-${tabKey}-imageToggle"
								onclick="javascript: return toggleTab(document, '${tabKey}'); " />
				</c:if>
				<c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
					<html:image property="methodToCall.toggleTab.tab${tabKey}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-show.gif" alt="show" title="toggle" styleClass="tinybutton" styleId="tab-${tabKey}-imageToggle"
								onclick="javascript: return toggleTab(document, '${tabKey}'); " />
				</c:if>				
				</h3>
				<c:if test="${isOpen == 'true' || isOpen == 'TRUE'}">
					<div style="display: block;" id="tab-${tabKey}-div">
				</c:if>
				<c:if test="${isOpen != 'true' && isOpen != 'TRUE'}" >
					<div style="display: none;" id="tab-${tabKey}-div">
				</c:if>
				
				<!--  Correction Receiving View notes -->
				<table cellpadding="0" cellspacing="0" class="datatable" summary="Notes">
					<c:choose>
		    		<c:when test="${!empty correctionView.notes}">
						<tr>
							<kul:htmlAttributeHeaderCell scope="col" width="15%">Date</kul:htmlAttributeHeaderCell>
							<kul:htmlAttributeHeaderCell scope="col" width="15%">User</kul:htmlAttributeHeaderCell>
							<kul:htmlAttributeHeaderCell scope="col" width="70%">Note</kul:htmlAttributeHeaderCell>
		       			</tr>
						<c:forEach items="${correctionView.notes}" var="note" >
		       			<tr>
		       				<td align="center" valign="middle" class="datacell">
		       					<c:out value="${note.notePostedTimestamp}" />
			       			</td>
			       			<td align="center" valign="middle" class="datacell">
		       					<c:out value="${note.authorUniversal.name}" />
			       			</td>
			       			<td align="left" valign="middle" class="datacell">
		       					<c:out value="${note.noteText}" />
			      	 		</td>
			      	 	</tr>
						</c:forEach>
					</c:when>	
		    		<c:otherwise>
						<tr>
			    			<th align="center" valign="middle" class="bord-l-b">No Notes</th>
			    		</tr>
					</c:otherwise>	
					</c:choose>
		    	</table>
			</div>					
			</c:forEach>
			
			<c:set var="viewShown" value="true"/>
		</c:if>
		</div>

	</logic:iterate>

    <c:if test="${viewShown}">
		<br />
	   	<br />
	</c:if>
</logic:notEmpty>

