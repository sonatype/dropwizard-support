<#--

    Copyright (c) 2018-present Sonatype, Inc. All rights reserved.

    This program is licensed to you under the Apache License Version 2.0,
    and you may not use this file except in compliance with the Apache License Version 2.0.
    You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.

    Unless required by applicable law or agreed to in writing,
    software distributed under the Apache License Version 2.0 is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.

-->
<#--
Wrapper for SQL statement.

Adds comment for JDBI method-name from assumed format of main-template.
Automatically adds statement terminator.
Optionally condenses to single-line.
-->
<#macro sql condense=true>
  <@compress single_line=condense>
    <#-- extract location from main template; following format of io.dropwizard.jdbi3.NamePrependingTemplateEngine -->
    <#local fqmn="${.main_template_name?remove_ending('.sql.ftl')?replace('/','.')}"/>
    <#local class_name="${fqmn?keep_before_last('.')}"/>
    <#local method_name="${fqmn?keep_after_last('.')}"/>
    <#local location="${class_name?keep_after_last('.')}.${method_name}"/>
    <#-- extract sql statement from nested -->
    <#local sql><#nested></#local>
    /* ${location} */ ${sql}
    <#-- automatically add statement terminator if missing -->
    <#if !sql?trim?ends_with(';')>;</#if>
  </@compress>
</#macro>