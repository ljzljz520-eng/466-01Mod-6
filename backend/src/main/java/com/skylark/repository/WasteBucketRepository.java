package com.skylark.repository;

import com.skylark.entity.WasteBucketEntity;
import com.skylark.enums.WasteCategory;
import com.skylark.enums.WasteStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WasteBucketRepository extends JpaRepository<WasteBucketEntity, Long>, JpaSpecificationExecutor<WasteBucketEntity> {

    Optional<WasteBucketEntity> findByBucketNo(String bucketNo);

    Optional<WasteBucketEntity> findByQrCode(String qrCode);

    boolean existsByBucketNo(String bucketNo);

    boolean existsByQrCode(String qrCode);

    List<WasteBucketEntity> findByStatus(WasteStatus status);

    List<WasteBucketEntity> findByStudentId(String studentId);

    List<WasteBucketEntity> findByLabRoom(String labRoom);

    @Query("SELECT w FROM WasteBucketEntity w WHERE " +
           "(:bucketNo IS NULL OR w.bucketNo LIKE %:bucketNo%) AND " +
           "(:category IS NULL OR w.category = :category) AND " +
           "(:status IS NULL OR w.status = :status) AND " +
           "(:studentId IS NULL OR w.studentId LIKE %:studentId%) AND " +
           "(:studentName IS NULL OR w.studentName LIKE %:studentName%) AND " +
           "(:experimentName IS NULL OR w.experimentName LIKE %:experimentName%) AND " +
           "(:labRoom IS NULL OR w.labRoom LIKE %:labRoom%) AND " +
           "(:startTime IS NULL OR w.submittedAt >= :startTime) AND " +
           "(:endTime IS NULL OR w.submittedAt <= :endTime) AND " +
           "(:categoryCorrected IS NULL OR " +
           "  (:categoryCorrected = true AND w.categoryCorrectionRemark IS NOT NULL) OR " +
           "  (:categoryCorrected = false AND w.categoryCorrectionRemark IS NULL))")
    Page<WasteBucketEntity> findByConditions(
            @Param("bucketNo") String bucketNo,
            @Param("category") WasteCategory category,
            @Param("status") WasteStatus status,
            @Param("studentId") String studentId,
            @Param("studentName") String studentName,
            @Param("experimentName") String experimentName,
            @Param("labRoom") String labRoom,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("categoryCorrected") Boolean categoryCorrected,
            Pageable pageable
    );

    @Query("SELECT w.category, COUNT(w) FROM WasteBucketEntity w GROUP BY w.category")
    List<Object[]> countByCategory();

    @Query("SELECT w.status, COUNT(w) FROM WasteBucketEntity w GROUP BY w.status")
    List<Object[]> countByStatus();
}
