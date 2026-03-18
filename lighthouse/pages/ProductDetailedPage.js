const selectors = require('../config/selectors'); 

class ProductDetailedPage {
    constructor(page) {
        this.page = page;
    }

    async verifyLoaded() {
        await this.page.waitForSelector(selectors.product.addToCartBtn);
        console.log("Product Detailed Page loaded successfully");
    }

    async addToCart() {
        await this.page.click(selectors.product.addToCartBtn);
        await this.page.waitForSelector(selectors.product.goToCartBtn);
    }

    async goToCart() {        
        await this.page.click(selectors.product.goToCartBtn),
        await this.page.waitForSelector(selectors.cart.checkoutOrderBtn, { timeout: 30000 });
    }
}
module.exports = ProductDetailedPage;