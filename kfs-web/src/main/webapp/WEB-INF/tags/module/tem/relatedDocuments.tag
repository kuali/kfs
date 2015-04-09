<%--
   - The Kuali Financial System, a comprehensive financial management system for higher education.
   - 
   - Copyright 2005-2014 The Kuali Foundation
   - 
   - This program is free software: you can redistribute it and/or modify
   - it under the terms of the GNU Affero General Public License as
   - published by the Free Software Foundation, either version 3 of the
   - License, or (at your option) any later version.
   - 
   - This program is distributed in the hope that it will be useful,
   - but WITHOUT ANY WARRANTY; without even the implied warranty of
   - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   - GNU Affero General Public License for more details.
   - 
   - You should have received a copy of the GNU Affero General Public License
   - along with this program.  If not, see <http://www.gnu.org/licenses/>.
--%>
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>
<%@ taglib uri="/WEB-INF/tlds/temfunc.tld" prefix="temfunc"%>

<%@ attribute name="noNewRelatedDocument" required="false" description="make the New Related Document widget unavailable" %>

<c:set var="accountingDocumentRelationship" value="${DataDictionary.AccountingDocumentRelationship.attributes}" />

<noscript>
	<c:set var="noscript" value="true" />
</noscript>
<script>
	$(document).ready(function() {
		$("#relatedDocuments").accordion({
			autoHeight : false
		});
	});
</script>

