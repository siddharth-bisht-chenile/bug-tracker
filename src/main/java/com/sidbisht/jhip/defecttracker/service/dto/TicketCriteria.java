package com.sidbisht.jhip.defecttracker.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.Criteria;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;
import io.github.jhipster.service.filter.ZonedDateTimeFilter;

/**
 * Criteria class for the {@link com.sidbisht.jhip.defecttracker.domain.Ticket} entity. This class is used
 * in {@link com.sidbisht.jhip.defecttracker.web.rest.TicketResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /tickets?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class TicketCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter title;

    private StringFilter description;

    private ZonedDateTimeFilter dueDate;

    private BooleanFilter done;

    private LongFilter labelId;

    private LongFilter projectId;

    private LongFilter assignedToId;

    public TicketCriteria(){
    }

    public TicketCriteria(TicketCriteria other){
        this.id = other.id == null ? null : other.id.copy();
        this.title = other.title == null ? null : other.title.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.dueDate = other.dueDate == null ? null : other.dueDate.copy();
        this.done = other.done == null ? null : other.done.copy();
        this.labelId = other.labelId == null ? null : other.labelId.copy();
        this.projectId = other.projectId == null ? null : other.projectId.copy();
        this.assignedToId = other.assignedToId == null ? null : other.assignedToId.copy();
    }

    @Override
    public TicketCriteria copy() {
        return new TicketCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getTitle() {
        return title;
    }

    public void setTitle(StringFilter title) {
        this.title = title;
    }

    public StringFilter getDescription() {
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public ZonedDateTimeFilter getDueDate() {
        return dueDate;
    }

    public void setDueDate(ZonedDateTimeFilter dueDate) {
        this.dueDate = dueDate;
    }

    public BooleanFilter getDone() {
        return done;
    }

    public void setDone(BooleanFilter done) {
        this.done = done;
    }

    public LongFilter getLabelId() {
        return labelId;
    }

    public void setLabelId(LongFilter labelId) {
        this.labelId = labelId;
    }

    public LongFilter getProjectId() {
        return projectId;
    }

    public void setProjectId(LongFilter projectId) {
        this.projectId = projectId;
    }

    public LongFilter getAssignedToId() {
        return assignedToId;
    }

    public void setAssignedToId(LongFilter assignedToId) {
        this.assignedToId = assignedToId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final TicketCriteria that = (TicketCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(title, that.title) &&
            Objects.equals(description, that.description) &&
            Objects.equals(dueDate, that.dueDate) &&
            Objects.equals(done, that.done) &&
            Objects.equals(labelId, that.labelId) &&
            Objects.equals(projectId, that.projectId) &&
            Objects.equals(assignedToId, that.assignedToId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        title,
        description,
        dueDate,
        done,
        labelId,
        projectId,
        assignedToId
        );
    }

    @Override
    public String toString() {
        return "TicketCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (title != null ? "title=" + title + ", " : "") +
                (description != null ? "description=" + description + ", " : "") +
                (dueDate != null ? "dueDate=" + dueDate + ", " : "") +
                (done != null ? "done=" + done + ", " : "") +
                (labelId != null ? "labelId=" + labelId + ", " : "") +
                (projectId != null ? "projectId=" + projectId + ", " : "") +
                (assignedToId != null ? "assignedToId=" + assignedToId + ", " : "") +
            "}";
    }

}
