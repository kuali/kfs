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
<tiles:useAttribute name="manager" classname="org.kuali.rice.krad.uif.layout.BoxLayoutManager"/>
<tiles:useAttribute name="container" classname="org.kuali.rice.krad.uif.container.ContainerBase"/>

<%--
    Box Layout Manager:
    
      Places each component of the given list into a horizontal or vertical row.
      
      The amount of padding is configured by the seperationPadding 
      property of the layout manager. The padding is implemented by setting the margin of the wrapping
      span style. For vertical orientation, the span style is set to block.
 --%>
 
<c:if test="${!empty manager.styleClassesAsString}">
  <c:set var="styleClass" value="class=\"${manager.styleClassesAsString}\""/>
</c:if>

<c:if test="${!empty manager.style}">
  <c:set var="style" value="${manager.style}"/>
</c:if>

<c:if test="${!empty manager.itemStyle}">
  <c:set var="itemStyle" value="style=\"${manager.itemStyle}\""/>
</c:if>

<c:choose>
  <c:when test="${manager.orientation=='HORIZONTAL'}">
     <c:set var="itemStyleClass" value="boxLayoutHorizontalItem ${manager.itemStyleClassesAsString}"/>
  </c:when>
  <c:otherwise>
     <c:set var="itemStyleClass" value="boxLayoutVerticalItem ${manager.itemStyleClassesAsString} clearfix"/>
  </c:otherwise>
</c:choose>

<c:choose>
  <c:when test="${container.fieldContainer}">
     <c:set var="style" value="float:left;${style}"/>
  </c:when>
  <c:otherwise>
     <c:set var="itemStyleClass" value="fieldLine ${itemStyleClass}"/>
  </c:otherwise>
</c:choose>

<c:set var="itemStyleClass" value="class=\"${itemStyleClass}\""/>

<c:if test="${!empty style}">
  <c:set var="style" value="style=\"${style}\""/>
</c:if>

<%-- render items --%>
<div id="${manager.id}" ${style} ${styleClass}>
  <c:forEach items="${items}" var="item" varStatus="itemVarStatus">
     <span ${itemStyle} ${itemStyleClass}>
        <krad:template component="${item}"/>
     </span>
  </c:forEach>

  <%--
     Adds a special error container for horizontal case, fields will instead display their errors here
     (errorsField in attributeFields of this layout will not generate their errorsField through their jsp, as normal)
     see BoxLayoutManager.java
  --%>
  <c:if test="${manager.layoutFieldErrors}">
	   <span id="${manager.id}_errors_block" class="kr-errorsField" style="float:left;">
	   		<c:forEach items="${container.inputFields}" var="item">
           <krad:template component="${item.errorsField}"/>
         </c:forEach>
	   </span>
  </c:if>

</div> 