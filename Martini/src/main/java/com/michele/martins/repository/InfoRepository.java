package com.michele.martins.repository;

import com.michele.martins.model.Info;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InfoRepository extends JpaRepository<Info, Long> {
   
}
