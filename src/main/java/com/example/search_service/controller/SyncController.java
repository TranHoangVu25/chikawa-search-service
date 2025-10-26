package com.example.search_service.controller;

import com.example.search_service.services.SyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/sync")
@RequiredArgsConstructor
public class SyncController {

    private final SyncService syncService;

    /**
     * Gọi thủ công để đồng bộ toàn bộ sản phẩm từ Product Service vào Elasticsearch.
     */
    @PostMapping
    public ResponseEntity<String> syncAllProducts() {
        log.info("🔄 Yêu cầu đồng bộ dữ liệu sản phẩm nhận được...");
        try {
            syncService.syncAllProducts();
            return ResponseEntity.ok("✅ Đồng bộ dữ liệu sản phẩm thành công!");
        } catch (Exception e) {
            log.error("❌ Lỗi khi đồng bộ sản phẩm: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body("❌ Đồng bộ thất bại: " + e.getMessage());
        }
    }
}

