package com.sidbisht.jhip.defecttracker.domain;

import java.time.ZonedDateTime;
import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Ticket.class)
public abstract class Ticket_ {

	public static volatile SingularAttribute<Ticket, ZonedDateTime> dueDate;
	public static volatile SingularAttribute<Ticket, String> description;
	public static volatile SingularAttribute<Ticket, Project> project;
	public static volatile SingularAttribute<Ticket, Long> id;
	public static volatile SingularAttribute<Ticket, String> title;
	public static volatile SingularAttribute<Ticket, Boolean> done;
	public static volatile SingularAttribute<Ticket, User> assignedTo;
	public static volatile SetAttribute<Ticket, Label> labels;

	public static final String DUE_DATE = "dueDate";
	public static final String DESCRIPTION = "description";
	public static final String PROJECT = "project";
	public static final String ID = "id";
	public static final String TITLE = "title";
	public static final String DONE = "done";
	public static final String ASSIGNED_TO = "assignedTo";
	public static final String LABELS = "labels";

}

