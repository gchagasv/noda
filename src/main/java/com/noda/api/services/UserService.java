package com.noda.api.services;

import com.noda.api.models.User;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    public List<User> findAllUsers() {
        List <User> users = new ArrayList<>();
        users.add(new User(1L, "Gabriel", "gabriel@email.com"));
        users.add(new User(2L, "Senior Mentor", "mentor@email.com"));
        return users;
    }
}
