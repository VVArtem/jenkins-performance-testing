const selectors = require('../config/selectors'); 

class CartPage {
    constructor(page) {
        this.page = page;
    }

    async verifyLoaded() {
        await this.page.waitForSelector(selectors.cart.checkoutOrderBtn);
        console.log("Cart Page loaded successfully");
    }

    async checkoutOrder() {        
        await Promise.all([
            this.page.waitForNavigation(),
            this.page.click(selectors.cart.checkoutOrderBtn),
        ]);
    }
}
module.exports = CartPage;