[#-------------- INCLUDES AND ASSIGNMENTS --------------]
[#include "macros/breadcrumb.ftl"]
[#include "macros/productTeaserList.ftl"]
[#include "macros/productFilters.ftl"]
[#include "macros/productPager.ftl"]

[#assign queryStr = (ctx.getParameter("queryStr")?html)!""]
[#assign currentPage = (ctx.getParameter("currentPage")?number)!1]
[#assign perPage = (content.perPage?number)!20]
[#if perPage == 0]
    [#-- set max limit value--]
    [#assign perPage = 500]
[/#if]
[#assign productTypes = ctfn.getProductTypes()]
[#assign attributeFacets = (content.attributeFacets)![]]
[#assign filterBy = ctfn.getFilterBy(attributeFacets)]

[#-------------- RENDERING --------------]
[#if queryStr?has_content]
    <div class="container">
        <div class="row">
            [#assign products = ctfn.searchForProducts(queryStr, (currentPage-1)*perPage, perPage, productTypes, attributeFacets, filterBy)!]
            [#if products.getResults()?has_content]
                [#if products.getFacetsResults()?has_content]
                    [@productFilters attributeFacets products.getFacetsResults() productTypes filterBy {"queryStr":queryStr} /]
                [/#if]
                <div class="search-results row col-lg-9 col-md-9 col-sm-9">
                    <h3>Search results for: <span>${queryStr}</span></h3>
                </div>
                <div class="row item-listing flex-box col-lg-9 col-md-9 col-sm-9">
                    [#if products.getResults()?has_content]
                        [@productTeaserList products content.productDetailPage /]
                    [/#if]
                </div>
            [#else]
                ${i18n['ct.noResultsFound']}
            [/#if]
        </div>
    </div>
    <!-- Pagination -->
    [@productPager (products.getTotal())!0 currentPage perPage /]
[/#if]
