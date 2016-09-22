package hr.spring.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hr.spring.demo.domain.Applicant;

@Repository
public interface ApplicantRepository extends JpaRepository<Applicant, Long> {

}