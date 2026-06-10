package com.skylark.controller;

import com.skylark.dto.*;
import com.skylark.entity.WasteBucketEntity;
import com.skylark.enums.WasteStatus;
import com.skylark.service.WasteBucketService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/waste-buckets")
public class WasteBucketController {

    @Autowired
    private WasteBucketService wasteBucketService;

    @PostMapping("/submit")
    public ResponseEntity<WasteBucketEntity> submitWaste(@Valid @RequestBody WasteBucketSubmitDTO dto) {
        WasteBucketEntity result = wasteBucketService.submitWaste(dto);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/safety-check")
    public ResponseEntity<WasteBucketEntity> safetyCheck(@Valid @RequestBody WasteSafetyCheckDTO dto) {
        WasteBucketEntity result = wasteBucketService.safetyCheck(dto);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/store")
    public ResponseEntity<WasteBucketEntity> storeWaste(@Valid @RequestBody WasteStorageDTO dto) {
        WasteBucketEntity result = wasteBucketService.storeWaste(dto);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/collect")
    public ResponseEntity<WasteBucketEntity> collectWaste(@Valid @RequestBody WasteCollectionDTO dto) {
        WasteBucketEntity result = wasteBucketService.collectWaste(dto);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WasteBucketEntity> getById(@PathVariable Long id) {
        WasteBucketEntity result = wasteBucketService.getById(id);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/bucket-no/{bucketNo}")
    public ResponseEntity<WasteBucketEntity> getByBucketNo(@PathVariable String bucketNo) {
        WasteBucketEntity result = wasteBucketService.getByBucketNo(bucketNo);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/qr-code/{qrCode}")
    public ResponseEntity<WasteBucketEntity> getByQrCode(@PathVariable String qrCode) {
        WasteBucketEntity result = wasteBucketService.getByQrCode(qrCode);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/query")
    public ResponseEntity<Page<WasteBucketEntity>> queryWaste(@RequestBody WasteTraceQueryDTO dto) {
        Page<WasteBucketEntity> result = wasteBucketService.queryWaste(dto);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<WasteBucketEntity>> getByStatus(@PathVariable WasteStatus status) {
        List<WasteBucketEntity> result = wasteBucketService.getByStatus(status);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<WasteBucketEntity>> getByStudentId(@PathVariable String studentId) {
        List<WasteBucketEntity> result = wasteBucketService.getByStudentId(studentId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        Map<String, Object> result = wasteBucketService.getStatistics();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/pending/safety-check")
    public ResponseEntity<List<WasteBucketEntity>> getPendingSafetyCheck() {
        List<WasteBucketEntity> result = wasteBucketService.getPendingSafetyCheck();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/pending/storage")
    public ResponseEntity<List<WasteBucketEntity>> getPendingStorage() {
        List<WasteBucketEntity> result = wasteBucketService.getPendingStorage();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/pending/collection")
    public ResponseEntity<List<WasteBucketEntity>> getPendingCollection() {
        List<WasteBucketEntity> result = wasteBucketService.getPendingCollection();
        return ResponseEntity.ok(result);
    }
}
