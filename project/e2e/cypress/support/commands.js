Cypress.Commands.add('loginAdmin', () => {
    cy.fixture('settings').then(settings => {
        cy.visit(settings.baseUrl);
        cy.contains('a', 'Login').click();
        cy.get('input[name="username"]').type(settings.adminUser);
        cy.get('input[name="password"]').type(settings.adminPw);
        cy.contains('button', 'Login').click();
    })
})

Cypress.Commands.add('logout', () => {
    cy.fixture('settings').then(() => {
        cy.contains('a', 'Logout').click();
    })
})

Cypress.Commands.add('register', () => {
    cy.fixture('settings').then(settings => {
        cy.visit(settings.baseUrl);
        // cy.wait(6000); //TODO: remove once the tickets are available/no error pops up
        cy.contains('a', 'Registration').click();
        cy.get('input[name="firstName"]').type(settings.newUserLN);
        cy.get('input[name="lastName"]').type(settings.newUserLN);
        cy.get('input[name="username"]').type(settings.newUser);
        cy.get('input[name="password"]').type(settings.newUserPw);
        cy.get('input[name="confirmation"]').type(settings.newUserPw);
        cy.contains('button', 'Register').click();
    })
})

Cypress.Commands.add('createMessage', (msg) => {
    cy.fixture('settings').then(settings => {
        cy.contains('a', 'News').click();
        cy.contains('button', 'Add news').click();
        cy.get('input[name="title"]').type('title' +  msg);
        cy.get('textarea[name="summary"]').type('summary' +  msg);
        cy.get('textarea[name="text"]').type('text' +  msg);
        cy.get('button[id="add-msg"]').click();
        // cy.get('button[id="close-modal-btn"]').click();

        cy.contains('title' +  msg).should('be.visible');
        cy.contains('summary' +  msg).should('be.visible');
    })
})
