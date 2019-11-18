import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
import { Translate, translate, ICrudGetAction, ICrudGetAllAction, ICrudPutAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { ILabel } from 'app/shared/model/label.model';
import { getEntities as getLabels } from 'app/entities/label/label.reducer';
import { IProject } from 'app/shared/model/project.model';
import { getEntities as getProjects } from 'app/entities/project/project.reducer';
import { IUser } from 'app/shared/model/user.model';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { getEntity, updateEntity, createEntity, reset } from './ticket.reducer';
import { ITicket } from 'app/shared/model/ticket.model';
import { convertDateTimeFromServer, convertDateTimeToServer } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface ITicketUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export interface ITicketUpdateState {
  isNew: boolean;
  idslabel: any[];
  projectId: string;
  assignedToId: string;
}

export class TicketUpdate extends React.Component<ITicketUpdateProps, ITicketUpdateState> {
  constructor(props) {
    super(props);
    this.state = {
      idslabel: [],
      projectId: '0',
      assignedToId: '0',
      isNew: !this.props.match.params || !this.props.match.params.id
    };
  }

  componentWillUpdate(nextProps, nextState) {
    if (nextProps.updateSuccess !== this.props.updateSuccess && nextProps.updateSuccess) {
      this.handleClose();
    }
  }

  componentDidMount() {
    if (this.state.isNew) {
      this.props.reset();
    } else {
      this.props.getEntity(this.props.match.params.id);
    }

    this.props.getLabels();
    this.props.getProjects();
    this.props.getUsers();
  }

  saveEntity = (event, errors, values) => {
    values.dueDate = convertDateTimeToServer(values.dueDate);

    if (errors.length === 0) {
      const { ticketEntity } = this.props;
      const entity = {
        ...ticketEntity,
        ...values,
        labels: mapIdList(values.labels)
      };

      if (this.state.isNew) {
        this.props.createEntity(entity);
      } else {
        this.props.updateEntity(entity);
      }
    }
  };

  handleClose = () => {
    this.props.history.push('/ticket');
  };

  render() {
    const { ticketEntity, labels, projects, users, loading, updating } = this.props;
    const { isNew } = this.state;

    return (
      <div>
        <Row className="justify-content-center">
          <Col md="8">
            <h2 id="jhipDefectTrackerApp.ticket.home.createOrEditLabel">
              <Translate contentKey="jhipDefectTrackerApp.ticket.home.createOrEditLabel">Create or edit a Ticket</Translate>
            </h2>
          </Col>
        </Row>
        <Row className="justify-content-center">
          <Col md="8">
            {loading ? (
              <p>Loading...</p>
            ) : (
              <AvForm model={isNew ? {} : ticketEntity} onSubmit={this.saveEntity}>
                {!isNew ? (
                  <AvGroup>
                    <Label for="ticket-id">
                      <Translate contentKey="global.field.id">ID</Translate>
                    </Label>
                    <AvInput id="ticket-id" type="text" className="form-control" name="id" required readOnly />
                  </AvGroup>
                ) : null}
                <AvGroup>
                  <Label id="titleLabel" for="ticket-title">
                    <Translate contentKey="jhipDefectTrackerApp.ticket.title">Title</Translate>
                  </Label>
                  <AvField
                    id="ticket-title"
                    type="text"
                    name="title"
                    validate={{
                      required: { value: true, errorMessage: translate('entity.validation.required') },
                      maxLength: { value: 255, errorMessage: translate('entity.validation.maxlength', { max: 255 }) }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="descriptionLabel" for="ticket-description">
                    <Translate contentKey="jhipDefectTrackerApp.ticket.description">Description</Translate>
                  </Label>
                  <AvField
                    id="ticket-description"
                    type="text"
                    name="description"
                    validate={{
                      required: { value: true, errorMessage: translate('entity.validation.required') }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="dueDateLabel" for="ticket-dueDate">
                    <Translate contentKey="jhipDefectTrackerApp.ticket.dueDate">Due Date</Translate>
                  </Label>
                  <AvInput
                    id="ticket-dueDate"
                    type="datetime-local"
                    className="form-control"
                    name="dueDate"
                    placeholder={'YYYY-MM-DD HH:mm'}
                    value={isNew ? null : convertDateTimeFromServer(this.props.ticketEntity.dueDate)}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="doneLabel" check>
                    <AvInput id="ticket-done" type="checkbox" className="form-control" name="done" />
                    <Translate contentKey="jhipDefectTrackerApp.ticket.done">Done</Translate>
                  </Label>
                </AvGroup>
                <AvGroup>
                  <Label for="ticket-label">
                    <Translate contentKey="jhipDefectTrackerApp.ticket.label">Label</Translate>
                  </Label>
                  <AvInput
                    id="ticket-label"
                    type="select"
                    multiple
                    className="form-control"
                    name="labels"
                    value={ticketEntity.labels && ticketEntity.labels.map(e => e.id)}
                  >
                    <option value="" key="0" />
                    {labels
                      ? labels.map(otherEntity => (
                          <option value={otherEntity.id} key={otherEntity.id}>
                            {otherEntity.label}
                          </option>
                        ))
                      : null}
                  </AvInput>
                </AvGroup>
                <AvGroup>
                  <Label for="ticket-project">
                    <Translate contentKey="jhipDefectTrackerApp.ticket.project">Project</Translate>
                  </Label>
                  <AvInput
                    id="ticket-project"
                    type="select"
                    className="form-control"
                    name="project.id"
                    value={isNew ? projects[0] && projects[0].id : ticketEntity.project.id}
                    required
                  >
                    {projects
                      ? projects.map(otherEntity => (
                          <option value={otherEntity.id} key={otherEntity.id}>
                            {otherEntity.name}
                          </option>
                        ))
                      : null}
                  </AvInput>
                  <AvFeedback>
                    <Translate contentKey="entity.validation.required">This field is required.</Translate>
                  </AvFeedback>
                </AvGroup>
                <AvGroup>
                  <Label for="ticket-assignedTo">
                    <Translate contentKey="jhipDefectTrackerApp.ticket.assignedTo">Assigned To</Translate>
                  </Label>
                  <AvInput id="ticket-assignedTo" type="select" className="form-control" name="assignedTo.id">
                    <option value="" key="0" />
                    {users
                      ? users.map(otherEntity => (
                          <option value={otherEntity.id} key={otherEntity.id}>
                            {otherEntity.login}
                          </option>
                        ))
                      : null}
                  </AvInput>
                </AvGroup>
                <Button tag={Link} id="cancel-save" to="/ticket" replace color="info">
                  <FontAwesomeIcon icon="arrow-left" />
                  &nbsp;
                  <span className="d-none d-md-inline">
                    <Translate contentKey="entity.action.back">Back</Translate>
                  </span>
                </Button>
                &nbsp;
                <Button color="primary" id="save-entity" type="submit" disabled={updating}>
                  <FontAwesomeIcon icon="save" />
                  &nbsp;
                  <Translate contentKey="entity.action.save">Save</Translate>
                </Button>
              </AvForm>
            )}
          </Col>
        </Row>
      </div>
    );
  }
}

const mapStateToProps = (storeState: IRootState) => ({
  labels: storeState.label.entities,
  projects: storeState.project.entities,
  users: storeState.userManagement.users,
  ticketEntity: storeState.ticket.entity,
  loading: storeState.ticket.loading,
  updating: storeState.ticket.updating,
  updateSuccess: storeState.ticket.updateSuccess
});

const mapDispatchToProps = {
  getLabels,
  getProjects,
  getUsers,
  getEntity,
  updateEntity,
  createEntity,
  reset
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(TicketUpdate);
