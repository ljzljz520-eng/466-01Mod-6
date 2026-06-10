package com.skylark.service;

import com.skylark.dto.*;
import com.skylark.entity.WasteBucketEntity;
import com.skylark.enums.WasteCategory;
import com.skylark.enums.WasteStatus;
import com.skylark.repository.WasteBucketRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class WasteBucketService {

    @Autowired
    private WasteBucketRepository wasteBucketRepository;

    @Transactional
    public WasteBucketEntity submitWaste(WasteBucketSubmitDTO dto) {
        if (wasteBucketRepository.existsByBucketNo(dto.getBucketNo())) {
            throw new IllegalArgumentException("桶号已存在：" + dto.getBucketNo());
        }

        WasteBucketEntity entity = new WasteBucketEntity();
        entity.setBucketNo(dto.getBucketNo());
        entity.setCategory(dto.getCategory());
        entity.setWeight(dto.getWeight());
        entity.setHasSpecialIngredients(dto.getHasSpecialIngredients());
        entity.setSpecialIngredientsRemark(dto.getSpecialIngredientsRemark());
        entity.setExperimentName(dto.getExperimentName());
        entity.setExperimentTime(dto.getExperimentTime());
        entity.setLabRoom(dto.getLabRoom());
        entity.setStudentId(dto.getStudentId());
        entity.setStudentName(dto.getStudentName());
        entity.setStudentContact(dto.getStudentContact());

        String qrCode = "QR-" + dto.getBucketNo() + "-" + UUID.randomUUID().toString().substring(0, 8);
        entity.setQrCode(qrCode);

        entity.setStatus(WasteStatus.SUBMITTED);
        entity.setSubmittedAt(LocalDateTime.now());

        return wasteBucketRepository.save(entity);
    }

    @Transactional
    public WasteBucketEntity safetyCheck(WasteSafetyCheckDTO dto) {
        WasteBucketEntity entity = wasteBucketRepository.findById(dto.getBucketId())
                .orElseThrow(() -> new EntityNotFoundException("废液桶不存在，ID：" + dto.getBucketId()));

        if (entity.getStatus() != WasteStatus.SUBMITTED) {
            throw new IllegalStateException("当前状态不允许安全检查，当前状态：" + entity.getStatus().getDescription());
        }

        entity.setSafetyOfficerId(dto.getOfficerId());
        entity.setSafetyOfficerName(dto.getOfficerName());
        entity.setSafetyCheckedAt(LocalDateTime.now());
        entity.setSafetyRemark(dto.getRemark());

        if (dto.getPassed()) {
            if (dto.getCorrectedCategory() != null && dto.getCorrectedCategory() != entity.getCategory()) {
                entity.setCategoryCorrectionRemark(dto.getCorrectionRemark());
                entity.setCategory(dto.getCorrectedCategory());
            }
            entity.setStatus(WasteStatus.SAFETY_CHECKED);
        } else {
            entity.setStatus(WasteStatus.CANCELLED);
        }

        return wasteBucketRepository.save(entity);
    }

    @Transactional
    public WasteBucketEntity storeWaste(WasteStorageDTO dto) {
        WasteBucketEntity entity = wasteBucketRepository.findById(dto.getBucketId())
                .orElseThrow(() -> new EntityNotFoundException("废液桶不存在，ID：" + dto.getBucketId()));

        if (entity.getStatus() != WasteStatus.SAFETY_CHECKED) {
            throw new IllegalStateException("当前状态不允许入库，当前状态：" + entity.getStatus().getDescription());
        }

        entity.setStorageOperatorId(dto.getOperatorId());
        entity.setStorageOperatorName(dto.getOperatorName());
        entity.setStoredAt(LocalDateTime.now());
        entity.setStatus(WasteStatus.IN_STORAGE);

        return wasteBucketRepository.save(entity);
    }

    @Transactional
    public WasteBucketEntity collectWaste(WasteCollectionDTO dto) {
        WasteBucketEntity entity = wasteBucketRepository.findByQrCode(dto.getQrCode())
                .orElseThrow(() -> new EntityNotFoundException("二维码对应的废液桶不存在：" + dto.getQrCode()));

        if (entity.getStatus() != WasteStatus.IN_STORAGE) {
            throw new IllegalStateException("当前状态不允许回收，当前状态：" + entity.getStatus().getDescription());
        }

        entity.setRecyclerId(dto.getRecyclerId());
        entity.setRecyclerName(dto.getRecyclerName());
        entity.setCollectedAt(LocalDateTime.now());
        entity.setStatus(WasteStatus.COLLECTED);

        return wasteBucketRepository.save(entity);
    }

    public WasteBucketEntity getById(Long id) {
        return wasteBucketRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("废液桶不存在，ID：" + id));
    }

    public WasteBucketEntity getByBucketNo(String bucketNo) {
        return wasteBucketRepository.findByBucketNo(bucketNo)
                .orElseThrow(() -> new EntityNotFoundException("废液桶不存在，桶号：" + bucketNo));
    }

    public WasteBucketEntity getByQrCode(String qrCode) {
        return wasteBucketRepository.findByQrCode(qrCode)
                .orElseThrow(() -> new EntityNotFoundException("废液桶不存在，二维码：" + qrCode));
    }

    public Page<WasteBucketEntity> queryWaste(WasteTraceQueryDTO dto) {
        Pageable pageable = PageRequest.of(
                dto.getPageNum() - 1,
                dto.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return wasteBucketRepository.findByConditions(
                dto.getBucketNo(),
                dto.getCategory(),
                dto.getStatus(),
                dto.getStudentId(),
                dto.getStudentName(),
                dto.getExperimentName(),
                dto.getLabRoom(),
                dto.getStartTime(),
                dto.getEndTime(),
                dto.getCategoryCorrected(),
                pageable
        );
    }

    public List<WasteBucketEntity> getByStatus(WasteStatus status) {
        return wasteBucketRepository.findByStatus(status);
    }

    public List<WasteBucketEntity> getByStudentId(String studentId) {
        return wasteBucketRepository.findByStudentId(studentId);
    }

    public Map<String, Object> getStatistics() {
        Map<String, Object> result = new HashMap<>();

        List<Object[]> categoryCounts = wasteBucketRepository.countByCategory();
        Map<String, Long> categoryStats = new HashMap<>();
        for (Object[] row : categoryCounts) {
            WasteCategory category = (WasteCategory) row[0];
            Long count = (Long) row[1];
            categoryStats.put(category.name(), count);
        }
        result.put("byCategory", categoryStats);

        List<Object[]> statusCounts = wasteBucketRepository.countByStatus();
        Map<String, Long> statusStats = new HashMap<>();
        for (Object[] row : statusCounts) {
            WasteStatus status = (WasteStatus) row[0];
            Long count = (Long) row[1];
            statusStats.put(status.name(), count);
        }
        result.put("byStatus", statusStats);

        result.put("total", wasteBucketRepository.count());

        return result;
    }

    public List<WasteBucketEntity> getPendingSafetyCheck() {
        return wasteBucketRepository.findByStatus(WasteStatus.SUBMITTED);
    }

    public List<WasteBucketEntity> getPendingStorage() {
        return wasteBucketRepository.findByStatus(WasteStatus.SAFETY_CHECKED);
    }

    public List<WasteBucketEntity> getPendingCollection() {
        return wasteBucketRepository.findByStatus(WasteStatus.IN_STORAGE);
    }
}
