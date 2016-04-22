[#-------------- ASSIGNMENTS --------------]
[#assign shoppingCart = ctfn.getCart()!]
[#assign teaserLink = cmsfn.link("website", content.productDetailPage!"")!""]
[#assign changesEnabled = content.changesEnabled!false]

[#-------------- RENDERING --------------]
<div class="divider"></div>
<div class="container" >
    <h3>Shopping cart</h3>
    <div class="row">
        <div class="notation hidden-xs">
            <p class="description col-lg-7 col-md-7 col-sm-7">Description</p>
            <p class="quantity text-right col-lg-1 col-md-1 col-sm-1">Quantity</p>
            <p class="price text-right col-lg-2 col-md-2 col-sm-2">Price</p>
            <p class="total text-right col-lg-2 col-md-2 col-sm-2">Total</p>
        </div>
    </div>
    <div class="divider"></div>
    [#list shoppingCart.getLineItems() as product]
        <div class="row">
            <div class="cart">
                <div class="row single-cart-item" id="${product.getId()}">
                    <div class="description col-lg-7 col-md-7 col-sm-7 col-xs-12">
                        <div class="product-image col-lg-2 col-md-2 col-sm-2 col-xs-3">
                            <a href="${teaserLink}?productId=${product.getProductId()}">
                                <img src="${product.getVariant().getImages()[0].getUrl()!""}"
                                     alt="${product.getName().get(ctfn.getLanguage())!""}" class="img-responsive img-thumbnail">
                            </a>
                        </div>
                        <div class="product-info col-lg-10 col-md-10 col-sm-10 col-xs-9">
                            <h4>${product.getName().get(ctfn.getLanguage())!""}</h4>
                            <h5 class="text-muted">SKU: ${product.getVariant().getSku()!""}</h5>
                            <p class="description">${(product.getDescription().get(ctfn.getLanguage()))!""}</p>
                        </div>
                    </div>
                    <div class="quantity col-lg-1 col-md-1 col-sm-1 col-xs-3"><input type="number" class="quantity-input text-center pull-right" productId="${product.getId()}" value="${product.getQuantity()}" min="1" max="50" [#if !changesEnabled]disabled[/#if]></div>
                    <div class="price-item col-lg-2 cold-md-2 col-sm-2 col-xs-4 text-right"><p><span>${product.getPrice().getValue().getCurrency()}&nbsp;</span>${product.getPrice().getValue().getNumber()}</p></div>
                    <div class="price-item-total col-lg-2 col-md-2 col-sm-2 col-xs-5 text-right"><p>
                        <span>${product.getTotalPrice().getCurrency()}&nbsp;</span><span class="item-total">${product.getTotalPrice().getNumber()}</span> <span>[#if changesEnabled]<a productId="${product.getId()}" href="#" class="remove pull-right">&#10005;</a>[#else]&nbsp;[/#if]</span>
                    </p>
                    </div>
                </div>
            </div>
        </div>
    [/#list]
    <div class="divider"></div>
    <div class="row">
        <div class="total-price clearfix">
            <div class="col-sm-10 col-xs-7">
                <div class="text-right subtotal">
                    <span class="subtotal-title">Subtotal</span>
                </div>

                <div class="text-right delivery-info">
                    <span class="delivery-info-title">Shipping</span>
                </div>
                <hr class="total-divider">
                <div class="text-right">
                    <span>Sales Tax</span>
                </div>
                <div class="text-right">
                    <span class="order-total">Total</span>
                </div>
            </div>
            <div class="col-sm-2 col-xs-5 text-right">
                <div>
                    [#if shoppingCart.getTaxedPrice()?has_content]
                        <span>${shoppingCart.getTaxedPrice().getTotalNet().getCurrency()}  <span id="cart-subtotal">${shoppingCart.getTaxedPrice().getTotalNet().getNumber()}</span></span>
                    [/#if]
                </div>

                <div>
                    <span>${(shoppingCart.getShippingInfo().getPrice().getCurrency())!""} <span id="cart-shipping">${(shoppingCart.getShippingInfo().getPrice().getNumber())!"0"}</span></span>
                </div>
                <hr>
                <div>
                    [#if shoppingCart.getTaxedPrice()?has_content]
                        [#if shoppingCart.getTaxedPrice().getTaxPortions()?has_content]
                            <div id="tax-portions">
                                [#list shoppingCart.getTaxedPrice().getTaxPortions() as taxPortion]
                                    <span>${taxPortion.getRate()} ${taxPortion.getAmount().getCurrency()} ${taxPortion.getAmount().getNumber()}</span>
                                [/#list]
                            </div>
                        [/#if]
                    [/#if]
                </div>
                <div>
                    <span class="order-total">${shoppingCart.getTotalPrice().getCurrency()} <span id="cart-total">${shoppingCart.getTotalPrice().getNumber()}</span></span>
                </div>
            </div>
        </div>
    </div>
    <div class="divider"></div>
</div><!-- end ${model.style!} -->

[#if changesEnabled]
<script>
    (function () {
        var cartId = "${shoppingCart.getId()}";

        $(".quantity-input").on("change", function () {
            var productElement = this;
            $.ajax({
                url: "${ctx.contextPath}/.rest/ctCart/${ctfn.getProjectName()}/${ctfn.getCountryCode()}/${ctfn.getCurrencyCode()}/" + cartId + "/" + productElement.getAttribute("productId") + "/" + productElement.value,
                data: {
                    format: "json"
                },
                error: function () {
                    console.log("error while retrieving cart");
                },
                dataType: 'json',
                success: function (data) {
                    cartId = data.id;
                    var lineItem;
                    var productId = productElement.getAttribute("productId");
                    for (var i = 0; i < data.lineItems.length; i++) {
                        if (data.lineItems[i].id == productId) {
                            lineItem = data.lineItems[i];
                        }
                    }
                    $("#" + productId).find(".item-total").html(lineItem.totalPrice.number);
                    setCartPrices(data);
                },
                type: 'POST'
            });
        });

        $(".remove").on("click", function (e) {
            e.preventDefault();
            var productElement = this;
            $.ajax({
                url: "${ctx.contextPath}/.rest/ctCart/${ctfn.getProjectName()}/${ctfn.getCountryCode()}/${ctfn.getCurrencyCode()}/" + cartId + "/" + productElement.getAttribute("productId"),
                data: {
                    format: "json"
                },
                error: function () {
                    console.log("error while retrieving cart");
                },
                dataType: 'json',
                success: function (data) {
                    cartId = data.id;
                    $("#" + productElement.getAttribute("productId")).remove();
                    setCartPrices(data);
                    $('#cartItemNum').html((data.lineItems.length + data.customLineItems.length));
                },
                type: 'DELETE'
            });
        });

        function setCartPrices(cart) {
            $("#cart-subtotal").html(cart.taxedPrice.totalNet.number);
            $("#cart-shipping").html(cart.shippingInfo != null ? cart.shippingInfo.price.number : "0");
            $("#cart-total").html(cart.totalPrice.number);
            var taxPortionsHtml = "";
            cart.taxedPrice.taxPortions.forEach(
                function (item, index) {
                    taxPortionsHtml += "<span>" + item.rate + " " + item.amount.currency.currencyCode + " " + item.amount.number + "</span>";
                }
            )
            $("#tax-portions").html(taxPortionsHtml);
        }
    })();
</script>
[/#if]
