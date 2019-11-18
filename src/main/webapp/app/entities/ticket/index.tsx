import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Ticket from './ticket';
import TicketDetail from './ticket-detail';
import TicketUpdate from './ticket-update';
import TicketDeleteDialog from './ticket-delete-dialog';
import TicketSelf from './ticket-self'

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={TicketUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/self`} component={TicketSelf} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={TicketUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={TicketDetail} />
      <ErrorBoundaryRoute path={match.url} component={Ticket} />
    </Switch>
    <ErrorBoundaryRoute path={`${match.url}/:id/delete`} component={TicketDeleteDialog} />
  </>
);

export default Routes;
