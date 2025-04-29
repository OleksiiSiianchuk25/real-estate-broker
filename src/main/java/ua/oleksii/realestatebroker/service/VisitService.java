package ua.oleksii.realestatebroker.service;

import org.springframework.stereotype.Service;
import ua.oleksii.realestatebroker.model.Visit;
import ua.oleksii.realestatebroker.repository.VisitRepository;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class VisitService {
    private final VisitRepository visitRepo;

    public VisitService(VisitRepository visitRepo) {
        this.visitRepo = visitRepo;
    }

    /** Записуємо кожен http‑запит у таблицю visits */
    public void record(HttpServletRequest req) {
        Visit v = new Visit();
        v.setPath(req.getRequestURI());
        visitRepo.save(v);
    }

    public long getTotalVisits() {
        return visitRepo.count();
    }

    public List<Visit> getAll() {
        return visitRepo.findAll();
    }
}
