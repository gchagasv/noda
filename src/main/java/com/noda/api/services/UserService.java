        package com.noda.api.services;

        import com.noda.api.exceptions.CpfAlreadyRegisteredException;
        import com.noda.api.exceptions.EmailAlreadyRegisteredException;
        import com.noda.api.models.User;
        import com.noda.api.repositories.UserRepository;
        import lombok.RequiredArgsConstructor;
        import org.springframework.data.domain.Page;
        import org.springframework.data.domain.Pageable;
        import org.springframework.stereotype.Service;
        import java.util.Optional;


        @Service
        @RequiredArgsConstructor
        public class UserService {

            private final UserRepository userRepository;

            public User save(User user) {

                Optional<User> userWithTheSameCpf = userRepository.findByCpf(user.getCpf());
                    if (userWithTheSameCpf.isPresent()) {
                        throw new CpfAlreadyRegisteredException("This CPF is already registered.");
                    }

                    Optional<User> userWithTheSameEmail= userRepository.findByEmail(user.getEmail());
                    if(userWithTheSameEmail.isPresent()) {
                        throw new EmailAlreadyRegisteredException("This Email is already registered.");
                    }

                return userRepository.save(user);
            }

            public Page<User> findAllUsers(Pageable pageable) {
                return userRepository.findAll(pageable);
            }
        }
