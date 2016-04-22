[#-------------- ASSIGNMENTS --------------]
[#assign productId = (ctx.getParameter("productId")?html)!""]

[#-------------- RENDERING --------------]

[#if productId?has_content]
    [#assign product = ctfn.getProduct(productId)]
    [#if product?has_content]
        <!-- ProductDetail -->
        <!-- breadcrumb for each category, product is in? -->
        <div class="col-lg-8 col-lg-offset-2 col-md-10 col-md-offset-1 col-sm-12 flex-box bc-group btn-group btn-breadcrumb">
            <a href="#" class="bc-button btn btn-default"><i class="glyphicon glyphicon-home"></i></a>
            <a href="#" class="bc-button btn btn-default">Men</a>
            <a href="#" class="bc-button btn btn-default">Fluffy</a>
            <a href="#" class="bc-button btn btn-default">Koalas</a>
        </div>
        <!-- /breadcrumb-->
        <main>
            <img src="${product.getMasterVariant().getImages()[0].getUrl()!""}" alt="${product.getName().get(ctfn.getLanguage())!""}">
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
                [#assign price = ctfn.getPriceToShow(product.getMasterVariant().getPrices())?split(" ")]
                [#if price?has_content]
                    <span>${price[1]}</span>
                    <span>${price[0]}</span>
                [/#if]
            </h4>
            <div>
                [#assign sku = product.getMasterVariant().getSku()!""]
                <span>[#if sku?has_content]SKU: ${sku}[/#if]</span>
            </div>
            <!-- List variants and switch logic -->
            <div class="size-info">
                <label>Size</label>
                <ul class="flex-box">
                    <li><a href="" class="active">XS</a></li>
                    <li><a href="">S</a></li>
                    <li><a href="">M</a></li>
                    <li><a href="">L</a></li>
                    <li><a href="">XL</a></li>
                </ul>
            </div>
            <!-- end variants -->
            <input type="number" name="quantity" value="1" min="1" max="100">
            <button> ${i18n['ctProduct.addToCart']}</button>
        </aside>
    [/#if]
[/#if]