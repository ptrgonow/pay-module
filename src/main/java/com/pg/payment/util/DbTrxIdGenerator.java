package com.pg.payment.util;

import com.pg.payment.persistence.entity.Sequence;
import com.pg.payment.persistence.repo.SeqRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@RequiredArgsConstructor
public class DbTrxIdGenerator implements TrxIdGenerator {
    
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyMMdd");
    
    private final SeqRepository seqRepo;
    
    
    @Override
    @Transactional
    public String generateTrxId( ) {
        String dateKey = LocalDate.now().format(DATE_FORMAT);
        
        Sequence sequence = seqRepo.findById(dateKey)
                .orElseGet(( ) -> Sequence.builder()
                        .seqDate(dateKey)
                        .currentSeq(0)
                        .version(0)
                        .build());
        
        sequence.increment();
        
        if (sequence.getCurrentSeq() > 999_999) {
            // TODO: 추후 전역 에러핸들러를 사용하여 처리
            log.error("일간 트랜잭션 ID가 999,999를 초과했습니다. 날짜: {}, 현재 시퀀스: {}", dateKey, sequence.getCurrentSeq());
            throw new IllegalStateException("일간 거래 ID 수 초과로 더 이상 생성할 수 없습니다.");
        }
        
        seqRepo.save(sequence);
        
        return "T" + dateKey + String.format("%06d", sequence.getCurrentSeq());
    }
}
