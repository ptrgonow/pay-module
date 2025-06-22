package com.pg.payment.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pg_trx_seq")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Sequence {
    
    @Id
    @Column(length = 6)
    private String seqDate;
    
    @Column(nullable = false)
    private Integer currentSeq;
    
    @Version
    private Integer version;
    
    public void increment( ) {
        this.currentSeq++;
    }
    
}
