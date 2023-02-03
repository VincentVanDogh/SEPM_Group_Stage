// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

export const environment = {
  production: false,
  backendUrl: 'http://localhost:8080/',
  ticketUrl: 'http://localhost:8080/api/v1/ticket/',
  ticketOrderUrl: 'http://localhost:8080/api/v1/ticket_order/',
  ticketOrderOverviewUrl: 'http://localhost:8080/api/v1/ticket_order/overview',
  ticketsPurchaseUrl: 'http://localhost:8080/api/v1/tickets/buy',
  merchArticleUrl: 'http://localhost:8080/api/v1/merch_article',
  merchPurchaseUrl: 'http://localhost:8080/api/v1/merch_purchase',
  merchShoppingCart: 'http://localhost:8080/api/v1/merch_purchase?purchased=false',
  merchComplete: 'http://localhost:8080/api/v1/merch_purchase?purchased=true',
  shoppingCart: 'http://localhost:8080/api/v1/shopping_cart'
};

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/plugins/zone-error';  // Included with Angular CLI.
