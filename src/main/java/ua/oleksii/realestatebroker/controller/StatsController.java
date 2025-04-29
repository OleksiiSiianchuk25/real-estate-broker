package ua.oleksii.realestatebroker.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.oleksii.realestatebroker.dto.StatsDTO;
import ua.oleksii.realestatebroker.model.Property;
import ua.oleksii.realestatebroker.model.User;
import ua.oleksii.realestatebroker.repository.FavoriteRepository;
import ua.oleksii.realestatebroker.repository.PropertyRepository;
import ua.oleksii.realestatebroker.repository.UserRepository;
import ua.oleksii.realestatebroker.service.VisitService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/stats")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class StatsController {
    private final PropertyRepository propertyRepo;
    private final UserRepository userRepo;
    private final FavoriteRepository favRepo;
    private final VisitService visitService;

    @GetMapping
    public StatsDTO getStats() {
        StatsDTO dto = new StatsDTO();
        dto.setTotalProperties(propertyRepo.count());
        Map<String, Long> byStatus = Arrays.stream(Property.Status.values())
                .collect(Collectors.toMap(
                        Enum::name,
                        status -> propertyRepo.countByStatus(status)
                ));
        dto.setPropertiesByStatus(byStatus);

        dto.setTotalUsers(userRepo.count());
        Map<String, Long> byRole = Arrays.stream(User.Role.values())
                .collect(Collectors.toMap(
                        Enum::name,
                        role -> userRepo.countByRole(role)
                ));
        dto.setUsersByRole(byRole);


        LocalDateTime since = LocalDate.now().minusDays(7).atStartOfDay();
        List<Object[]> raw = userRepo.countNewUsersByDay(since);

        List<StatsDTO.DayCount> byDay = raw.stream()
                .map(r -> {
                    String day = ((java.sql.Date) r[0]).toLocalDate().toString();
                    Long   count = (Long) r[1];
                    return new StatsDTO.DayCount(day, count);
                })
                .collect(Collectors.toList());

        dto.setNewUsersLast7Days(byDay);


        dto.setTotalFavorites(favRepo.count());

        dto.setVisits(visitService.getTotalVisits());

        return dto;
    }
}
