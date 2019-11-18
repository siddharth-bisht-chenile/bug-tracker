import { browser, element, by, protractor } from 'protractor';

import NavBarPage from './../../page-objects/navbar-page';
import SignInPage from './../../page-objects/signin-page';
import TicketComponentsPage, { TicketDeleteDialog } from './ticket.page-object';
import TicketUpdatePage from './ticket-update.page-object';
import { waitUntilDisplayed, waitUntilHidden } from '../../util/utils';

const expect = chai.expect;

describe('Ticket e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let ticketComponentsPage: TicketComponentsPage;
  let ticketUpdatePage: TicketUpdatePage;
  /* let ticketDeleteDialog: TicketDeleteDialog; */

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.waitUntilDisplayed();

    await signInPage.username.sendKeys('admin');
    await signInPage.password.sendKeys('admin');
    await signInPage.loginButton.click();
    await signInPage.waitUntilHidden();
    await waitUntilDisplayed(navBarPage.entityMenu);
  });

  it('should load Tickets', async () => {
    await navBarPage.getEntityPage('ticket');
    ticketComponentsPage = new TicketComponentsPage();
    expect(await ticketComponentsPage.getTitle().getText()).to.match(/Tickets/);
  });

  it('should load create Ticket page', async () => {
    await ticketComponentsPage.clickOnCreateButton();
    ticketUpdatePage = new TicketUpdatePage();
    expect(await ticketUpdatePage.getPageTitle().getAttribute('id')).to.match(/jhipDefectTrackerApp.ticket.home.createOrEditLabel/);
    await ticketUpdatePage.cancel();
  });

  /*  it('should create and save Tickets', async () => {
        async function createTicket() {
            await ticketComponentsPage.clickOnCreateButton();
            await ticketUpdatePage.setTitleInput('title');
            expect(await ticketUpdatePage.getTitleInput()).to.match(/title/);
            await ticketUpdatePage.setDescriptionInput('description');
            expect(await ticketUpdatePage.getDescriptionInput()).to.match(/description/);
            await ticketUpdatePage.setDueDateInput('01/01/2001' + protractor.Key.TAB + '02:30AM');
            expect(await ticketUpdatePage.getDueDateInput()).to.contain('2001-01-01T02:30');
            const selectedDone = await ticketUpdatePage.getDoneInput().isSelected();
            if (selectedDone) {
                await ticketUpdatePage.getDoneInput().click();
                expect(await ticketUpdatePage.getDoneInput().isSelected()).to.be.false;
            } else {
                await ticketUpdatePage.getDoneInput().click();
                expect(await ticketUpdatePage.getDoneInput().isSelected()).to.be.true;
            }
            // ticketUpdatePage.labelSelectLastOption();
            await ticketUpdatePage.projectSelectLastOption();
            await ticketUpdatePage.assignedToSelectLastOption();
            await waitUntilDisplayed(ticketUpdatePage.getSaveButton());
            await ticketUpdatePage.save();
            await waitUntilHidden(ticketUpdatePage.getSaveButton());
            expect(await ticketUpdatePage.getSaveButton().isPresent()).to.be.false;
        }

        await createTicket();
        await ticketComponentsPage.waitUntilLoaded();
        const nbButtonsBeforeCreate = await ticketComponentsPage.countDeleteButtons();
        await createTicket();

        await ticketComponentsPage.waitUntilDeleteButtonsLength(nbButtonsBeforeCreate + 1);
        expect(await ticketComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1);
    }); */

  /*  it('should delete last Ticket', async () => {
        await ticketComponentsPage.waitUntilLoaded();
        const nbButtonsBeforeDelete = await ticketComponentsPage.countDeleteButtons();
        await ticketComponentsPage.clickOnLastDeleteButton();

        const deleteModal = element(by.className('modal'));
        await waitUntilDisplayed(deleteModal);

        ticketDeleteDialog = new TicketDeleteDialog();
        expect(await ticketDeleteDialog.getDialogTitle().getAttribute('id')).to.match(/jhipDefectTrackerApp.ticket.delete.question/);
        await ticketDeleteDialog.clickOnConfirmButton();

        await ticketComponentsPage.waitUntilDeleteButtonsLength(nbButtonsBeforeDelete - 1);
        expect(await ticketComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
    }); */

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
