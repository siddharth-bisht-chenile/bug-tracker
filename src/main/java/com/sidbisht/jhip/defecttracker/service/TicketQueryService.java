package com.sidbisht.jhip.defecttracker.service;

import java.util.List;

import javax.persistence.criteria.JoinType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import com.sidbisht.jhip.defecttracker.domain.Ticket;
import com.sidbisht.jhip.defecttracker.domain.*; // for static metamodels
import com.sidbisht.jhip.defecttracker.repository.TicketRepository;
import com.sidbisht.jhip.defecttracker.service.dto.TicketCriteria;

/**
 * Service for executing complex queries for {@link Ticket} entities in the database.
 * The main input is a {@link TicketCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Ticket} or a {@link Page} of {@link Ticket} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TicketQueryService extends QueryService<Ticket> {

    private final Logger log = LoggerFactory.getLogger(TicketQueryService.class);

    private final TicketRepository ticketRepository;

    public TicketQueryService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    /**
     * Return a {@link List} of {@link Ticket} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Ticket> findByCriteria(TicketCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Ticket> specification = createSpecification(criteria);
        return ticketRepository.findAll(specification);
    }

    @Transactional(readOnly = true)
    public Page<Ticket> findLoggedInUserTickets(Pageable pageable) {
        log.debug("find logged in users tickets");
        return ticketRepository.findAllByAssignedToIsCurrentUser(pageable);
    }
    /**
     * Return a {@link Page} of {@link Ticket} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Ticket> findByCriteria(TicketCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Ticket> specification = createSpecification(criteria);
        return ticketRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TicketCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Ticket> specification = createSpecification(criteria);
        return ticketRepository.count(specification);
    }

    /**
     * Function to convert {@link TicketCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Ticket> createSpecification(TicketCriteria criteria) {
        Specification<Ticket> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Ticket_.id));
            }
            if (criteria.getTitle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTitle(), Ticket_.title));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), Ticket_.description));
            }
            if (criteria.getDueDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDueDate(), Ticket_.dueDate));
            }
            if (criteria.getDone() != null) {
                specification = specification.and(buildSpecification(criteria.getDone(), Ticket_.done));
            }
            if (criteria.getLabelId() != null) {
                specification = specification.and(buildSpecification(criteria.getLabelId(),
                    root -> root.join(Ticket_.labels, JoinType.LEFT).get(Label_.id)));
            }
            if (criteria.getProjectId() != null) {
                specification = specification.and(buildSpecification(criteria.getProjectId(),
                    root -> root.join(Ticket_.project, JoinType.LEFT).get(Project_.id)));
            }
            if (criteria.getAssignedToId() != null) {
                specification = specification.and(buildSpecification(criteria.getAssignedToId(),
                    root -> root.join(Ticket_.assignedTo, JoinType.LEFT).get(User_.id)));
            }
        }
        return specification;
    }
    
}
