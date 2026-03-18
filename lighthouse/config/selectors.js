module.exports = {
    home: {
        categoryLinks: "div.al_archive:not(.priced) .pseudo-a",
    },
    category: {
        productLinks: ".al_archive.priced a",
    },
    product: {
        addToCartBtn: ".add-to-shopping-cart button",
        goToCartBtn: ".add_to_cart_form_container .success a",
    },
    cart: {
        checkoutOrderBtn: "input.to_cart_submit",
    },
    checkout: {
        placeOrderBtn: "input[value='Place Order']",
        fields: {
            name: "input[name='cart_name']",
            address: "input[name='cart_address']",
            postal: "input[name='cart_postal']",
            city: "input[name='cart_city']",
            country: "select[name='cart_country']",
            phone: "input[name='cart_phone']",
            email: "input[name='cart_email']"
        }
    },
};
