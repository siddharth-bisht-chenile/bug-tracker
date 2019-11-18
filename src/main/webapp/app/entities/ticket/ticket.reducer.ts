import axios from 'axios';
import { ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { ITicket, defaultValue } from 'app/shared/model/ticket.model';

export const ACTION_TYPES = {
  FETCH_TICKET_LIST: 'ticket/FETCH_TICKET_LIST',
  FETCH_TICKET_SELF: 'ticket/FETCH_TICKET_SELF',
  FETCH_TICKET: 'ticket/FETCH_TICKET',
  CREATE_TICKET: 'ticket/CREATE_TICKET',
  UPDATE_TICKET: 'ticket/UPDATE_TICKET',
  DELETE_TICKET: 'ticket/DELETE_TICKET',
  RESET: 'ticket/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<ITicket>,
  entity: defaultValue,
  updating: false,
  totalItems: 0,
  updateSuccess: false
};

export type TicketState = Readonly<typeof initialState>;

// Reducer

export default (state: TicketState = initialState, action): TicketState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.FETCH_TICKET_SELF):
    case REQUEST(ACTION_TYPES.FETCH_TICKET_LIST):
    case REQUEST(ACTION_TYPES.FETCH_TICKET):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.CREATE_TICKET):
    case REQUEST(ACTION_TYPES.UPDATE_TICKET):
    case REQUEST(ACTION_TYPES.DELETE_TICKET):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.FETCH_TICKET_LIST):
    case FAILURE(ACTION_TYPES.FETCH_TICKET):
    case FAILURE(ACTION_TYPES.CREATE_TICKET):
    case FAILURE(ACTION_TYPES.UPDATE_TICKET):
    case FAILURE(ACTION_TYPES.DELETE_TICKET):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.FETCH_TICKET_LIST):
      return {
        ...state,
        loading: false,
        entities: action.payload.data,
        totalItems: parseInt(action.payload.headers['x-total-count'], 10)
      };
    case SUCCESS(ACTION_TYPES.FETCH_TICKET):
      return {
        ...state,
        loading: false,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.CREATE_TICKET):
    case SUCCESS(ACTION_TYPES.UPDATE_TICKET):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.DELETE_TICKET):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: {}
      };
    case ACTION_TYPES.RESET:
      return {
        ...initialState
      };
    default:
      return state;
  }
};

const apiUrl = 'api/tickets';

// Actions

export const getMyEntities: ICrudGetAllAction<ITicket> = (page, size, sort) => {
  const requestUrl = `${apiUrl}/self/${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_TICKET_SELF,
    payload: axios.get<ITicket>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`)
  };
};

export const getEntities: ICrudGetAllAction<ITicket> = (page, size, sort) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_TICKET_LIST,
    payload: axios.get<ITicket>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`)
  };
};

export const getEntity: ICrudGetAction<ITicket> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_TICKET,
    payload: axios.get<ITicket>(requestUrl)
  };
};

export const createEntity: ICrudPutAction<ITicket> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_TICKET,
    payload: axios.post(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<ITicket> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_TICKET,
    payload: axios.put(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const deleteEntity: ICrudDeleteAction<ITicket> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_TICKET,
    payload: axios.delete(requestUrl)
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});
