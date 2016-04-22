[#-------------- ASSIGNMENTS --------------]
[#assign categoryId = (ctx.getParameter("categoryId")?html)!content.categoryId!]
[#assign page = ((ctx.getParameter("page")?html)?number)!1]
[#assign perPage = (content.perPage?number)!12]

[#-------------- RENDERING --------------]
[#if categoryId?has_content]
    [#assign products = ctfn.getProducts(categoryId, (page-1)*perPage, perPage)!]
    [#if products?has_content]
        [#list products as product]
            [#assign teaserLink = cmsfn.link(cmsfn.contentByIdentifier(content.productDetailPage!""))]
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
    [/#if]
[/#if]
