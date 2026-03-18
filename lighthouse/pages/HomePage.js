const selectors = require('../config/selectors'); 

class HomePage {
    constructor(page) {
        this.page = page;
    }

    async verifyLoaded() {
        await this.page.waitForSelector(selectors.home.categoryLinks);
        console.log("Home Page loaded successfully");
    }

    async clickRandomCategory() {
        const links = await this.page.$$(selectors.home.categoryLinks)

        if (links.length === 0) {
            throw new Error(`No categories found. Selector: ${selectors.home.categoryLinks}`);
        }
                
        const randomLink = links[Math.floor(Math.random() * links.length)];
        
        await Promise.all([
            this.page.waitForNavigation(),
            this.page.evaluate(el => el.click(), randomLink)        
        ]);
    }
}
module.exports = HomePage;