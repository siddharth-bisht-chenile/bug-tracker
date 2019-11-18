import { Moment } from 'moment';
import { ILabel } from 'app/shared/model/label.model';
import { IProject } from 'app/shared/model/project.model';
import { IUser } from 'app/shared/model/user.model';

export interface ITicket {
  id?: number;
  title?: string;
  description?: string;
  dueDate?: Moment;
  done?: boolean;
  labels?: ILabel[];
  project?: IProject;
  assignedTo?: IUser;
}

export const defaultValue: Readonly<ITicket> = {
  done: false
};
