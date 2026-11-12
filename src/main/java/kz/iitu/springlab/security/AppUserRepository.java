package kz.iitu.springlab.security;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

@Repository
public class AppUserRepository {
    private final JdbcTemplate jdbc;

    public AppUserRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Optional<AppUser> findByUsername(String username) {
        List<AppUser> found = jdbc.query("""
                select u.id, u.username, u.password_hash, u.enabled,
                       coalesce(string_agg(r.role, ',' order by r.role), '') as roles
                from app_user u left join user_role r on r.user_id = u.id
                where u.username = ?
                group by u.id, u.username, u.password_hash, u.enabled
                """, (rs, rowNum) -> new AppUser(rs.getLong("id"), rs.getString("username"),
                rs.getString("password_hash"), rs.getBoolean("enabled"),
                roles(rs.getString("roles"))), username);
        return found.stream().findFirst();
    }

    public List<AppUser> findAll() {
        return jdbc.query("""
                select u.id, u.username, u.password_hash, u.enabled,
                       coalesce(string_agg(r.role, ',' order by r.role), '') as roles
                from app_user u left join user_role r on r.user_id = u.id
                group by u.id, u.username, u.password_hash, u.enabled
                order by u.username
                """, (rs, rowNum) -> new AppUser(rs.getLong("id"), rs.getString("username"),
                rs.getString("password_hash"), rs.getBoolean("enabled"),
                roles(rs.getString("roles"))));
    }

    private LinkedHashSet<String> roles(String csv) {
        return csv == null || csv.isBlank() ? new LinkedHashSet<>() : new LinkedHashSet<>(List.of(csv.split(",")));
    }

    public boolean existsByUsername(String username) {
        Boolean exists = jdbc.queryForObject("select exists(select 1 from app_user where username = ?)", Boolean.class, username);
        return Boolean.TRUE.equals(exists);
    }

    public Optional<Long> insertUser(String username, String passwordHash) {
        return jdbc.query("insert into app_user (username, password_hash) values (?, ?) " +
                        "on conflict (username) do nothing returning id",
                (rs, rowNum) -> rs.getLong("id"), username, passwordHash).stream().findFirst();
    }

    public void addRole(long userId, String role) {
        jdbc.update("insert into user_role (user_id, role) values (?, ?)", userId, role);
    }
}
