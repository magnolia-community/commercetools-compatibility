[#-------------- ASSIGNMENTS --------------]
[#assign site = sitefn.site()!]
[#assign theme = sitefn.theme(site)!]

[#-------------- RENDERING --------------]
<!DOCTYPE html>
[@cms.init /]

<html xml:lang="${cmsfn.language()}" lang="${cmsfn.language()}" class="no-js">

<head>
    [@cms.area name="htmlHead" content=content/]

    [#list theme.cssFiles as cssFile]
        [#if cssFile.conditionalComment?has_content]<!--[if ${cssFile.conditionalComment}]>[/#if]
        <link rel="stylesheet" type="text/css" href="${cssFile.link}" media="${cssFile.media}" />
        [#if cssFile.conditionalComment?has_content]<![endif]-->[/#if]
    [/#list]

    [#-- jsFiles from the theme are here we need it up here because of jquery inside of components --]
    [#list theme.jsFiles as jsFile]
        <script src="${jsFile.link}"></script>
    [/#list]
</head>

<body>
    [@cms.area name="navigation" /]
    [@cms.area name="header" /]
    [@cms.area name="main" /]
    [@cms.area name="footer" /]
</body>
</html>
