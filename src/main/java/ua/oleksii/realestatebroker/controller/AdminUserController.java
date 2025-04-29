package ua.oleksii.realestatebroker.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ua.oleksii.realestatebroker.model.User;
import ua.oleksii.realestatebroker.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {
    private final UserService userService;
    public AdminUserController(UserService svc){ this.userService = svc; }

    @GetMapping
    public List<User> listAll() {
        return userService.findAll();
    }

    @PostMapping
    public User create(@RequestBody User u) {
        return userService.save(u);
    }

    @GetMapping("{id}")
    public ResponseEntity<User> get(@PathVariable Long id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("{id}")
    public User update(@PathVariable Long id, @RequestBody User u) {
        u.setId(id);
        return userService.save(u);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Long id) {
        userService.deleteById(id);
    }
}
