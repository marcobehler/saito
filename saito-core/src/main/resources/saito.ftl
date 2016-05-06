[#-- @ftlvariable name="paginator" type="com.marcobehler.saito.core.pagination.Paginator" --]

[#macro yield]${_saito_content_}[/#macro]

[#macro partial name]
    [#include "_" + name + ".ftl"]
[/#macro]

[#macro stylesheet_link_tag files]
${saitoLinkHelper.styleSheet(files)}
[/#macro]

[#macro javascript_include_tag files]
${saitoLinkHelper.javascript(files)}
[/#macro]

[#macro paginate data]
${paginator.restartIfNecessary(data, pagination.per_page)}
    [#list data as d]
        [#nested d]
    [/#list]
[/#macro]
