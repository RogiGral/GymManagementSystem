package com.gymsystem.gms.repository;

import com.gymsystem.gms.model.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScoreRepository extends JpaRepository<Score,Long> {
}
