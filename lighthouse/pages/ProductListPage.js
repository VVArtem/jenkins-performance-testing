const selectors = require('../config/selectors'); 

class ProductListPage {
    constructor(page) {
        this.page = page;
    }

    async verifyLoaded() {
        await this.page.waitForSelector(selectors.category.productLinks);
        console.log("Product List Page loaded successfully");
    }

    async clickRandomProduct() {
        const products = await this.page.$$(selectors.category.productLinks)

        if (products.length === 0) {
            throw new Error(`No products found. Selector: ${selectors.category.productLinks}`);
        }
                
        const randomProduct = products[Math.floor(Math.random() * products.length)];

        await randomProduct.evaluate(el => el.click());
        await this.page.waitForNavigation();
    }
}
module.exports = ProductListPage;