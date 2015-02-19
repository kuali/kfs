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
<%@ tag description="render the capital modify tag that contains the given capital asset info"%>

<%@ attribute name="readOnly" required="false" description="Whether the capital asset information should be read only" %>

<c:set var="capitalAssetInfoSize" value="${fn:length(KualiForm.document.capitalAssetInformation)}" />	
<c:set var="defaultOpen" value="false"/>
<c:set var="modifiedCapitalAssets" value="0"/>

<c:forEach items="${KualiForm.document.capitalAssetInformation}" var="detailLine" varStatus="status">
	<c:if test="${detailLine.capitalAssetActionIndicator == KFSConstants.CapitalAssets.CAPITAL_ASSET_MODIFY_ACTION_INDICATOR}">
		<c:set var="modifiedCapitalAssets" value="${modifiedCapitalAssets + 1}"/>
	</c:if>
</c:forEach>	

<c:if test="${modifiedCapitalAssets > 0}" >
	<c:set var="defaultOpen" value="true"/>
</c:if>

<kul:tab tabTitle="${KFSConstants.CapitalAssets.MODIFY_CAPITAL_ASSETS_TAB_TITLE}" defaultOpen="${defaultOpen}" tabErrorKey="${KFSConstants.EDIT_CAPITAL_ASSET_MODIFY_ERRORS}" >
     <div class="tab-container" align="center" valign="middle">
		 <h3>Modify Capital Assets</h3>
		 <table class="datatable" cellpadding="0" cellspacing="0" summary="Capital Asset Information">
		     <c:if test="${modifiedCapitalAssets <= 0}">
				<tr>
					<td class="datacell" height="50" colspan="5"><div align="center">There are currently no Modify Capital Assets entries associated with this Transaction Processing document.</div></td>
				</tr>
			</c:if>
			<c:if test="${modifiedCapitalAssets > 0}">
				<tr>
					<td>
						<fp:capitalAssetModify readOnly="${readOnly}"/>					
					</td>
				</tr>	 
	 		</c:if>
	     </table>
	 </div>	
</kul:tab>	 
