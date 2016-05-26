[#-------------- ASSIGNMENTS --------------]
[#assign shoppingCart = ctfn.getCart()!]
[#assign teaserLink = cmsfn.link("website", content.productDetailPage!"")!""]

[#-------------- RENDERING --------------]
<div ${model.style!} >
    <h3>Shopping cart</h3>

    <div>
        <p>Description</p>
        <p>Quantity</p>
        <p>Price</p>
        <p>Total</p>
    </div>
[#list shoppingCart.getLineItems() as product]
    <div id="${product.getId()}">
        <div>
            <a href="${teaserLink}?productId=${product.getProductId()}">
                <img src="${product.getVariant().getImages()[0].getUrl()!""}"
                     alt="${product.getName().get(ctfn.getLanguage())!""}" class="img-responsive img-thumbnail" alt="">
            </a>
            <h4>${product.getName().get(ctfn.getLanguage())!""}</h4>
            <h5>SLUG: ${product.getProductSlug().get(ctfn.getLanguage())!""}</h5>
            <h5>SKU: ${product.getVariant().getSku()!""}</h5>
        </div>
        <input class="quantity" productId="${product.getId()}" value="${product.getQuantity()}" min="1" max="50" class="text-center pull-right">
        <p><span>${product.getPrice().getValue().getCurrency()}&nbsp;</span>${product.getPrice().getValue().getNumber()}</p>
        <p><span>${product.getTotalPrice().getCurrency()}&nbsp;</span><span class="item-total">${product.getTotalPrice().getNumber()}</span><span><a productId="${product.getId()}" href="#" class="remove pull-right">&#10005</a></span>
        </p>
    </div>
[/#list]
    <div>
        <div>
            <span>Subtotal</span>
        [#if shoppingCart.getTaxedPrice()?has_content]
            <span>${shoppingCart.getTaxedPrice().getTotalNet().getCurrency()}  <span id="cart-subtotal">${shoppingCart.getTaxedPrice().getTotalNet().getNumber()}</span></span>
        [/#if]
        </div>
        <div>
            <span>Shipping</span>
            <span>${(shoppingCart.getShippingInfo().getPrice().getCurrency())!""} <span id="cart-shipping">${(shoppingCart.getShippingInfo().getPrice().getNumber())!"0"}</span></span>
        </div>
        <div>
            <span>Sales Tax</span>
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
            <span>Total</span>
            <span>${shoppingCart.getTotalPrice().getCurrency()} <span id="cart-total">${shoppingCart.getTotalPrice().getNumber()}</span></span>
        </div>
    </div>
</div><!-- end ${model.style!} -->

<script>
    (function () {
        var cartId = "${shoppingCart.getId()}";

        $(".quantity").on("change", function () {
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
