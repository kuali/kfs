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
<kul:tab tabTitle="View Related Documents" defaultOpen="false">
   <div id="relatedDocuments" class="tab-container" align="center" > 
    	<c:forEach items="${KualiForm.relatedDocuments}"
                   var="relatedEntry">
          <h3 style="cursor: pointer;"><a href="#">${relatedEntry.key}</a></h3>
              <h4><a href="kew/DocHandler.do?command=displayDocSearchView&docId=${view.documentNumber}"
                style="color: #FFF"
                target="_BLANK"><c:out value="${view.documentNumber}"
                /></a></h4>
			<c:forEach items="${relatedEntry.value}" var="view">
				<c:set var="documentTitle" value="${relatedEntry.key} + ${view.documentTitle}"/>
				<c:set var="tabKey" value="${kfunc:generateTabKey(documentTitle)}" />
				<c:set var="currentTab"
				value="${kfunc:getTabState(KualiForm, tabKey)}"
				/>				    
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
		
				<h3>${relatedEntry.key} - <a href="kew/DocHandler.do?command=displayDocSearchView&docId=${view.documentNumber}" style="color: #FFF" target="_BLANK"><c:out value="${view.documentNumber}" /></a>	    
				<c:if test="${isOpen == 'true' || isOpen == 'TRUE'}">
				<html:image property="methodToCall.toggleTab.tab${tabKey}" 
					src="${ConfigProperties.kr.externalizable.images.url}tinybutton-hide.gif" 
					alt="hide" title="toggle" styleClass="tinybutton" styleId="tab-${tabKey}-imageToggle"
					onclick="javascript: return toggleTab(document, '${tabKey}'); " />
				</c:if>
				<c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
				<html:image property="methodToCall.toggleTab.tab${tabKey}" 
					src="${ConfigProperties.kr.externalizable.images.url}tinybutton-show.gif" 
					alt="show" title="toggle" styleClass="tinybutton" styleId="tab-${tabKey}-imageToggle"
					onclick="javascript: return toggleTab(document, '${tabKey}'); " />
				</c:if>
				</h3>
	            
	            <c:if test="${isOpen == 'true' || isOpen == 'TRUE'}">
					<div style="display: block;" id="tab-${tabKey}-div">
				</c:if>
				<c:if test="${isOpen != 'true' && isOpen != 'TRUE'}" >
					<div style="display: none;" id="tab-${tabKey}-div">
				</c:if>
	            
	            <table cellpadding="0" cellspacing="0" class="datatable" summary="Notes">
			    	<c:if test="${!empty KualiForm.relatedDocumentNotes[view.documentNumber]}">
						<tr>
							<kul:htmlAttributeHeaderCell scope="col" width="15%">Date</kul:htmlAttributeHeaderCell>
							<kul:htmlAttributeHeaderCell scope="col" width="15%">User</kul:htmlAttributeHeaderCell>
							<kul:htmlAttributeHeaderCell scope="col" width="70%">Note</kul:htmlAttributeHeaderCell>
			        	</tr>
						<c:forEach items="${KualiForm.relatedDocumentNotes[view.documentNumber]}" var="note" >
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
					</c:if>	
			    	<c:if test="${empty KualiForm.relatedDocumentNotes[view.documentNumber]}">
				        <tr>
				            <th align="center" valign="middle" class="bord-l-b">No Notes</th>
				        </tr>
					</c:if>	
		    	</table>
				</div>
	        </c:forEach>
		</c:forEach>
    </div>
</kul:tab>
