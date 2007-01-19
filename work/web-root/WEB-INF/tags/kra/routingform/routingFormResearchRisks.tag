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

<%@ include file="/jsp/core/tldHeader.jsp"%>

<c:set var="routingFormResearchRiskStudyAttributes" value="${DataDictionary.RoutingFormResearchRiskStudy.attributes}" />
<c:set var="routingFormResearchRiskAttributes" value="${DataDictionary.RoutingFormResearchRisk.attributes}"/>

<div id="workarea">
  <c:forEach items="${KualiForm.routingFormDocument.routingFormResearchRisks}" var="researchRisk" varStatus="researchRiskStatus">
	<html:hidden property="document.routingFormResearchRisk[${researchRiskStatus.index}].documentNumber"/>
	<html:hidden property="document.routingFormResearchRisk[${researchRiskStatus.index}].researchRiskTypeCode"/>
	<html:hidden property="document.routingFormResearchRisk[${researchRiskStatus.index}].objectId"/>
	<html:hidden property="document.routingFormResearchRisk[${researchRiskStatus.index}].versionNumber"/>
	<html:hidden property="document.routingFormResearchRisk[${researchRiskStatus.index}].researchRiskType.researchRiskTypeCode"/>
	<html:hidden property="document.routingFormResearchRisk[${researchRiskStatus.index}].researchRiskType.researchRiskTypeDescription"/>
	<html:hidden property="document.routingFormResearchRisk[${researchRiskStatus.index}].researchRiskType.researchRiskTypeNotificationGroupText"/>
	<html:hidden property="document.routingFormResearchRisk[${researchRiskStatus.index}].researchRiskType.controlAttributeTypeCode"/>
	
	<c:choose>
	  <c:when test="${researchRisk.researchRiskType.controlAttributeTypeCode == KraConstants.RESEARCH_RISK_TYPE_ALL_COLUMNS}">
		<kul:tab 
			tabTitle="${researchRisk.researchRiskType.researchRiskTypeDescription}" 
			defaultOpen="false" rightSideHtmlProperty="document.routingFormResearchRisk[${researchRiskStatus.index}].dataPresentIndicator" 
			rightSideHtmlAttribute="${routingFormResearchRiskStudyAttributes.dataPresentIndicator}" 
			transparentBackground="${researchRiskStatus.index == 0}"
			tabErrorKey="document.routingFormResearchRisk[${researchRiskStatus.index}]*"
			tabItemCount="${researchRisk.numStudies}">
			<div class="tab-container" id="G02" style="" align="center">
            	<div class="h2-container"><h2>${researchRisk.researchRiskType.researchRiskTypeDescription}</h2></div>
            	<table cellpadding="0" cellspacing="0" summary="">
              		<tr>
                		<td colspan=8 class="tab-subhead"><span class="left">Insert Study </span> </td>
              		</tr>
              		<tr>
                		<th>&nbsp;</th>
                		<th>
                			<div align="center">
								<kul:htmlAttributeLabel attributeEntry="${routingFormResearchRiskStudyAttributes.researchRiskStudyApprovalStatusCode}" useShortLabel="false" skipHelpUrl="true" noColon="true" />
							</div>
						</th>
                		<th>
                			<div align="center">
								<kul:htmlAttributeLabel attributeEntry="${routingFormResearchRiskStudyAttributes.researchRiskStudyNumber}" useShortLabel="false" skipHelpUrl="true" noColon="true" />
                			</div>
                		</th>
                		<th>
                			<div align="center">
								<kul:htmlAttributeLabel attributeEntry="${routingFormResearchRiskStudyAttributes.researchRiskStudyApprovalDate}" useShortLabel="false" skipHelpUrl="true" noColon="true" />
							</div>
						</th>
						<th>
                			<div align="center">
								<kul:htmlAttributeLabel attributeEntry="${routingFormResearchRiskStudyAttributes.researchRiskStudyExpirationDate}" useShortLabel="false" skipHelpUrl="true" noColon="true" />
							</div>
						</th>
                		<th>
                			<div align="center">
								<kul:htmlAttributeLabel attributeEntry="${routingFormResearchRiskStudyAttributes.researchRiskStudyReviewCode}" useShortLabel="false" skipHelpUrl="true" noColon="true" />                
                			</div>
                		</th>
                		<th>
                			<div align="center">
								<kul:htmlAttributeLabel attributeEntry="${routingFormResearchRiskStudyAttributes.researchRiskExemptionNumber}" useShortLabel="false" skipHelpUrl="true" noColon="true" />
							</div>
						</th>
                		<th>Action</th>
              		</tr>
              		<tr>
                		<th scope="row">add:</th>
                		<td class="infoline">
                			<div align="center">
                				<kul:htmlControlAttribute property="document.routingFormResearchRisk[${researchRiskStatus.index}].newResearchRiskStudy.researchRiskStudyApprovalStatusCode" attributeEntry="${routingFormResearchRiskStudyAttributes.researchRiskStudyApprovalStatusCode}" />
                			</div>
                		</td>
                		<td class="infoline">
                			<div align="center">
                    			<kul:htmlControlAttribute property="document.routingFormResearchRisk[${researchRiskStatus.index}].newResearchRiskStudy.researchRiskStudyNumber" attributeEntry="${routingFormResearchRiskStudyAttributes.researchRiskStudyNumber}" />
                			</div>
                		</td>
                		<td class="infoline">
                			<div align="center">
                    			<kul:htmlControlAttribute property="document.routingFormResearchRisk[${researchRiskStatus.index}].newResearchRiskStudy.researchRiskStudyApprovalDate" attributeEntry="${routingFormResearchRiskStudyAttributes.researchRiskStudyApprovalDate}" datePicker="true" />
                			</div>
                		</td>
                		<td class="infoline">
                			<div align="center">
                    			<kul:htmlControlAttribute property="document.routingFormResearchRisk[${researchRiskStatus.index}].newResearchRiskStudy.researchRiskStudyExpirationDate" attributeEntry="${routingFormResearchRiskStudyAttributes.researchRiskStudyExpirationDate}" datePicker="true" />
                			</div>
                		</td>
                		<td class="infoline">
                			<div align="center">
                    			<kul:htmlControlAttribute property="document.routingFormResearchRisk[${researchRiskStatus.index}].newResearchRiskStudy.researchRiskStudyReviewCode" attributeEntry="${routingFormResearchRiskStudyAttributes.researchRiskStudyReviewCode}" />
                			</div>
                		</td>
                		<td class="infoline">
                			<div align="center">
                    			<kul:htmlControlAttribute property="document.routingFormResearchRisk[${researchRiskStatus.index}].newResearchRiskStudy.researchRiskExemptionNumber" attributeEntry="${routingFormResearchRiskStudyAttributes.researchRiskExemptionNumber}" />                
                			</div>
                		</td>
                		<td class="infoline"><div align=center><html:image property="methodToCall.insertRoutingFormResearchRiskStudy.anchor${currentTabIndex}.line${researchRiskStatus.index}" styleClass="tinybutton" src="images/tinybutton-add1.gif" alt="add research risk"/></div></td>
              		</tr>
              		
              		<c:forEach items="${researchRisk.researchRiskStudies}" var="study" varStatus="studyStatus">
				 		<tr>
                			<th scope="row">${studyStatus.index + 1}</th>
                			<td class="infoline">
                				<div align="center">
	            					<kul:htmlControlAttribute property="document.routingFormResearchRisk[${researchRiskStatus.index}].researchRiskStudy[${studyStatus.index}].researchRiskStudyApprovalStatusCode" attributeEntry="${routingFormResearchRiskStudyAttributes.researchRiskStudyApprovalStatusCode}" />
                				</div>
                			</td>
                			<td class="infoline">
                				<div align="center">
                    				<kul:htmlControlAttribute property="document.routingFormResearchRisk[${researchRiskStatus.index}].researchRiskStudy[${studyStatus.index}].researchRiskStudyNumber" attributeEntry="${routingFormResearchRiskStudyAttributes.researchRiskStudyNumber}" />
                				</div>
                			</td>
                			<td class="infoline">
                				<div align="center">
                    				<kul:htmlControlAttribute property="document.routingFormResearchRisk[${researchRiskStatus.index}].researchRiskStudy[${studyStatus.index}].researchRiskStudyApprovalDate" attributeEntry="${routingFormResearchRiskStudyAttributes.researchRiskStudyApprovalDate}" datePicker="true" />
                				</div>
                			</td>
                			<td class="infoline">
                				<div align="center">
                    				<kul:htmlControlAttribute property="document.routingFormResearchRisk[${researchRiskStatus.index}].researchRiskStudy[${studyStatus.index}].researchRiskStudyExpirationDate" attributeEntry="${routingFormResearchRiskStudyAttributes.researchRiskStudyExpirationDate}" datePicker="true" />
                				</div>
                			</td>
                			<td class="infoline">
                				<div align="center">
                    				<kul:htmlControlAttribute property="document.routingFormResearchRisk[${researchRiskStatus.index}].researchRiskStudy[${studyStatus.index}].researchRiskStudyReviewCode" attributeEntry="${routingFormResearchRiskStudyAttributes.researchRiskStudyReviewCode}" />
                				</div>
                			</td>
                			<td class="infoline">
                				<div align="center">
                    				<kul:htmlControlAttribute property="document.routingFormResearchRisk[${researchRiskStatus.index}].researchRiskStudy[${studyStatus.index}].researchRiskExemptionNumber" attributeEntry="${routingFormResearchRiskStudyAttributes.researchRiskExemptionNumber}" />
                				</div>
                			</td>
                			<td class="infoline">
                				<div align=center><html:image property="methodToCall.deleteRoutingFormResearchRiskStudy.tab${currentTabIndex}.line${studyStatus.index}.anchor${currentTabIndex}" styleClass="tinybutton" src="images/tinybutton-delete1.gif" alt="delete research risk"/></div>
                				<html:hidden property="document.routingFormResearchRisk[${researchRiskStatus.index}].researchRiskStudy[${studyStatus.index}].documentNumber"/>
                				<html:hidden property="document.routingFormResearchRisk[${researchRiskStatus.index}].researchRiskStudy[${studyStatus.index}].researchRiskTypeCode"/>
                				<html:hidden property="document.routingFormResearchRisk[${researchRiskStatus.index}].researchRiskStudy[${studyStatus.index}].routingFormResearchRiskStudySequenceNumber"/>
                				<html:hidden property="document.routingFormResearchRisk[${researchRiskStatus.index}].researchRiskStudy[${studyStatus.index}].objectId"/>
                				<html:hidden property="document.routingFormResearchRisk[${researchRiskStatus.index}].researchRiskStudy[${studyStatus.index}].versionNumber"/>
                			</td>
              			</tr>
              		</c:forEach>
            	</table>
          	</div>
		</kul:tab>
	  </c:when>
	  <c:when test="${researchRisk.researchRiskType.controlAttributeTypeCode == KraConstants.RESEARCH_RISK_TYPE_SOME_COLUMNS}">
		<kul:tab 
			tabTitle="${researchRisk.researchRiskType.researchRiskTypeDescription}" 
			defaultOpen="false"
			rightSideHtmlProperty="document.routingFormResearchRisk[${researchRiskStatus.index}].dataPresentIndicator" 
			rightSideHtmlAttribute="${routingFormResearchRiskStudyAttributes.dataPresentIndicator}"
			transparentBackground="${researchRiskStatus.index == 0}"
			tabErrorKey="document.routingFormResearchRisk[${researchRiskStatus.index}]*"
			tabItemCount="${researchRisk.numStudies}">
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
								<kul:htmlAttributeLabel attributeEntry="${routingFormResearchRiskStudyAttributes.researchRiskStudyApprovalStatusCode}" useShortLabel="false" skipHelpUrl="true" noColon="true" />
							</div>
						</th>
                		<th>
                			<div align="center">
								<kul:htmlAttributeLabel attributeEntry="${routingFormResearchRiskStudyAttributes.researchRiskStudyNumber}" useShortLabel="false" skipHelpUrl="true" noColon="true" />
                			</div>
                		</th>
                		<th>
                			<div align="center">
								<kul:htmlAttributeLabel attributeEntry="${routingFormResearchRiskStudyAttributes.researchRiskStudyApprovalDate}" useShortLabel="false" skipHelpUrl="true" noColon="true" />
							</div>
						</th>
						<th>
                			<div align="center">
								<kul:htmlAttributeLabel attributeEntry="${routingFormResearchRiskStudyAttributes.researchRiskStudyExpirationDate}" useShortLabel="false" skipHelpUrl="true" noColon="true" />
							</div>
						</th>
                		<th>Action</th>
              		</tr>
              		<tr>
                		<th scope="row">add:</th>
                		<td class="infoline">
                			<div align="center">
                				<kul:htmlControlAttribute property="document.routingFormResearchRisk[${researchRiskStatus.index}].newResearchRiskStudy.researchRiskStudyApprovalStatusCode" attributeEntry="${routingFormResearchRiskStudyAttributes.researchRiskStudyApprovalStatusCode}" />
                			</div>
                		</td>
                		<td class="infoline">
                			<div align="center">
                    			<kul:htmlControlAttribute property="document.routingFormResearchRisk[${researchRiskStatus.index}].newResearchRiskStudy.researchRiskStudyNumber" attributeEntry="${routingFormResearchRiskStudyAttributes.researchRiskStudyNumber}" />
                			</div>
                		</td>
                		<td class="infoline">
                			<div align="center">
                    			<kul:htmlControlAttribute property="document.routingFormResearchRisk[${researchRiskStatus.index}].newResearchRiskStudy.researchRiskStudyApprovalDate" attributeEntry="${routingFormResearchRiskStudyAttributes.researchRiskStudyApprovalDate}" datePicker="true" />
                			</div>
                		</td>
                		<td class="infoline">
                			<div align="center">
                    			<kul:htmlControlAttribute property="document.routingFormResearchRisk[${researchRiskStatus.index}].newResearchRiskStudy.researchRiskStudyExpirationDate" attributeEntry="${routingFormResearchRiskStudyAttributes.researchRiskStudyExpirationDate}" datePicker="true" />
                			</div>
                		</td>
                		<td class="infoline"><div align=center><html:image property="methodToCall.insertRoutingFormResearchRiskStudy.anchor${currentTabIndex}.line${researchRiskStatus.index}" styleClass="tinybutton" src="images/tinybutton-add1.gif" alt="add research risk"/></div></td>
              		</tr>
              		
              		<c:forEach items="${researchRisk.researchRiskStudies}" var="study" varStatus="studyStatus">
				 		<tr>
                			<th scope="row">${studyStatus.index + 1}</th>
                			<td class="infoline">
                				<div align="center">
	            					<kul:htmlControlAttribute property="document.routingFormResearchRisk[${researchRiskStatus.index}].researchRiskStudy[${studyStatus.index}].researchRiskStudyApprovalStatusCode" attributeEntry="${routingFormResearchRiskStudyAttributes.researchRiskStudyApprovalStatusCode}" />
                				</div>
                			</td>
                			<td class="infoline">
                				<div align="center">
                    				<kul:htmlControlAttribute property="document.routingFormResearchRisk[${researchRiskStatus.index}].researchRiskStudy[${studyStatus.index}].researchRiskStudyNumber" attributeEntry="${routingFormResearchRiskStudyAttributes.researchRiskStudyNumber}" />
                				</div>
                			</td>
                			<td class="infoline">
                				<div align="center">
                    				<kul:htmlControlAttribute property="document.routingFormResearchRisk[${researchRiskStatus.index}].researchRiskStudy[${studyStatus.index}].researchRiskStudyApprovalDate" attributeEntry="${routingFormResearchRiskStudyAttributes.researchRiskStudyApprovalDate}" datePicker="true" />
                				</div>
                			</td>
                			<td class="infoline">
                				<div align="center">
                    				<kul:htmlControlAttribute property="document.routingFormResearchRisk[${researchRiskStatus.index}].researchRiskStudy[${studyStatus.index}].researchRiskStudyExpirationDate" attributeEntry="${routingFormResearchRiskStudyAttributes.researchRiskStudyExpirationDate}" datePicker="true" />
                				</div>
                			</td>
                			<td class="infoline">
                				<div align=center><html:image property="methodToCall.deleteRoutingFormResearchRiskStudy.tab${currentTabIndex}.line${studyStatus.index}.anchor${currentTabIndex}" styleClass="tinybutton" src="images/tinybutton-delete1.gif" alt="delete research risk"/></div>
                				<html:hidden property="document.routingFormResearchRisk[${researchRiskStatus.index}].researchRiskStudy[${studyStatus.index}].documentNumber"/>
                				<html:hidden property="document.routingFormResearchRisk[${researchRiskStatus.index}].researchRiskStudy[${studyStatus.index}].researchRiskTypeCode"/>
                				<html:hidden property="document.routingFormResearchRisk[${researchRiskStatus.index}].researchRiskStudy[${studyStatus.index}].routingFormResearchRiskStudySequenceNumber"/>
                				<html:hidden property="document.routingFormResearchRisk[${researchRiskStatus.index}].researchRiskStudy[${studyStatus.index}].objectId"/>
                				<html:hidden property="document.routingFormResearchRisk[${researchRiskStatus.index}].researchRiskStudy[${studyStatus.index}].versionNumber"/>
                			</td>
              			</tr>
              		</c:forEach>
            	</table>
          	</div>
		</kul:tab>
	  </c:when>
	  <c:when test="${researchRisk.researchRiskType.controlAttributeTypeCode == KraConstants.RESEARCH_RISK_TYPE_DESCRIPTION}">
		<kul:tab 
			tabTitle="${researchRisk.researchRiskType.researchRiskTypeDescription}" 
			defaultOpen="false"  
			rightSideHtmlProperty="document.routingFormResearchRisk[${researchRiskStatus.index}].dataPresentIndicator" 
			rightSideHtmlAttribute="${routingFormResearchRiskStudyAttributes.dataPresentIndicator}"  
			transparentBackground="${researchRiskStatus.index == 0}"
			tabErrorKey="document.routingFormResearchRisk[${researchRiskStatus.index}]*">
			<div class="tab-container" id="G02" style="" align="center">
            	<div class="h2-container"><h2>${researchRisk.researchRiskType.researchRiskTypeDescription}</h2></div>
				<table cellpadding="0" cellspacing="0" summary="">
					<tr>
						<th><kul:htmlAttributeLabel attributeEntry="${routingFormResearchRiskAttributes.researchRiskDescription}" useShortLabel="true" skipHelpUrl="true" noColon="false" /></th>
						<td><span class="infoline"><kul:htmlControlAttribute property="document.routingFormResearchRisk[${researchRiskStatus.index}].researchRiskDescription" attributeEntry="${routingFormResearchRiskAttributes.researchRiskDescription}" /></span></td>
					</tr>
				</table>
            </div>
        </kul:tab>
      </c:when>
      <c:otherwise><!-- not a match - do nothing. --></c:otherwise>
    </c:choose>
  </c:forEach>
  <table width="100%" border="0" cellpadding="0" cellspacing="0" class="b3" summary="">
	<tr>
		<td align="left" class="footer"><img src="images/pixel_clear.gif" alt="" width="12" height="14" class="bl3"></td>
		<td align="right" class="footer-right"><img src="images/pixel_clear.gif" alt="" width="12" height="14" class="br3"></td>
    </tr>
  </table>
</div>
