[#-------------- INCLUDES AND ASSIGNMENTS --------------]
[#include "macros/breadcrumb.ftl"]
[#assign productId = (ctx.getParameter("productId")?html)!""]
[#assign variantId = (ctx.getParameter("variantId")?html)!0]
[#assign cartId = ctx.getAttribute("ctCartId")!""]

[#-------------- RENDERING --------------]

<div class="divider"></div>
[#if productId?has_content]
    [#assign product = ctfn.getProduct(productId)]
    [#if product?has_content]
        <!-- Item-detail -->
        <div class="container">
            <div class="row">
                [@breadcrumb product.getCategories()[0].getObj() cmsfn.link("website", content.productsPage!"")!"#" /]
                <div class="item-detail col-lg-12 col-md-12 col-sm-12">
                    <div class="row">
                        <img class="col-lg-3 col-md-3 col-sm-4 img-responsive" id="image" src="${ctfn.getVariantOrMaster(product, variantId?number).getImages()[0].getUrl()!""}" alt="${product.getName().get(ctfn.getLanguage())!""}">
                        <div class="col-lg-9 col-md-9 col-sm-8">
                            <div class="row">
                                <div class="col-lg-4 col-md-5 col-sm-5 col-xs-12">
                                    <h1>${product.getName().get(ctfn.getLanguage())!""}</h1>
                                    [#assign sku = ctfn.getVariantOrMaster(product, variantId?number).getSku()!""]
                                    <div class="divider"></div>
                                    <div class="price-info">
                                        [#assign price = ctfn.getPriceToShow(ctfn.getVariantOrMaster(product, variantId?number).getPrices()).getValue()!]
                                        [#if price?has_content]
                                            <span class="currency">${price.getCurrency()}</span>
                                            <span class="price">${price.getNumber()}</span>
                                        [/#if]
                                        <p class="stock-id" id="SKU">[#if sku?has_content]SKU: ${sku}[#else]&nbsp;[/#if]</p>
                                        <div class="size-info">
                                            <label>Size</label>
                                            <ul class="flex-box">
                                                [#assign variantList = ctfn.getListOfAttributeValuesFromProductVariants(product, "size")]
                                                [#list variantList as size]
                                                    [#list size?keys as variantId]
                                                        <li><a variantId="${variantId}" href="#" class="variant [#if variantList[0]?keys[0] == variantId]active[/#if]">${size[variantId]}</a></li>
                                                    [/#list]
                                                [/#list]
                                            </ul>
                                        </div>
                                    </div>
                                    <div class="divider hidden-sm hidden-md hidden-lg"></div>
                                </div>
                                <div class="col-lg-8 col-md-7 col-sm-7 col-xs-12 description">
                                    <h3 class="hidden-xs">Description</h3>
                                    <h3 class="hidden-sm hidden-md hidden-lg mobile"><a href="#">Description<span class="pull-right">+</span></a></h3>
                                    <p class="mob-description">
                                        [#if product.getDescription()??]
                                            ${product.getDescription().get(ctfn.getLanguage())!""}
                                        [/#if]
                                    </p>
                                </div>
                                <div class="buy-group col-lg-12 col-md-12 col-sm-12 col-xs-12">
                                    <div class="row">
                                        <div class="divider hidden-sm hidden-md hidden-lg"></div>
                                        <div>
                                            <input type="number" name="quantity" id="quantity" value="1" min="1" max="100">
                                        </div>
                                        <div>
                                            <button class="btn cta-button addToCart"> ${i18n['ctProduct.addToCart']}</button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    [/#if]
    <script>
        (function () {
            var cartId = "${cartId}";
            var variantId = "${ctfn.getVariantOrMaster(product, variantId?number).getId()}";

            $(".variant").on("click", function (e) {
                e.preventDefault();
                var productElement = this;
                $.ajax({
                    url: "${ctx.contextPath}/.rest/ctVariant/${ctfn.getProjectName()}/${productId}/" + productElement.getAttribute("variantId"),
                    data: {
                        format: "json"
                    },
                    error: function () {
                        console.log("error while retrieving product variant");
                    },
                    dataType: 'json',
                    success: function (data) {
                        $('#image').attr("src", data.images[0].url);
                        $('#SKU').html(data.sku == null ? "&nbsp;" : "SKU: " + data.sku);
                        $('.variant').removeClass('active');
                        variantId = data.id;
                        $(".variant[variantId=" + variantId + "]").addClass('active');
                    },
                    type: 'GET'
                });
            });

            $(".addToCart").on("click", function (e) {
                e.preventDefault();
                var productElement = this;
                $.ajax({
                    url: "${ctx.contextPath}/.rest/ctCart/${ctfn.getProjectName()}/${ctfn.getCountryCode()}/${ctfn.getCurrencyCode()}/${productId}/" + variantId + "?ctProductQuantity=" + ($('#quantity').val() === "" ? "1" : $('#quantity').val()) + (cartId === "" ? "" : "&ctCartId=" + cartId),
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
