package ua.oleksii.realestatebroker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.oleksii.realestatebroker.model.Visit;

public interface VisitRepository extends JpaRepository<Visit, Long> {
}
