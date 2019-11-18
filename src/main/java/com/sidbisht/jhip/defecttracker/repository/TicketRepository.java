package com.sidbisht.jhip.defecttracker.repository;
import com.sidbisht.jhip.defecttracker.domain.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the Ticket entity.
 */
@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long>, JpaSpecificationExecutor<Ticket> {

    @Query("select ticket from Ticket ticket where ticket.assignedTo.login = ?#{principal.username}")
    Page<Ticket> findAllByAssignedToIsCurrentUser(Pageable pageable);

    @Query(value = "select distinct ticket from Ticket ticket left join fetch ticket.labels",
        countQuery = "select count(distinct ticket) from Ticket ticket")
    Page<Ticket> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct ticket from Ticket ticket left join fetch ticket.labels")
    List<Ticket> findAllWithEagerRelationships();

    @Query("select ticket from Ticket ticket left join fetch ticket.labels where ticket.id =:id")
    Optional<Ticket> findOneWithEagerRelationships(@Param("id") Long id);

    Page<Ticket> findAllByOrderByDueDateAsc(Pageable pageable);

}
