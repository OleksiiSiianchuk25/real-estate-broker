package ua.oleksii.realestatebroker.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ua.oleksii.realestatebroker.dto.PoiDto;
import ua.oleksii.realestatebroker.model.PointOfInterest;
import ua.oleksii.realestatebroker.repository.PoiRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/pois")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class PoiAdminController {

    private final PoiRepository repo;

    private PoiDto toDto(PointOfInterest e) {
        PoiDto d = new PoiDto();
        d.setId(e.getId());
        d.setName(e.getName());
        d.setCategory(e.getCategory());
        d.setLatitude(e.getLatitude());
        d.setLongitude(e.getLongitude());
        d.setOsmType(e.getOsmType());
        d.setOsmId(e.getOsmId());
        return d;
    }

    private PointOfInterest toEntity(PoiDto d) {
        PointOfInterest e = new PointOfInterest();
        if (d.getId() != null) e.setId(d.getId());
        e.setName(d.getName());
        e.setCategory(d.getCategory());
        e.setLatitude(d.getLatitude());
        e.setLongitude(d.getLongitude());
        // osmType/osmId — зазвичай не міняємо у CRUD, але підставимо як є
        e.setOsmType(d.getOsmType());
        e.setOsmId(d.getOsmId());
        // геометрію скласти вручну
        org.locationtech.jts.geom.GeometryFactory gf = new org.locationtech.jts.geom.GeometryFactory();
        e.setGeom(gf.createPoint(new org.locationtech.jts.geom.Coordinate(d.getLongitude(), d.getLatitude())));
        return e;
    }

    @GetMapping
    public List<PoiDto> listAll() {
        return repo.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public PoiDto getOne(@PathVariable Long id) {
        PointOfInterest e = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "POI не знайдено"));
        return toDto(e);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PoiDto create(@Valid @RequestBody PoiDto dto) {
        PointOfInterest saved = repo.save(toEntity(dto));
        return toDto(saved);
    }

    @PutMapping("/{id}")
    public PoiDto update(@PathVariable Long id, @Valid @RequestBody PoiDto dto) {
        PointOfInterest existing = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "POI не знайдено"));
        dto.setId(id);
        // можна оновити тільки name, category, coords
        existing.setName(dto.getName());
        existing.setCategory(dto.getCategory());
        existing.setLatitude(dto.getLatitude());
        existing.setLongitude(dto.getLongitude());
        existing.setGeom(new org.locationtech.jts.geom.GeometryFactory()
                .createPoint(new org.locationtech.jts.geom.Coordinate(dto.getLongitude(), dto.getLatitude())));
        return toDto(repo.save(existing));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        repo.deleteById(id);
    }
}
