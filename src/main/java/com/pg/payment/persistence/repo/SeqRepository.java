package com.pg.payment.persistence.repo;

import com.pg.payment.persistence.entity.Sequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeqRepository extends JpaRepository<Sequence, String> {

}
