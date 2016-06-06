[#macro breadcrumb category productsLink]
    [#-------------- ASSIGNMENTS --------------]
    [#assign breadcrumbItemList = ctfn.getCategoryListForBreadcrumb(category)]
    [#assign siteRootLink = cmsfn.link(cmsfn.siteRoot(content))]

    [#-------------- RENDERING --------------]
    [#if breadcrumbItemList?has_content]
    <!-- Breadcrumb -->
        <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 flex-box bc-group btn-group btn-breadcrumb">
            <a href="${siteRootLink}" class="bc-button btn btn-default"><i class="glyphicon glyphicon-home"></i></a>
            [#list breadcrumbItemList as breadcrumbItem]
                [#list breadcrumbItem?keys as categoryId]
                    <a href="${productsLink}?categoryId=${categoryId}" class="bc-button btn btn-default">${breadcrumbItem[categoryId]}</a>
                [/#list]
            [/#list]
        </div>
    <!-- Breadcrumb end -->
    [/#if]
[/#macro]
