package com.sidbisht.jhip.defecttracker.repository;
import com.sidbisht.jhip.defecttracker.domain.Label;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Label entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LabelRepository extends JpaRepository<Label, Long>, JpaSpecificationExecutor<Label> {

}
