[#-------------- INCLUDES AND ASSIGNMENTS --------------]
[#include "../components/macros/menuCategoryList.ftl"]

[#assign categories = ctfn.getCategories(null).getResults()]
[#assign siteRootLink = cmsfn.link(cmsfn.siteRoot(content))]

[#-------------- RENDERING --------------]
[@menuCategoryList categories siteRootLink content.maxCategoriesDepth/]