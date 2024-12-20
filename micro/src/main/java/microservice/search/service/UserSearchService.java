package microservice.search.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import microservice.search.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class UserSearchService {

    private static final Logger logger = LoggerFactory.getLogger(UserSearchService.class);
    private final RestTemplate restTemplate;
    private final Cache<Long, User> userIdCache;
    private final Cache<String, List<User>> userEmailCache;
    private final MeterRegistry meterRegistry;

    public UserSearchService(RestTemplate restTemplate, MeterRegistry meterRegistry) {
        this.restTemplate = restTemplate;
        this.meterRegistry = meterRegistry;
        this.userIdCache = Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(1000)
                .build();
        this.userEmailCache = Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(1000)
                .build();
    }

    public List<User> getAllUsers() {
        try {
            return Timer.builder("api.requests")
                    .tag("method", "getAllUsers")
                    .tag("status", "success")
                    .register(meterRegistry)
                    .record(() -> {
                        String apiUrl = "http://localhost:8080/api/search/all";
                        return restTemplate.exchange(apiUrl, HttpMethod.GET, null,
                                new ParameterizedTypeReference<List<User>>() {}).getBody();
                    });
        } catch (Exception e) {
            logger.error("Error in getAllUsers", e);
            Timer.builder("api.requests")
                    .tag("method", "getAllUsers")
                    .tag("status", "failure")
                    .register(meterRegistry)
                    .record(() -> {});
            throw e;
        }
    }

    public User getUserById(Long id) {
        try {
            return Timer.builder("api.requests")
                    .tag("method", "getUserById")
                    .tag("status", "success")
                    .register(meterRegistry)
                    .record(() -> userIdCache.get(id, key -> {
                        String apiUrl = "http://localhost:8080/api/search/id/" + id;
                        return restTemplate.getForObject(apiUrl, User.class);
                    }));
        } catch (Exception e) {
            logger.error("Error in getUserById for ID: {}", id, e);
            Timer.builder("api.requests")
                    .tag("method", "getUserById")
                    .tag("status", "failure")
                    .register(meterRegistry)
                    .record(() -> {});
            throw e;
        }
    }

    public List<User> getUsersByEmail(String email) {
        try {
            return Timer.builder("api.requests")
                    .tag("method", "getUsersByEmail")
                    .tag("status", "success")
                    .register(meterRegistry)
                    .record(() -> userEmailCache.get(email.toLowerCase(), key -> {
                        String apiUrl = "http://localhost:8080/api/search/email/" + email;
                        return restTemplate.exchange(apiUrl, HttpMethod.GET, null,
                                new ParameterizedTypeReference<List<User>>() {}).getBody();
                    }));
        } catch (Exception e) {
            logger.error("Error in getUsersByEmail for email: {}", email, e);
            Timer.builder("api.requests")
                    .tag("method", "getUsersByEmail")
                    .tag("status", "failure")
                    .register(meterRegistry)
                    .record(() -> {});
            throw e;
        }
    }

    public List<User> searchUsersByUsername(String username) {
        try {
            return Timer.builder("api.requests")
                    .tag("method", "searchUsersByUsername")
                    .tag("status", "success")
                    .register(meterRegistry)
                    .record(() -> {
                        String apiUrl = "http://localhost:8080/api/search/username/" + username;
                        return restTemplate.exchange(apiUrl, HttpMethod.GET, null,
                                new ParameterizedTypeReference<List<User>>() {}).getBody();
                    });
        } catch (Exception e) {
            logger.error("Error in searchUsersByUsername for username: {}", username, e);
            Timer.builder("api.requests")
                    .tag("method", "searchUsersByUsername")
                    .tag("status", "failure")
                    .register(meterRegistry)
                    .record(() -> {});
            throw e;
        }
    }

    public List<User> searchUsersByQuery(String query) {
        try {
            return Timer.builder("api.requests")
                    .tag("method", "searchUsersByQuery")
                    .tag("status", "success")
                    .register(meterRegistry)
                    .record(() -> {
                        Set<User> result = new HashSet<>();
                        result.addAll(getUsersByEmail(query));
                        result.addAll(searchUsersByUsername(query));
                        return result.stream().collect(Collectors.toList());
                    });
        } catch (Exception e) {
            logger.error("Error in searchUsersByQuery for query: {}", query, e);
            Timer.builder("api.requests")
                    .tag("method", "searchUsersByQuery")
                    .tag("status", "failure")
                    .register(meterRegistry)
                    .record(() -> {});
            throw e;
        }
    }
}