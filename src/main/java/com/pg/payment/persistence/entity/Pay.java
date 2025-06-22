package com.pg.payment.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pg_trx_pay")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Pay {
    
    @Id
    @Column(name = "trxId", length = 255)
    private String trxId;
    
    @Column(name = "mchtId", length = 50, nullable = false)
    private String mchtId;
    
    @Column(name = "tmnId", length = 50, nullable = false)
    private String tmnId;
    
    @Column(name = "trackId", length = 255)
    private String trackId;
    
    @Column(name = "payerName", length = 100)
    private String payerName;
    
    @Column(name = "payerEmail", length = 100)
    private String payerEmail;
    
    @Column(name = "payerTel", length = 20)
    private String payerTel;
    
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;
    
    @Column(name = "installment", length = 2, nullable = false)
    private String installment;
    
    @Column(name = "cardType", length = 10)
    private String cardType;
    
    @Column(name = "bin", length = 255)
    private String bin;
    
    @Builder.Default
    @Column(name = "last4", length = 4)
    private String last4 = "";
    
    // TODO: enum으로 리팩토링 예정
    @Column(name = "status", length = 10)
    private String status;
    
    @Column(name = "issuer", length = 128)
    private String issuer;
    
    @Column(name = "acquirer", length = 128)
    private String acquirer;
    
    @Column(name = "reqDay", length = 8, nullable = false)
    private String reqDay;
    
    @Column(name = "reqTime", length = 6)
    private String reqTime;
    
    @Column(name = "authCd", length = 8)
    private String authCd;
    
    @Column(name = "resultCd", length = 6, nullable = false)
    private String resultCd;
    
    @Column(name = "resultMsg", length = 50, nullable = false)
    private String resultMsg;
    
    @Column(name = "van", length = 10, nullable = false)
    private String van;
    
    @Column(name = "vanId", length = 50, nullable = false)
    private String vanId;
    
    @Column(name = "vanTrxId", length = 255, nullable = false)
    private String vanTrxId;
    
    @Column(name = "regDay", nullable = false)
    private Integer regDay;
    
    @Column(name = "regTime", length = 6, nullable = false)
    private String regTime;
    
    @Column(name = "regDate", nullable = false,
            columnDefinition = "timestamp default CURRENT_TIMESTAMP")
    private LocalDateTime regDate;
    
    /**
     * ※ 시스템 내에서 사용되지 않음. 외부 테이블 연계 또는 레거시 호환성 유지용.
     * 항상 빈 문자열("")로 저장되며, 절대 업무 로직에 사용되지 않음.
     */
    @Builder.Default
    @Column(name = "prodId", length = 64)
    private String prodId = "";
    
    @Builder.Default
    @Column(name = "cardId", length = 32)
    private String cardId = "";
    
    @Builder.Default
    @Column(name = "cyrexVtid", length = 64)
    private String cyrexVtid = "";
    
    @Builder.Default
    @Column(name = "cyrexRouteVan", length = 32)
    private String cyrexRouteVan = "";
    
    @Builder.Default
    @Column(name = "cyrexCid", length = 64)
    private String cyrexCid = "";
    
    @PrePersist
    private void prePersist( ) {
        if (this.regDate == null) {
            this.regDate = LocalDateTime.now();
        }
        if (this.regDay == null) {
            this.regDay = Integer.parseInt(LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")));
        }
        if (this.regTime == null) {
            this.regTime = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HHmmss"));
        }
        if (this.installment == null || this.installment.isEmpty()) {
            this.installment = "00";
        }
    }
    
    
}
