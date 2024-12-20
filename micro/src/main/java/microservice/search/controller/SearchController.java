package microservice.search.controller;

import microservice.search.model.User;
import microservice.search.service.UserSearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final UserSearchService userSearchService;

    public SearchController(UserSearchService userSearchService) {
        this.userSearchService = userSearchService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userSearchService.getAllUsers());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userSearchService.getUserById(id);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<List<User>> getUsersByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userSearchService.getUsersByEmail(email));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<List<User>> getUsersByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userSearchService.searchUsersByUsername(username));
    }

    @GetMapping("/query/{query}")
    public ResponseEntity<List<User>> searchUsers(@PathVariable String query) {
        return ResponseEntity.ok(userSearchService.searchUsersByQuery(query));
    }
}