<c:choose>
	<c:when test="${noscript}">
		<kul:tab tabTitle="View Related Documents" defaultOpen="false" tabErrorKey="${TemKeyConstants.TRVL_RELATED_DOCUMENT}">		
			<div id="relatedDocuments" class="tab-container" align="center">
				<c:if test="${KualiForm.document.versionNumber == null}">
					<div>Related document can only be added after this document has been saved.</div>
				</c:if>
				<c:if test="${KualiForm.document.versionNumber != null && !noNewRelatedDocument}">
					<h3>New Related Document</h3>
					<table cellpadding="0" class="datatable">
						<tbody>
							<tr>
								<th><div align="left"><kul:htmlAttributeLabel attributeEntry="${accountingDocumentRelationship.relDocumentNumber}" useShortLabel="true"/></div></th>
								<th><div align="left"><kul:htmlAttributeLabel attributeEntry="${accountingDocumentRelationship.description}" /></div></th>				
								<th><div align="center">Actions</div></th>
							</tr>
							<tr>
								<td>
									<div align="center">
										<kul:htmlControlAttribute
					                    attributeEntry="${accountingDocumentRelationship.relDocumentNumber}"
					                    property="newAccountingDocumentRelationship.relDocumentNumber"
					                    readOnly="false" />
				                    </div>
				                </td>
								<td valign=top class="infoline">
									<div align="center">
										<kul:htmlControlAttribute
					                    attributeEntry="${accountingDocumentRelationship.relDocumentNumber}"
					                    property="newAccountingDocumentRelationship.description"
					                    readOnly="false" />
				                    </div>
			                    </td>
				                <td valign=top class="infoline">
									<div align="center">
										<html:image src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif"
										styleClass="tinybutton"
										property="methodToCall.addRelatedDocumentLine"
										alt="Add Related Document Line" title="Add Related Document Line" />
									</div>
								</td>
							</tr>
						</tbody>
					</table>
				</c:if>
				<c:forEach items="${KualiForm.relatedDocuments}" var="relatedEntry">
					<h3 style="cursor: pointer;">
						<a style="color: #FFF;" href="#">${relatedEntry.key} (${fn:length(relatedEntry.value)})</a>
					</h3>
					<div>
						<c:if test="${fn:length(relatedEntry.value) < 1}">
							<b>No related ${relatedEntry.key} documents</b>
						</c:if>
						<c:forEach items="${relatedEntry.value}" var="view">
							<c:set var="documentTitle" value="${view.documentNumber} + ${view.documentTitle}" />
							<c:set var="tabKey" value="${kfunc:generateTabKey(documentTitle)}" />
							<c:set var="currentTab" value="${kfunc:getTabState(KualiForm, tabKey)}" />

							<c:choose>
								<c:when test="${empty currentTab}">
									<c:set var="isOpen" value="false" />	
								</c:when>
								<c:when test="${!empty currentTab}">
									<c:set var="isOpen" value="${currentTab == 'OPEN'}" />
								</c:when>
							</c:choose>			    
							<html:hidden property="tabStates(${tabKey})" value="${(isOpen ? 'OPEN' : 'CLOSE')}" />
							<h3 style="background-color: #C3C3C3; ">
								<a href="${ConfigProperties.rice.server.url}/kew/DocHandler.do?command=displayDocSearchView&docId=${view.documentNumber}" target="_BLANK">${view.documentNumber} - ${view.documentTitle}</a>
								<c:if test="${!empty KualiForm.relatedDocumentNotes[view.documentNumber]}">
									<c:if test="${isOpen == 'true' || isOpen == 'TRUE'}">
										<html:image property="methodToCall.toggleTab.tab${tabKey}" 
											src="${ConfigProperties.kr.externalizable.images.url}tinybutton-hide.gif" 
											alt="hide" title="toggle" styleClass="tinybutton" styleId="tab-${tabKey}-imageToggle"
											onclick="javascript: return toggleTab(document, '${tabKey}'); " tabindex="-1" />
									</c:if>
									<c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
										<html:image property="methodToCall.toggleTab.tab${tabKey}" 
											src="${ConfigProperties.kr.externalizable.images.url}tinybutton-show.gif" 
											alt="show" title="toggle" styleClass="tinybutton" styleId="tab-${tabKey}-imageToggle"
											onclick="javascript: return toggleTab(document, '${tabKey}'); " tabindex="-1" />
									</c:if>
								</c:if>
								<c:if test="${temfunc:canDeleteDocumentRelationship(KualiForm.document.documentNumber, view.documentNumber)}">
									<div style="float:right"><html:image property="methodToCall.deleteRelatedDocumentLine.${view.documentNumber}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" title="Delete a Related Document" alt="Delete a Related Document" styleClass="tinybutton"/></div>
								</c:if>			
							</h3>							
							<c:choose>
								<c:when test="${isOpen == 'true' || isOpen == 'TRUE'}">
									<div style="display: block;" id="tab-${tabKey}-div">
								</c:when>
								<c:otherwise>
									<div style="display: none;" id="tab-${tabKey}-div">
								</c:otherwise>
							</c:choose>
								<table cellpadding="0" cellspacing="0" class="datatable" summary="Notes">
									<c:if test="${!empty KualiForm.relatedDocumentNotes[view.documentNumber]}">
										<tr>
											<kul:htmlAttributeHeaderCell scope="col" width="15%">Date</kul:htmlAttributeHeaderCell>
											<kul:htmlAttributeHeaderCell scope="col" width="15%">User</kul:htmlAttributeHeaderCell>
											<kul:htmlAttributeHeaderCell scope="col" width="70%">Note</kul:htmlAttributeHeaderCell>
										</tr>
										<c:forEach items="${KualiForm.relatedDocumentNotes[view.documentNumber]}" var="note">
											<tr>
												<td align="center" valign="middle" class="datacell"><c:out value="${note.notePostedTimestamp}" /></td>
												<td align="center" valign="middle" class="datacell"><c:out value="${note.authorUniversal.name}" /></td>
												<td align="left" valign="middle" class="datacell"><c:out value="${note.noteText}" /></td>
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
					    	<b>&nbsp;</b>							
						</c:forEach>
					</div>
				</c:forEach>
			</div>			
		</kul:tab>
	</c:when>
	<c:otherwise>
		<tem-noscript:relatedDocuments />
	</c:otherwise>
</c:choose>
