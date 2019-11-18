import { element, by, ElementFinder } from 'protractor';

export default class TicketUpdatePage {
  pageTitle: ElementFinder = element(by.id('jhipDefectTrackerApp.ticket.home.createOrEditLabel'));
  saveButton: ElementFinder = element(by.id('save-entity'));
  cancelButton: ElementFinder = element(by.id('cancel-save'));
  titleInput: ElementFinder = element(by.css('input#ticket-title'));
  descriptionInput: ElementFinder = element(by.css('input#ticket-description'));
  dueDateInput: ElementFinder = element(by.css('input#ticket-dueDate'));
  doneInput: ElementFinder = element(by.css('input#ticket-done'));
  labelSelect: ElementFinder = element(by.css('select#ticket-label'));
  projectSelect: ElementFinder = element(by.css('select#ticket-project'));
  assignedToSelect: ElementFinder = element(by.css('select#ticket-assignedTo'));

  getPageTitle() {
    return this.pageTitle;
  }

  async setTitleInput(title) {
    await this.titleInput.sendKeys(title);
  }

  async getTitleInput() {
    return this.titleInput.getAttribute('value');
  }

  async setDescriptionInput(description) {
    await this.descriptionInput.sendKeys(description);
  }

  async getDescriptionInput() {
    return this.descriptionInput.getAttribute('value');
  }

  async setDueDateInput(dueDate) {
    await this.dueDateInput.sendKeys(dueDate);
  }

  async getDueDateInput() {
    return this.dueDateInput.getAttribute('value');
  }

  getDoneInput() {
    return this.doneInput;
  }
  async labelSelectLastOption() {
    await this.labelSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async labelSelectOption(option) {
    await this.labelSelect.sendKeys(option);
  }

  getLabelSelect() {
    return this.labelSelect;
  }

  async getLabelSelectedOption() {
    return this.labelSelect.element(by.css('option:checked')).getText();
  }

  async projectSelectLastOption() {
    await this.projectSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async projectSelectOption(option) {
    await this.projectSelect.sendKeys(option);
  }

  getProjectSelect() {
    return this.projectSelect;
  }

  async getProjectSelectedOption() {
    return this.projectSelect.element(by.css('option:checked')).getText();
  }

  async assignedToSelectLastOption() {
    await this.assignedToSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async assignedToSelectOption(option) {
    await this.assignedToSelect.sendKeys(option);
  }

  getAssignedToSelect() {
    return this.assignedToSelect;
  }

  async getAssignedToSelectedOption() {
    return this.assignedToSelect.element(by.css('option:checked')).getText();
  }

  async save() {
    await this.saveButton.click();
  }

  async cancel() {
    await this.cancelButton.click();
  }

  getSaveButton() {
    return this.saveButton;
  }
}
