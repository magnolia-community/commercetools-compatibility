[#macro breadcrumb category productsLink]
    [#-------------- ASSIGNMENTS --------------]
    [#assign breadcrumbItemList = ctfn.getCategoryListForBreadcrumb(category)]
    [#assign siteRootLink = cmsfn.link(cmsfn.siteRoot(content))]

    [#-------------- RENDERING --------------]
    [#if breadcrumbItemList?has_content]
    <!-- Breadcrumb -->
        <div>
            <a href="${siteRootLink}">HOME</a>
            [#list breadcrumbItemList as breadcrumbItem]
                [#list breadcrumbItem?keys as categoryId]
                    <a href="${productsLink}?categoryId=${categoryId}">${breadcrumbItem[categoryId]}</a>
                [/#list]
            [/#list]
        </div>
    <!-- Breadcrumb end -->
    [/#if]
[/#macro]
