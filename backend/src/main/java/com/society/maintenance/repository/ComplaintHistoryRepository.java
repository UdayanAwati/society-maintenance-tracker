package com.society.maintenance.repository;

import com.society.maintenance.entity.Complaint;
import com.society.maintenance.entity.ComplaintHistory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComplaintHistoryRepository extends JpaRepository<ComplaintHistory, Long> {
    List<ComplaintHistory> findByComplaintOrderByTimestampAsc(Complaint complaint);
}
