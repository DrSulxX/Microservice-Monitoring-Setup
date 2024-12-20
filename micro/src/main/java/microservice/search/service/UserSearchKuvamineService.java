package microservice.search.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import microservice.search.model.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class UserSearchKuvamineService {

    // Loo vahemälu kasutajate jaoks
    private final Cache<Long, User> userIdCache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(1000)
            .build();

    private final Cache<String, List<User>> userEmailCache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(1000)
            .build();

    // Kuvamine vahemälust (näiteks kasutaja ID järgi)
    public List<User> getAllUsers() {
        return userEmailCache.asMap().values().stream().flatMap(List::stream).toList();
    }

    public User getUserById(Long id) {
        return userIdCache.getIfPresent(id);
    }

    public List<User> searchUsersByQuery(String query) {
        return userEmailCache.getIfPresent(query);  // Otsing päringute järgi
    }

    // Võite lisada ka andmete lisamise teenuse, kui vahemällu tuleb uusi andmeid
    public void addUserToCache(User user) {
        userIdCache.put(user.getId(), user);
    }
}