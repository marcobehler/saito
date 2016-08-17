[#-- @ftlvariable name="paginator" type="com.marcobehler.saito.core.pagination.Paginator" --]
[#-- @ftlvariable name="saitoLinkHelper" type="com.marcobehler.saito.core.util.LinkHelper" --]

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

[#macro favicon_tag icon]
${saitoLinkHelper.favicon(icon)}
[/#macro]

[#macro image_tag name height=-1 width=-1 class="" data="", alt=""]
${saitoLinkHelper.imageTag(name, width, height, class, data, alt)}
[/#macro]

[#macro paginate data]
${paginator.restartIfNecessary(data, current_page.pagination.per_page)}
    [#list data as d]
        [#nested d]
    [/#list]
[/#macro]
