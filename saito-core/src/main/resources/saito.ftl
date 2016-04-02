[#macro yield]${_saito_content_}[/#macro]

[#macro partial name]
    [#include "_" + name + ".ftl"]
[/#macro]

[#macro stylesheet_link_tag files]
[#list files as f]
    <link rel="stylesheet" href="/stylesheets/${f}.css"/>
[/#list]
[/#macro]

[#macro javascript_include_tag files]
[#list files as f]
    <script src="/javascripts/${f}.js" ></script>
[/#list]
[/#macro]