[#macro menuCategoryList categories siteRootLink categoriesDepth=-1 categoryId="root"]
    [#assign childCategories = ctfn.getChildCategoriesFromList(categories, categoryId)]
    [#if childCategories?has_content]
        <ul>
            [#list childCategories as childCategory]
                <li><a href="${siteRootLink}?categoryId=${childCategory.getId()}">${childCategory.getName().get(ctfn.getLanguage())}</a>
                    [#if categoriesDepth<0 || categoriesDepth-1>0]
                        [@menuCategoryList categories siteRootLink categoriesDepth-1 childCategory.getId() /]
                    [/#if]
                </li>
            [/#list]
        </ul>
    [/#if]
[/#macro]