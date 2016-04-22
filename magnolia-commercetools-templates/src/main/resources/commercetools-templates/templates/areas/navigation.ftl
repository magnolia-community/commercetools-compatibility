[#-------------- INCLUDES AND ASSIGNMENTS --------------]
[#include "../components/macros/menuCategoryList.ftl"]

[#assign categories = ctfn.getCategories(null)]
[#assign siteRootLink = cmsfn.link(cmsfn.siteRoot(content))]
[#assign homePageContent = cmsfn.siteRoot(content)]
[#assign customer = (ctx.getAttribute("ctCustomer"))!""]

[#-------------- RENDERING --------------]
<!-- Navigation-->
<nav>
    [@menuCategoryList categories siteRootLink homePageContent.maxCategoriesDepth/]
    <ul>
        <li>
            <form id="navbar-form" class="navbar-form" role="search">
                <div class="input-group">
                    <input type="text" class="form-control" placeholder="Search...">
                        <span class="input-group-btn">
                            <button type="submit" class="btn btn-default">
                                <span class="glyphicon glyphicon-search">
                                    <span class="sr-only">Search...</span>
                                </span>
                            </button>
                        </span>
                </div>
            </form>
        </li>
    </ul>
    <ul>
        [#if customer?has_content || cmsfn.editMode]
            <li><a href="${cmsfn.link("website", homePageContent.authenticationPage!"")!cmsfn.link(homePageContent)}?ctAction=ctDoLogout">Logout </a></li>
        [/#if]
        [#if !customer?has_content || cmsfn.editMode]
            <li><a href="${cmsfn.link("website", homePageContent.authenticationPage!"")!"#"}">Login </a></li>
        [/#if]
        <li><a href="#">My cart (0) items</a></li>
    </ul>
</nav>
