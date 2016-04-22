[#macro productTeaserList products currentPage perPage productDetailPage=""]
    [#-------------- INCLUDES AND ASSIGNMENTS --------------]
    [#include "./productPager.ftl"]
    [#assign teaserLink = cmsfn.link("website", productDetailPage)]
    [#assign cartId = ctx.getAttribute("ctCartId")!""]

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
                [#assign price = (ctfn.getPriceToShow(product.getMasterVariant().getPrices()).getValue())!]
                <span><strong>${(price.getCurrency())!} ${(price.getNumber())!i18n['ctProduct.priceNotSet']}</strong></span>
                <a class="addToCart" id="${product.getId()}" variantId="${product.getMasterVariant().getId()}" href="#">${i18n['ctProduct.addToCart']}</a>
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