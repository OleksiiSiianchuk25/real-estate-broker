package ua.oleksii.realestatebroker.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class StatsDTO {
    private long totalProperties;

    private Map<String, Long> propertiesByStatus;

    private long totalUsers;

    private Map<String, Long> usersByRole;


    private long totalFavorites;

    private long visits;

    private List<DayCount> newUsersLast7Days;

    public static record DayCount(String date, long count) {}

}
