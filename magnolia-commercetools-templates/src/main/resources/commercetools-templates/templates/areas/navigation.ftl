[#-------------- INCLUDES AND ASSIGNMENTS --------------]
[#include "../components/macros/menuCategoryList.ftl"]

[#assign categories = ctfn.getCategories(null).getResults()]
[#assign homePageContent = cmsfn.siteRoot(content)]
[#assign siteRootLink = cmsfn.link(homePageContent)]
[#assign customer = (ctx.getAttribute("ctCustomerId"))!""]
[#assign cartItemNum = ctfn.getNumberOfItemsInCart()]

[#-------------- RENDERING --------------]
<!-- Navigation-->
<nav>
    [@menuCategoryList categories siteRootLink homePageContent.maxCategoriesDepth/]
    <ul>
        <li>
            <form id="navbar-form" role="search" action="${cmsfn.link("website", homePageContent.searchResultPage!"")}">
                <input type="text" name="queryStr">
                <button type="submit">${i18n['ct.search']}</button>
            </form>
        </li>
    </ul>
    <ul>
        [#if customer?has_content || cmsfn.editMode]
            <li><a href="${cmsfn.link("website", homePageContent.authenticationPage!"")!cmsfn.link(homePageContent)}?ctAction=ctDoLogout">${i18n['ct.logout']} </a></li>
        [/#if]
        [#if !customer?has_content || cmsfn.editMode]
            <li><a href="${cmsfn.link("website", homePageContent.authenticationPage!"")!"#"}">${i18n['ct.login']} </a></li>
        [/#if]
        <li><a href="${cmsfn.link("website", homePageContent.cartPage!"")!"#"}">My cart (<span id="cartItemNum">${cartItemNum}</span>) items</a></li>
    </ul>
</nav>
