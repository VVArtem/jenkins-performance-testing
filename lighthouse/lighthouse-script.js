const fs = require('fs')
const path = require('path');
const puppeteer = require('puppeteer')

const HomePage = require('./pages/HomePage');
const ProductListPage = require('./pages/ProductListPage');
const ProductDetailedPage = require('./pages/ProductDetailedPage');
const CartPage = require('./pages/CartPage');
const CheckoutPage = require('./pages/CheckoutPage');
const ThankYouPage = require('./pages/ThankYouPage');

const CsvFeeder = require('./utils/CsvFeeder');

async function captureReport() {
    const { startFlow } = await import('lighthouse');
    const { default: desktopConfig } = await import('lighthouse/core/config/desktop-config.js');

    const users = CsvFeeder.loadUsers(path.resolve('data/users.csv'));
    const randomUser = users[Math.floor(Math.random() * users.length)];

    const browser = await puppeteer.launch({
        executablePath: '/usr/bin/google-chrome',
        args: [
            '--no-sandbox', 
            '--disable-setuid-sandbox', 
            '--disable-dev-shm-usage',
            '--disable-gpu'
        ],

        });

    const page = await browser.newPage()

    const homePage = new HomePage(page);
    const categoryPage = new ProductListPage(page);
    const productPage = new ProductDetailedPage(page);
    const cartPage = new CartPage(page);
    const checkoutPage = new CheckoutPage(page);
    const thankYouPage = new ThankYouPage(page);

    const baseUrl = 'http://localhost:';

    const flow = await startFlow(page, {
        name: 'Essentials frontend performance',
        config: desktopConfig, 
        configContext: {
            settingsOverrides: {
                screenEmulation: {
                    disabled: true,
                },
                throttlingMethod: "provided",
                onlyCategories: ['performance'],
            },
        },
    });


    try {
        // 1: Open the home page
        await flow.navigate(baseUrl, { stepName: 'Home Page Load' });
        await homePage.verifyLoaded();

        // 2: Go to a random category page
        await flow.startNavigation({ stepName: 'Navigate to Category' });
            await homePage.clickRandomCategory();
        await flow.endNavigation();
        
        await categoryPage.verifyLoaded();

        // 3: Select and open a random product
        await flow.startNavigation({ stepName: 'Navigate to Product' });
            await categoryPage.clickRandomProduct();
        await flow.endNavigation();
        
        await productPage.verifyLoaded();

        // 4: Add the selected product to the cart
        await flow.startTimespan({ stepName: 'Add to Cart' });
            await productPage.addToCart();
        await flow.endTimespan();

        // 5: Navigate to the shopping cart page
        await flow.startNavigation({ stepName: 'Go to Cart' });
            await productPage.goToCart();
        await flow.endNavigation();
        
        await cartPage.verifyLoaded();

        // 6: Proceed to the checkout screen
        await flow.startNavigation({ stepName: 'Go to Checkout' });
            await cartPage.checkoutOrder();
        await flow.endNavigation();
        
        await checkoutPage.verifyLoaded();

        // 7: Fill in the shipping and billing info
        await flow.startTimespan({ stepName: 'Fill Checkout Form' });
            await checkoutPage.fillOrderFields(randomUser);
        await flow.endTimespan();

        // 8: Click the button to finish the order
        await flow.startNavigation({ stepName: 'Place Order' });
            await checkoutPage.placeOrder();
        await flow.endNavigation();

        // 9: Verify the success page is displayed
        await thankYouPage.verifyLoaded();

    } catch (error) {
        // Log errors and save a debug screenshot if something fails
        console.error('Error in flow:', error);
        await page.screenshot({ path: 'error.png', fullPage: true });
    }


    const iteration = process.env.ITERATION || '1';
    const reportPath = __dirname + `user-flow-${iteration}.report.html`; //folder for html report
    const reportPathJson = __dirname + '/user-flow.report.json'; //folder for json report

    const report = await flow.generateReport();
    const flowResult = await flow.createFlowResult();
    const reportJson = JSON.stringify(flowResult).replace(/</g, '\\u003c').replace(/\u2028/g, '\\u2028').replace(/\u2029/g, '\\u2029');

    fs.writeFileSync(reportPath, report); //write html report
    // fs.writeFileSync(reportPathJson, reportJson); //write json report

    await browser.close();
}

captureReport();