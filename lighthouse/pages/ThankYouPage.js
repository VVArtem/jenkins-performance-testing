const selectors = require('../config/selectors'); 

class ThankYouPage {
    constructor(page) {
        this.page = page;
    }

    async verifyLoaded() {
        await this.page.waitForSelector("h1.entry-title");

        const text = await this.page.$eval("h1.entry-title", el => el.innerText);

        if (!text.includes("Thank You")) {
            throw new Error(`Text containing "Thank you" not found."`);
        }
        console.log("Thank You Page loaded successfully");
    }
}
module.exports = ThankYouPage;