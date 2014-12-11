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

<%@ attribute name="readOnly" required="true"
	description="If document is in read only mode"%>
	
<c:set var="ContractsGrantsCollectionActivityAttributes"
	value="${DataDictionary.ContractsGrantsCollectionActivityDocument.attributes}"/>

<kul:tab tabTitle="Global Collection Event" defaultOpen="true"
	tabErrorKey="document.activityCode,document.activityDate,document.activityText,document.followupDate,document.completedDate">
	<div class="tab-container" align="center">
		<h3>New</h3>
		<table width="100%" cellpadding="0" cellspacing="0" class="datatable">
			<tr>
				<kul:htmlAttributeHeaderCell width="50%"
					attributeEntry="${ContractsGrantsCollectionActivityAttributes.activityCode}" horizontal="true" useShortLabel="false"/>
				<td>
					<div id="document.activityCode.div">
						<c:choose>
							<c:when test="${not empty KualiForm.document.proposalNumber}">
								<kul:htmlControlAttribute attributeEntry="${ContractsGrantsCollectionActivityAttributes.activityCode}"
									property="document.activityCode" readOnly="${readOnly}" />
								<c:if test="${not readOnly}">
									&nbsp;
									<kul:lookup boClassName="org.kuali.kfs.module.ar.businessobject.CollectionActivityType" fieldConversions="activityCode:document.activityCode" />
								</c:if>
							</c:when>
							<c:otherwise>
								&nbsp;
							</c:otherwise>
						</c:choose>
					</div>
				</td>
			</tr>
			<tr>
				<kul:htmlAttributeHeaderCell width="50%"
					attributeEntry="${ContractsGrantsCollectionActivityAttributes.activityDate}" horizontal="true" useShortLabel="false"/>
				<td>
					<div id="document.activityDate.div">
						<c:choose>
							<c:when test="${not empty KualiForm.document.proposalNumber}">
								<kul:htmlControlAttribute attributeEntry="${ContractsGrantsCollectionActivityAttributes.activityDate}"
									property="document.activityDate" readOnly="${readOnly}" />				
							</c:when>
							<c:otherwise>
								&nbsp;
							</c:otherwise>
						</c:choose>
					</div>
				</td>
			</tr>
			<tr>
				<kul:htmlAttributeHeaderCell width="50%"
					attributeEntry="${ContractsGrantsCollectionActivityAttributes.activityText}" horizontal="true" useShortLabel="false"/>
				<td>
					<div id="document.activityText.div">
						<c:choose>
							<c:when test="${not empty KualiForm.document.proposalNumber}">
								<kul:htmlControlAttribute attributeEntry="${ContractsGrantsCollectionActivityAttributes.activityText}"
									property="document.activityText" readOnly="${readOnly}" expandedTextArea="true" />						
							</c:when>
							<c:otherwise>
								&nbsp;
							</c:otherwise>
						</c:choose>
					</div>
				</td>
			</tr>
			<tr>
				<kul:htmlAttributeHeaderCell width="50%"
					attributeEntry="${ContractsGrantsCollectionActivityAttributes.followupDate}" horizontal="true" useShortLabel="false"/>
				<td>
					<div id="document.followupDate.div">
						<c:choose>
							<c:when test="${not empty KualiForm.document.proposalNumber}">
								<kul:htmlControlAttribute attributeEntry="${ContractsGrantsCollectionActivityAttributes.followupDate}"
									property="document.followupDate" readOnly="${readOnly}" />						
							</c:when>
							<c:otherwise>
								&nbsp;
							</c:otherwise>
						</c:choose>
					</div>
				</td>
			</tr>
			<tr>
				<kul:htmlAttributeHeaderCell width="50%"
					attributeEntry="${ContractsGrantsCollectionActivityAttributes.completedDate}" horizontal="true" useShortLabel="false"/>
				<td>
					<div id="document.completedDate.div">
						<c:choose>
							<c:when test="${not empty KualiForm.document.proposalNumber}">
								<kul:htmlControlAttribute attributeEntry="${ContractsGrantsCollectionActivityAttributes.completedDate}"
									property="document.completedDate" readOnly="${readOnly}" />						
							</c:when>
							<c:otherwise>
								&nbsp;
							</c:otherwise>
						</c:choose>
					</div>
				</td>
			</tr>
		</table>
	</div>
</kul:tab>