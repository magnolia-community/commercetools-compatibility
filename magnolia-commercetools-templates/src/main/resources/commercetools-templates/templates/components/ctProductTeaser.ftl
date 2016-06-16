[#-------------- INCLUDES AND ASSIGNMENTS --------------]
[#assign detailPageLink = cmsfn.link("website", content.productDetailPage!)!]
[#assign cartId = ctx.getAttribute(ctfn.getProjectName() + "_ctCartId")!""]
[#if ((content.productId?split("--")?reverse[2])!"") == "product"]
    [#assign product = (ctfn.getProduct(content.productId?split("--")?reverse[0]!""))!]
[/#if]
[#assign divClass = content.divClass!]
[#if content.productImage?has_content]
    [#assign productImageUrl = (damfn.getRendition(content.productImage!"", "original").getLink())!]
[/#if]


[#-------------- RENDERING --------------]
[#if product?has_content]
    <div [#if divClass?has_content]class="${divClass}"[/#if]>
        <a href="${detailPageLink}?productId=${product.getId()}">
            <img src="${productImageUrl!product.getMasterVariant().getImages()[0].getUrl()!""}" alt="${product.getName().get(ctfn.getLanguage())!""}">
        </a>
        <p>
            <a href="${detailPageLink}?productId=${product.getId()}">${product.getName().get(ctfn.getLanguage())!""}</a>
            <span>${content.productDescription!(product.getDescription().get(ctfn.getLanguage()))!""}</span>
        </p>
        <div>
            [#assign price = ctfn.getPriceToShow(product.getMasterVariant().getPrices()).getValue()!]
            <span><strong>${(price.getCurrency())!} ${(price.getNumber())!i18n['ctProduct.priceNotSet']}</strong></span>
            <a class="addToCart" id="${product.getId()}" variantId="${product.getMasterVariant().getId()}" href="#">${i18n['ctProduct.addToCart']}</a>
        </div>
    </div>
    <script>
        (function () {
            var cartId = "${cartId}";

            $(".addToCart").on("click", function (e) {
                e.preventDefault();
                var productElement = this;
                $.ajax({
                    url: "${ctx.contextPath}/.rest/commercetools/cart/${ctfn.getProjectName()}/${ctfn.getCountryCode()}/${ctfn.getCurrencyCode()}/" + productElement.id + "/" + productElement.getAttribute("variantId") + (cartId === "" ? "" : "?ctCartId=" + cartId),
                    data: {
                        format: "json"
                    },
                    error: function () {
                        console.log("error while retrieving cart");
                    },
                    dataType: 'json',
                    success: function (data) {
                        var cartItemNum = $('#cartItemNum');
                        if(typeof cartItemNum !== 'undefined') {
                            cartItemNum.html((data.lineItems.length + data.customLineItems.length));
                        }
                        cartId = data.id;
                    },
                    type: 'PUT'
                });
            });
        })();
    </script>
[/#if]