declare namespace Cypress{
    interface Chainable {
        /**
         * Navigate to main page and login as admin
         */
        loginAdmin();

        /**
         * Logout fom the website
         */
        logout();

        /**
         * Navigate to main page and register a new account
         */
        register();

        /**
         * Creates a message with a given text
         * @param msg the text of the created message
         */
        createMessage(msg: string);
    }
}
