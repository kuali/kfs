<%--
 Copyright 2006 The Kuali Foundation.
 
 $Source$
 
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

<%@ include file="/jsp/core/tldHeader.jsp"%>

<c:set var="routingFormResearchRiskAttributes" value="${DataDictionary.RoutingFormResearchRisk.attributes}" />

<c:forEach items="${KualiForm.routingFormDocument.routingFormDocumentResearchRisks}" var="researchRisk" varStatus="researchRiskStatus">
	<html:hidden property="document.routingFormDocumentResearchRisk[${researchRiskStatus.index}].researchDocumentNumber"/>
	<html:hidden property="document.routingFormDocumentResearchRisk[${researchRiskStatus.index}].researchRiskTypeCode"/>
	<html:hidden property="document.routingFormDocumentResearchRisk[${researchRiskStatus.index}].objectId"/>
	<html:hidden property="document.routingFormDocumentResearchRisk[${researchRiskStatus.index}].versionNumber"/>
	<html:hidden property="document.routingFormDocumentResearchRisk[${researchRiskStatus.index}].researchRiskType.researchRiskTypeCode"/>
	<html:hidden property="document.routingFormDocumentResearchRisk[${researchRiskStatus.index}].researchRiskType.researchRiskTypeDescription"/>
	<html:hidden property="document.routingFormDocumentResearchRisk[${researchRiskStatus.index}].researchRiskType.controlAttributeTypeCode"/>

	<c:if test="${researchRisk.researchRiskType.controlAttributeTypeCode == 'S'}">
		<kul:tab tabTitle="${researchRisk.researchRiskType.researchRiskTypeDescription}" defaultOpen="false">
			<div class="tab-container" id="G02" style="" align="center">
            	<div class="h2-container"><h2>${researchRisk.researchRiskType.researchRiskTypeDescription}</h2></div>
            	<table cellpadding="0" cellspacing="0" summary="">
              		<tr>
                		<td colspan=7 class="tab-subhead"><span class="left">Insert Study </span> </td>
              		</tr>
              		<tr>
                		<th>&nbsp;</th>                
                		<th> 
                			<div align="center">
								<kul:htmlAttributeLabel attributeEntry="${DataDictionary.RoutingFormResearchRisk.attributes.researchRiskApprovalPendingIndicator}" useShortLabel="false" skipHelpUrl="true" noColon="true" />
							</div>
						</th>
                		<th>
                			<div align="center">
								<kul:htmlAttributeLabel attributeEntry="${DataDictionary.RoutingFormResearchRisk.attributes.researchRiskStudyNumber}" useShortLabel="false" skipHelpUrl="true" noColon="true" />
                			</div>
                		</th>
                		<th>
                			<div align="center">
								<kul:htmlAttributeLabel attributeEntry="${DataDictionary.RoutingFormResearchRisk.attributes.researchRiskStudyApprovalDate}" useShortLabel="false" skipHelpUrl="true" noColon="true" />
							</div>
						</th>
                		<th>
                			<div align="center">
								<kul:htmlAttributeLabel attributeEntry="${DataDictionary.RoutingFormResearchRisk.attributes.researchRiskStudyReviewCode}" useShortLabel="false" skipHelpUrl="true" noColon="true" />                
                			</div>
                		</th>
                		<th>
                			<div align="center">
								<kul:htmlAttributeLabel attributeEntry="${DataDictionary.RoutingFormResearchRisk.attributes.researchRiskExemptionNumber}" useShortLabel="false" skipHelpUrl="true" noColon="true" />                
							</div>
						</th>
                		<th>Action</th>
              		</tr>
              		<tr>
                		<th scope="row">add:</th>
                		<td class="infoline">
                			<div align="center">
                				<kul:htmlControlAttribute property="document.routingFormDocumentResearchRisk[${researchRiskStatus.index}].newResearchRiskStudy.researchRiskApprovalPendingIndicator" attributeEntry="${routingFormResearchRiskAttributes.researchRiskApprovalPendingIndicator}" />
                			</div>
                		</td>
                		<td class="infoline">
                			<div align="center">
                    			<kul:htmlControlAttribute property="document.routingFormDocumentResearchRisk[${researchRiskStatus.index}].newResearchRiskStudy.researchRiskStudyNumber" attributeEntry="${routingFormResearchRiskAttributes.researchRiskStudyNumber}" />
                			</div>
                		</td>
                		<td class="infoline">
                			<div align="center">
                    			<kul:htmlControlAttribute property="document.routingFormDocumentResearchRisk[${researchRiskStatus.index}].newResearchRiskStudy.researchRiskStudyApprovalDate" attributeEntry="${routingFormResearchRiskAttributes.researchRiskStudyApprovalDate}" datePicker="true" />
                			</div>
                		</td>
                		<td class="infoline">
                			<div align="center">
                    			<kul:htmlControlAttribute property="document.routingFormDocumentResearchRisk[${researchRiskStatus.index}].newResearchRiskStudy.researchRiskStudyReviewCode" attributeEntry="${routingFormResearchRiskAttributes.researchRiskStudyReviewCode}" />
                			</div>
                		</td>
                		<td class="infoline">
                			<div align="center">
                    			<kul:htmlControlAttribute property="document.routingFormDocumentResearchRisk[${researchRiskStatus.index}].newResearchRiskStudy.researchRiskExemptionNumber" attributeEntry="${routingFormResearchRiskAttributes.researchRiskExemptionNumber}" />                
                			</div>
                		</td>
                		<td class="infoline"><div align=center><html:image property="methodToCall.insertRoutingFormResearchRiskStudy.anchor${currentTabIndex}.line${researchRiskStatus.index}" styleClass="tinybutton" src="images/tinybutton-add1.gif" alt="add research risk"/></div></td>
              		</tr>
              		
              		<c:forEach items="${researchRisk.researchRiskStudies}" var="study" varStatus="studyStatus">
				 		<tr>
                			<th scope="row">${studyStatus.index + 1}</th>
                			<td class="infoline">
                				<div align="center">
	            					<kul:htmlControlAttribute property="document.routingFormDocumentResearchRisk[${researchRiskStatus.index}].researchRiskStudy[${studyStatus.index}].researchRiskApprovalPendingIndicator" attributeEntry="${routingFormResearchRiskAttributes.researchRiskApprovalPendingIndicator}" />
                				</div>
                			</td>
                			<td class="infoline">
                				<div align="center">
                    				<kul:htmlControlAttribute property="document.routingFormDocumentResearchRisk[${researchRiskStatus.index}].researchRiskStudy[${studyStatus.index}].researchRiskStudyNumber" attributeEntry="${routingFormResearchRiskAttributes.researchRiskStudyNumber}" />
                				</div>
                			</td>
                			<td class="infoline">
                				<div align="center">
                    				<kul:htmlControlAttribute property="document.routingFormDocumentResearchRisk[${researchRiskStatus.index}].researchRiskStudy[${studyStatus.index}].researchRiskStudyApprovalDate" attributeEntry="${routingFormResearchRiskAttributes.researchRiskStudyApprovalDate}" datePicker="true" />
                				</div>
                			</td>
                			<td class="infoline">
                				<div align="center">
                    				<kul:htmlControlAttribute property="document.routingFormDocumentResearchRisk[${researchRiskStatus.index}].researchRiskStudy[${studyStatus.index}].researchRiskStudyReviewCode" attributeEntry="${routingFormResearchRiskAttributes.researchRiskStudyReviewCode}" />
                				</div>
                			</td>
                			<td class="infoline">
                				<div align="center">
                    				<kul:htmlControlAttribute property="document.routingFormDocumentResearchRisk[${researchRiskStatus.index}].researchRiskStudy[${studyStatus.index}].researchRiskExemptionNumber" attributeEntry="${routingFormResearchRiskAttributes.researchRiskExemptionNumber}" />                
                				</div>
                			</td>
                			<td class="infoline">
                				<div align=center><html:image property="methodToCall.deleteRoutingFormResearchRiskStudy.line${studyStatus.index}.anchor${currentTabIndex}" styleClass="tinybutton" src="images/tinybutton-delete1.gif" alt="delete research risk"/></div>
                				<html:hidden property="document.routingFormDocumentResearchRisk[${researchRiskStatus.index}].researchRiskStudy[${studyStatus.index}].researchDocumentNumber"/>
                				<html:hidden property="document.routingFormDocumentResearchRisk[${researchRiskStatus.index}].researchRiskStudy[${studyStatus.index}].researchRiskTypeCode"/>
                				<html:hidden property="document.routingFormDocumentResearchRisk[${researchRiskStatus.index}].researchRiskStudy[${studyStatus.index}].proposalResearchRiskSequenceNumber"/>
                				<html:hidden property="document.routingFormDocumentResearchRisk[${researchRiskStatus.index}].researchRiskStudy[${studyStatus.index}].objectId"/>
                				<html:hidden property="document.routingFormDocumentResearchRisk[${researchRiskStatus.index}].researchRiskStudy[${studyStatus.index}].versionNumber"/>
                			</td>
              			</tr>
              		</c:forEach>
            	</table>
          	</div>
		</kul:tab>
	</c:if>
	
	<c:if test="${researchRisk.researchRiskType.controlAttributeTypeCode == 'D'}">
		<kul:tab tabTitle="${researchRisk.researchRiskType.researchRiskTypeDescription}" defaultOpen="false">
			<div class="tab-container" id="G02" style="" align="center">
            	<div class="h2-container"><h2>${researchRisk.researchRiskType.researchRiskTypeDescription}</h2></div>
            	<div class="tab-container" align="center" id="G4">
					<table cellpadding="0" cellspacing="0" summary="">
						<tr>
							<th>
								Describe:
							</th>
							<td>
								<span class="infoline"> <textarea name="textfield"
										cols="60" rows="5"></textarea> </span>
							</td>
						</tr>
					</table>
				</div>
            </div>
        </kul:tab>
	</c:if>

</c:forEach>