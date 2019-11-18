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

import com.sidbisht.jhip.defecttracker.domain.Label;
import com.sidbisht.jhip.defecttracker.domain.*; // for static metamodels
import com.sidbisht.jhip.defecttracker.repository.LabelRepository;
import com.sidbisht.jhip.defecttracker.service.dto.LabelCriteria;

/**
 * Service for executing complex queries for {@link Label} entities in the database.
 * The main input is a {@link LabelCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Label} or a {@link Page} of {@link Label} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class LabelQueryService extends QueryService<Label> {

    private final Logger log = LoggerFactory.getLogger(LabelQueryService.class);

    private final LabelRepository labelRepository;

    public LabelQueryService(LabelRepository labelRepository) {
        this.labelRepository = labelRepository;
    }

    /**
     * Return a {@link List} of {@link Label} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Label> findByCriteria(LabelCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Label> specification = createSpecification(criteria);
        return labelRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Label} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Label> findByCriteria(LabelCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Label> specification = createSpecification(criteria);
        return labelRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(LabelCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Label> specification = createSpecification(criteria);
        return labelRepository.count(specification);
    }

    /**
     * Function to convert {@link LabelCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Label> createSpecification(LabelCriteria criteria) {
        Specification<Label> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Label_.id));
            }
            if (criteria.getLabel() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLabel(), Label_.label));
            }
        }
        return specification;
    }
}
