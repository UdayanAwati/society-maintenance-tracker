package com.society.maintenance.repository;

import com.society.maintenance.entity.Notice;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    Page<Notice> findAllByOrderByImportantDescCreatedAtDesc(Pageable pageable);
}
