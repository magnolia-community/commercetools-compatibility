[#macro productTeaserList products productDetailPage=""]
    [#-------------- INCLUDES AND ASSIGNMENTS --------------]
    [#assign teaserLink = cmsfn.link("website", productDetailPage)]
    [#assign cartId = ctx.getAttribute("ctCartId")!""]

    [#-------------- RENDERING --------------]
    [#list products.getResults() as product]
        <!-- ProductDetail -->
        <div class="item">
            <a href="${teaserLink}?productId=${product.getId()}"><div class="item-overlay"></div></a>
            <img src="${product.getMasterVariant().getImages()[0].getUrl()!""}" alt="${product.getName().get(ctfn.getLanguage())!""}" class="img-responsive">
            <p class="item-name text-center"><a href="${teaserLink}?productId=${product.getId()}">${product.getName().get(ctfn.getLanguage())!""}</a></p>
            [#assign priceToShow=ctfn.getPriceToShow(product.getMasterVariant().getPrices())!i18n['ctProduct.priceNotSet']]
            <div>
                [#assign price = (ctfn.getPriceToShow(product.getMasterVariant().getPrices()).getValue())!]
                <span class="currency"><strong>${(price.getCurrency())!}</span> <span class="price">${(price.getNumber())!i18n['ctProduct.priceNotSet']}</strong></span>
                <a href="#" class="buy pull-right glyphicon glyphicon-shopping-cart addToCart" id="${product.getId()}" variantId="${product.getMasterVariant().getId()}"></a>
            </div>
        </div>
    [/#list]

    <script>
        (function () {
            var cartId = "${cartId}";

            $(".addToCart").on("click", function (e) {
                e.preventDefault();
                var productElement = this;
                $.ajax({
                    url: "${ctx.contextPath}/.rest/ctCart/${ctfn.getProjectName()}/${ctfn.getCountryCode()}/${ctfn.getCurrencyCode()}/" + productElement.id + "/" + productElement.getAttribute("variantId") + (cartId === "" ? "" : "?ctCartId=" + cartId),
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
[/#macro]
