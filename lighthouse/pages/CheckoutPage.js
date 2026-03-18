const selectors = require('../config/selectors'); 

class CheckoutPage {
    constructor(page) {
        this.page = page;
    }

    async verifyLoaded() {
        await this.page.waitForSelector(selectors.checkout.placeOrderBtn);
        console.log("Checkout Page loaded successfully");
    }

    async fillOrderFields(userData) {
        await this.page.waitForSelector(selectors.checkout.fields.name);

        await this.page.type(selectors.checkout.fields.name, userData.name);
        await this.page.type(selectors.checkout.fields.address, userData.address);
        await this.page.type(selectors.checkout.fields.city, userData.city);
        await this.page.type(selectors.checkout.fields.postal, userData.postal);

        await this.page.select(selectors.checkout.fields.country, userData.country);

        await this.page.type(selectors.checkout.fields.phone, userData.phone);
        await this.page.type(selectors.checkout.fields.email, userData.email);
    }

    async placeOrder() {        
        await Promise.all([
            this.page.waitForNavigation(),
            this.page.click(selectors.checkout.placeOrderBtn),
        ]);
    }
}
module.exports = CheckoutPage;