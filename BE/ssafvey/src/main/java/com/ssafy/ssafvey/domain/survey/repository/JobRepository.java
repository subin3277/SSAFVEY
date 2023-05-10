package com.ssafy.ssafvey.domain.survey.repository;

import com.ssafy.ssafvey.domain.member.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job,Long> {
}
