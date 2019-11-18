import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Col, Row, Table } from 'reactstrap';
import { Translate, ICrudGetAllAction, TextFormat, getSortState, IPaginationBaseState, JhiPagination, JhiItemCount } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getMyEntities } from './ticket.reducer';
import { ITicket } from 'app/shared/model/ticket.model';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { ITEMS_PER_PAGE } from 'app/shared/util/pagination.constants';

export interface ITicketProps extends StateProps, DispatchProps, RouteComponentProps<{ url: string }> {}

export type ITicketState = IPaginationBaseState;

export class TicketSelf extends React.Component<ITicketProps, ITicketState> {
  state: ITicketState = {
    ...getSortState(this.props.location, ITEMS_PER_PAGE)
  };

  componentDidMount() {
	this.getMyEntities();
  }

  sort = prop => () => {
    this.setState(
      {
        order: this.state.order === 'asc' ? 'desc' : 'asc',
        sort: prop
      },
      () => this.sortEntities()
    );
  };

  sortEntities() {
    this.getEntities();
    this.props.history.push(`${this.props.location.pathname}?page=${this.state.activePage}&sort=${this.state.sort},${this.state.order}`);
  }

  handlePagination = activePage => this.setState({ activePage }, () => this.sortEntities());

  getMyEntities = () => {
	const { activePage, itemsPerPage, sort, order } = this.state;
    this.props.getMyEntities(activePage - 1, itemsPerPage, `${sort},${order}`);
  }

  getEntities = () => {
    const { activePage, itemsPerPage, sort, order } = this.state;
    this.props.getMyEntities(activePage - 1, itemsPerPage, `${sort},${order}`);
  };

  render() {
    const { ticketList, match, totalItems } = this.props;
    return (
      <div>
        <h2 id="ticket-heading">
          <Translate contentKey="jhipDefectTrackerApp.ticket.home.title">Tickets</Translate>
          <Link to="/ticket" className="btn btn-primary float-right flex-btn-group-container" id="my-tickets">
            <Translate contentKey="jhipDefectTrackerApp.ticket.home.allTicketsLabel">All Tickets</Translate>
          </Link>
          <Link to={`${match.url}/new`} className="btn btn-primary float-right jh-create-entity" id="jh-create-entity">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="jhipDefectTrackerApp.ticket.home.createLabel">Create a new Ticket</Translate>
          </Link>
        </h2>
        <div className="table-responsive">
          {ticketList && ticketList.length > 0 ? (
            <Table responsive aria-describedby="ticket-heading">
              <thead>
                <tr>
                  <th className="hand" onClick={this.sort('id')}>
                    <Translate contentKey="global.field.id">ID</Translate> <FontAwesomeIcon icon="sort" />
                  </th>
                  <th className="hand" onClick={this.sort('title')}>
                    <Translate contentKey="jhipDefectTrackerApp.ticket.title">Title</Translate> <FontAwesomeIcon icon="sort" />
                  </th>
                  <th className="hand" onClick={this.sort('description')}>
                    <Translate contentKey="jhipDefectTrackerApp.ticket.description">Description</Translate> <FontAwesomeIcon icon="sort" />
                  </th>
                  <th className="hand" onClick={this.sort('dueDate')}>
                    <Translate contentKey="jhipDefectTrackerApp.ticket.dueDate">Due Date</Translate> <FontAwesomeIcon icon="sort" />
                  </th>
                  <th className="hand" onClick={this.sort('done')}>
                    <Translate contentKey="jhipDefectTrackerApp.ticket.done">Done</Translate> <FontAwesomeIcon icon="sort" />
                  </th>
                  <th>
                    <Translate contentKey="jhipDefectTrackerApp.ticket.project">Project</Translate> <FontAwesomeIcon icon="sort" />
                  </th>
                  <th>
                    <Translate contentKey="jhipDefectTrackerApp.ticket.assignedTo">Assigned To</Translate> <FontAwesomeIcon icon="sort" />
                  </th>
                  <th />
                </tr>
              </thead>
              <tbody>
                {ticketList.map((ticket, i) => (
                  <tr key={`entity-${i}`}>
                    <td>
                      <Button tag={Link} to={`${match.url}/${ticket.id}`} color="link" size="sm">
                        {ticket.id}
                      </Button>
                    </td>
                    <td>{ticket.title}</td>
                    <td>{ticket.description}</td>
                    <td>
                      <TextFormat type="date" value={ticket.dueDate} format={APP_DATE_FORMAT} />
                    </td>
                    <td>{ticket.done ? 'true' : 'false'}</td>
                    <td>{ticket.project ? <Link to={`project/${ticket.project.id}`}>{ticket.project.name}</Link> : ''}</td>
                    <td>{ticket.assignedTo ? ticket.assignedTo.login : ''}</td>
                    <td className="text-right">
                      <div className="btn-group flex-btn-group-container">
                        <Button tag={Link} to={`${match.url}/${ticket.id}`} color="info" size="sm">
                          <FontAwesomeIcon icon="eye" />{' '}
                          <span className="d-none d-md-inline">
                            <Translate contentKey="entity.action.view">View</Translate>
                          </span>
                        </Button>
                        <Button tag={Link} to={`${match.url}/${ticket.id}/edit`} color="primary" size="sm">
                          <FontAwesomeIcon icon="pencil-alt" />{' '}
                          <span className="d-none d-md-inline">
                            <Translate contentKey="entity.action.edit">Edit</Translate>
                          </span>
                        </Button>
                        <Button tag={Link} to={`${match.url}/${ticket.id}/delete`} color="danger" size="sm">
                          <FontAwesomeIcon icon="trash" />{' '}
                          <span className="d-none d-md-inline">
                            <Translate contentKey="entity.action.delete">Delete</Translate>
                          </span>
                        </Button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </Table>
          ) : (
            <div className="alert alert-warning">
              <Translate contentKey="jhipDefectTrackerApp.ticket.home.notFound">No Tickets found</Translate>
            </div>
          )}
        </div>
        <div className={ticketList && ticketList.length > 0 ? '' : 'd-none'}>
          <Row className="justify-content-center">
            <JhiItemCount page={this.state.activePage} total={totalItems} itemsPerPage={this.state.itemsPerPage} i18nEnabled />
          </Row>
          <Row className="justify-content-center">
            <JhiPagination
              activePage={this.state.activePage}
              onSelect={this.handlePagination}
              maxButtons={5}
              itemsPerPage={this.state.itemsPerPage}
              totalItems={this.props.totalItems}
            />
          </Row>
        </div>
      </div>
    );
  }
}

const mapStateToProps = ({ ticket }: IRootState) => ({
  ticketList: ticket.entities,
  totalItems: ticket.totalItems
});

const mapDispatchToProps = {
  getMyEntities
};


type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(TicketSelf);
