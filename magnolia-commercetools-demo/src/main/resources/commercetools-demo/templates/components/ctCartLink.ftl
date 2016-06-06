[#-------------- INCLUDES AND ASSIGNMENTS --------------]
[#assign cartItemNum = ctfn.getNumberOfItemsInCart()]

[#-------------- RENDERING --------------]
<li>
    <a href="${cmsfn.link("website", content.cartPage!"")!"#"}">My cart (<span id="cartItemNum">${cartItemNum}</span>) items</a>
</li>
