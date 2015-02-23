/*
 * Copyright 2005-2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
(function($) {
	$.fn.selectTab = function(options){
		return this.each(function(){
			options = options || {};
			//default setting
			options = $.extend({
				selectPage: ""
			}, options);
			
			if(options.selectPage){
				var currentTab = $(this).find("a[name='" + options.selectPage + "']");
				if(currentTab){
					currentTab.parent().addClass("ui-state-active");
				}
			}
		});
	}
	
	$.fn.tabMenu = function(options){
		return this.each(function(){
			options = options || {};
			//default setting
			options = $.extend({
				defaultSelectFirst: true,
				currentPage: ""
			}, options);
			
			//element id strings
			var id = $(this).parent().attr('id');
			var list_elements = "#" + id + " li";
			var link_elements = list_elements + " a";
			
			//Styling
			$(this).parent().addClass("ui-tabs tab-navigation-block");
			$(this).addClass("ui-helper-reset ui-helper-clearfix tabMenu");
			$(list_elements).addClass("ui-state-default ui-corner-top");
			if(options.currentPage){
				var currentTab = $(this).find("a[name='" + options.currentPage + "']");
				if(currentTab){
					currentTab.closest("li").addClass("ui-state-active");
				}
			}
			//Handlers and animation
			$(document).ready(function()
			{
					$(link_elements).each(function(i)
					{
						if(i == 0 && options.defaultSelectFirst && !options.currentPage){
							$(this).closest("li").addClass("ui-state-active");
						}
						$(this).focus(
						function()
						{
							$(this).closest("li").addClass("ui-state-focus");
						});

                        $(this).click(
						function()
						{
							$(link_elements).each(function(){$(this).closest("li").removeClass("ui-state-active")});
							$(this).closest("li").addClass("ui-state-active");
						});
				
						$(this).hover(
						function()
						{
							$(this).closest("li").addClass("ui-state-hover");
						},		
						function()
						{
							$(this).closest("li").removeClass("ui-state-hover");
						});
				

				
						$(this).blur(
						function()
						{
							$(this).closest("li").removeClass("ui-state-focus");
						});
					});
			});
		});
	}
})(jQuery);