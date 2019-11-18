package com.sidbisht.jhip.defecttracker.domain;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Label.class)
public abstract class Label_ {

	public static volatile SetAttribute<Label, Ticket> tickets;
	public static volatile SingularAttribute<Label, Long> id;
	public static volatile SingularAttribute<Label, String> label;

	public static final String TICKETS = "tickets";
	public static final String ID = "id";
	public static final String LABEL = "label";

}

