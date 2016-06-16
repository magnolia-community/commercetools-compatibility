[#-------------- INCLUDES AND ASSIGNMENTS --------------]
[#include "macros/breadcrumb.ftl"]
[#assign productId = (ctx.getParameter("productId")?html)!""]
[#assign variantId = (ctx.getParameter("variantId")?html)!0]
[#assign cartId = ctx.getAttribute(ctfn.getProjectName() + "_ctCartId")!""]

[#-------------- RENDERING --------------]

[#if productId?has_content]
    [#assign product = ctfn.getProduct(productId)]
    [#if product?has_content]
        <!-- ProductDetail -->
        [@breadcrumb product.getCategories()[0].getObj() cmsfn.link("website", content.productsPage!"")!"#" /]
        <main>
            <img id="image" src="${ctfn.getVariantOrMaster(product, variantId?number).getImages()[0].getUrl()!""}" alt="${product.getName().get(ctfn.getLanguage())!""}">
            <h1>${product.getName().get(ctfn.getLanguage())!""}</h1>
            <h2>${product.getSlug().get(ctfn.getLanguage())!""}</h2>
            <p>
                [#if product.getDescription()??]
                    ${product.getDescription().get(ctfn.getLanguage())!""}
                [/#if]
            </p>
        </main>
        <aside>
            <h3>${product.getName().get(ctfn.getLanguage())!""}</h3>
            <h4>
                [#assign price = ctfn.getPriceToShow(ctfn.getVariantOrMaster(product, variantId?number).getPrices()).getValue()!]
                [#if price?has_content]
                    <span>${price.getCurrency()}</span>
                    <span>${price.getNumber()}</span>
                [/#if]
            </h4>
            <div>
                [#assign sku = ctfn.getVariantOrMaster(product, variantId?number).getSku()!""]
                <span id="SKU">[#if sku?has_content]SKU: ${sku}[/#if]</span>
            </div>
            <!-- List variants and switch logic -->
            <div class="size-info">
                <label>Size</label>
                <ul>
                    [#list ctfn.getListOfAttributeValuesFromProductVariants(product, "size") as size]
                        [#list size?keys as variantId]
                            <li><a variantId="${variantId}" href="#" class="variant">${size[variantId]}</a></li>
                        [/#list]
                    [/#list]
                </ul>
            </div>
            <!-- end variants -->
            <input type="number" id="quantity" name="quantity" value="1" min="1" max="100">
            <button class="addToCart"> ${i18n['ctProduct.addToCart']}</button>
        </aside>
    [/#if]
    <script>
        (function () {
            var cartId = "${cartId}";
            var variantId = "${ctfn.getVariantOrMaster(product, variantId?number).getId()}";

            $(".variant").on("click", function (e) {
                e.preventDefault();
                var productElement = this;
                $.ajax({
                    url: "${ctx.contextPath}/.rest/commercetools/variant/${ctfn.getProjectName()}/${productId}/" + productElement.getAttribute("variantId"),
                    data: {
                        format: "json"
                    },
                    error: function () {
                        console.log("error while retrieving product variant");
                    },
                    dataType: 'json',
                    success: function (data) {
                        $('#image').attr("src", data.images[0].url);
                        $('#SKU').html(data.sku == null ? "" : "SKU: " + data.sku);
                        variantId = data.id;
                    },
                    type: 'GET'
                });
            });

            $(".addToCart").on("click", function (e) {
                e.preventDefault();
                var productElement = this;
                $.ajax({
                    url: "${ctx.contextPath}/.rest/commercetools/cart/${ctfn.getProjectName()}/${ctfn.getCountryCode()}/${ctfn.getCurrencyCode()}/${productId}/" + variantId + "?ctProductQuantity=" + ($('#quantity').val() === "" ? "1" : $('#quantity').val()) + (cartId === "" ? "" : "&ctCartId=" + cartId),
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
