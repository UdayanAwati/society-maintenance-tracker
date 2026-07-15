package com.society.maintenance.repository;

import com.society.maintenance.entity.*;
import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface ComplaintRepository extends JpaRepository<Complaint, Long>, JpaSpecificationExecutor<Complaint> {
    Page<Complaint> findByResident(User resident, Pageable pageable);
    long countByStatus(ComplaintStatus status);
    long countByResidentAndStatus(User resident, ComplaintStatus status);
    long countByResident(User resident);
    long countByIsOverdueTrue();
    List<Complaint> findByStatusInAndCreatedAtBefore(List<ComplaintStatus> statuses, Instant before);

    @Query("select c.status as label, count(c) as value from Complaint c group by c.status")
    List<MetricRow> countByStatusMetric();
    @Query("select c.category as label, count(c) as value from Complaint c group by c.category")
    List<MetricRow> countByCategoryMetric();
    @Query("select c.priority as label, count(c) as value from Complaint c group by c.priority")
    List<MetricRow> countByPriorityMetric();

    @Query("""
            select c from Complaint c
            join c.resident r
            where (:status is null or c.status = :status)
              and (:priority is null or c.priority = :priority)
              and (:category is null or lower(c.category) = lower(:category))
              and (:residentId is null or r.id = :residentId)
              and (:from is null or c.createdAt >= :from)
              and (:to is null or c.createdAt <= :to)
              and (:search is null or lower(c.description) like lower(concat('%', :search, '%'))
                   or lower(r.name) like lower(concat('%', :search, '%'))
                   or cast(c.id as string) = :search)
            """)
    Page<Complaint> search(@Param("status") ComplaintStatus status,
                           @Param("priority") ComplaintPriority priority,
                           @Param("category") String category,
                           @Param("residentId") Long residentId,
                           @Param("from") Instant from,
                           @Param("to") Instant to,
                           @Param("search") String search,
                           Pageable pageable);

    interface MetricRow {
        Object getLabel();
        long getValue();
    }
}
