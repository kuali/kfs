<%--

    Copyright 2005-2014 The Kuali Foundation

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
<%@ include file="/krad/WEB-INF/jsp/tldHeader.jsp"%>

<tiles:useAttribute name="items" classname="java.util.List"/>
<tiles:useAttribute name="manager" classname="org.kuali.rice.krad.uif.layout.TableLayoutManager"/>

<%--
    Table Layout Manager:
    
      Works on a collection group to lay out the items as a table.
 --%>
 
<c:if test="${!empty manager.styleClassesAsString}">
  <c:set var="styleClass" value="class=\"${manager.styleClassesAsString}\""/>
</c:if>

<c:if test="${!empty manager.style}">
  <c:set var="style" value="style=\"${manager.style}\""/>
</c:if>

<c:if test="${manager.separateAddLine}">
  <krad:template component="${manager.addLineGroup}"/>
</c:if>

<c:if test="${!empty manager.headerFields}">
	<table id="${manager.id}" ${style} ${styleClass}>
		
		  <thead>
		     <krad:grid items="${manager.headerFields}" numberOfColumns="${manager.numberOfColumns}" 
		                renderHeaderRow="true" renderAlternatingHeaderColumns="false"
		                applyDefaultCellWidths="${manager.applyDefaultCellWidths}"/>
		  </thead>
	
		  <tbody>
		     <krad:grid items="${manager.dataFields}" numberOfColumns="${manager.numberOfColumns}" 
		                applyAlternatingRowStyles="${manager.applyAlternatingRowStyles}"
		                applyDefaultCellWidths="${manager.applyDefaultCellWidths}"
                    firstLineStyle="${manager.firstLineStyle}"
		                renderAlternatingHeaderColumns="false"/>
		  </tbody>
	  	
	</table>
</c:if>

<%-- invoke table tools widget --%>
<krad:template component="${manager.richTable}" componentId="${manager.id}"/>