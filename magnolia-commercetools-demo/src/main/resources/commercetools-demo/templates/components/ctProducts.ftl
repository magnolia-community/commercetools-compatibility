[#-------------- INCLUDES AND ASSIGNMENTS --------------]
[#include "macros/breadcrumb.ftl"]
[#include "macros/productTeaserList.ftl"]
[#include "macros/productFilters.ftl"]
[#include "macros/productPager.ftl"]

[#assign categoryId = (ctx.getParameter("categoryId")?html)!content.categoryId?split("--")?reverse[0]!""]
[#assign currentPage = (ctx.getParameter("currentPage")?number)!1]
[#assign perPage = (content.perPage?number)!20]
[#assign productTypes = ctfn.getProductTypes()]
[#assign attributeFacets = (content.attributeFacets)![]]
[#assign filterBy = ctfn.getFilterBy(attributeFacets)]

[#if perPage == 0]
    [#-- set max limit value--]
    [#assign perPage = 500]
[/#if]

[#-------------- RENDERING --------------]
[#if categoryId?has_content]
    [#assign products = ctfn.getProducts(categoryId, (currentPage-1)*perPage, perPage, productTypes, attributeFacets, filterBy)!]
    <div class="container">
        <div class="row">
            [#if products.getFacetsResults()?has_content]
                [@productFilters attributeFacets products.getFacetsResults() productTypes filterBy {"categoryId":categoryId} /]
            [/#if]
            <div class="item-listing flex-box col-lg-9 col-md-9 col-sm-9">
                [@breadcrumb ctfn.getCategory(categoryId) cmsfn.link(cmsfn.page(content)) /]
                [#if products.getResults()?has_content]
                    [@productTeaserList products content.productDetailPage /]
                [/#if]
            </div>
        </div>
    </div>
    <!-- Pagination -->
    [#if products.getTotal()?has_content]
        [@productPager products.getTotal() currentPage perPage /]
    [/#if]
[/#if]
