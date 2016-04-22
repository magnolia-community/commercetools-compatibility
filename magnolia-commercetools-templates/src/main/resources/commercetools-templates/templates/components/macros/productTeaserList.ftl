[#macro productTeaserList products currentPage perPage productDetailPage=""]
    [#-------------- INCLUDES AND ASSIGNMENTS --------------]
    [#include "./productPager.ftl"]
    [#assign teaserLink = cmsfn.link("website", productDetailPage)]

    [#-------------- RENDERING --------------]
    <!-- Pagination -->
    [@productPager products.getTotal() currentPage perPage /]
    [#list products.getResults() as product]
        <!-- ProductDetail -->
        <div>
            <a href="${teaserLink}?productId=${product.getId()}">
                <img src="${product.getMasterVariant().getImages()[0].getUrl()!""}" alt="${product.getName().get(ctfn.getLanguage())!""}">
            </a>
            <p><a href="${teaserLink}?productId=${product.getId()}">${product.getName().get(ctfn.getLanguage())!""}</a></p>
            <div>
                <span><strong>${ctfn.getPriceToShow(product.getMasterVariant().getPrices())!i18n['ctProduct.priceNotSet']}</strong></span>
                <a href="#">${i18n['ctProduct.addToCart']}</a>
            </div>
        </div>
    [/#list]
[/#macro]