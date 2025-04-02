package org.boot.repository;

import org.boot.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
public interface PlanRepository extends JpaRepository<Plan, Long> {



}
