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