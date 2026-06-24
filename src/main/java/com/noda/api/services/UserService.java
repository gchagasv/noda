package com.noda.api.services;


import com.noda.api.dtos.UserRequestDTO;
import com.noda.api.exceptions.CpfAlreadyRegisteredException;
import com.noda.api.exceptions.EmailAlreadyRegisteredException;
import com.noda.api.exceptions.UserNotFoundException;
import com.noda.api.models.Address;
import com.noda.api.models.User;
import com.noda.api.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ViaCepService viaCepService;
    private final PasswordEncoder passwordEncoder;

    public User save(UserRequestDTO dto) {
        validateUniqueFields(dto);
        User user = convertToEntity(dto);
        return userRepository.save(user);
    }

    public Page<User> findAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
    }

    private void validateUniqueFields(UserRequestDTO dto) {
        if (userRepository.findByCpf(dto.cpf()).isPresent()) {
            throw new CpfAlreadyRegisteredException("This CPF is already registered.");
        }
        if (userRepository.findByEmail(dto.email()).isPresent()) {
            throw new EmailAlreadyRegisteredException("This Email is already registered.");
        }
    }

    private User convertToEntity(UserRequestDTO dto) {
        User user = new User();
        user.setName(dto.name());
        user.setCpf(dto.cpf());
        user.setEmail(dto.email());
        user.setBirthday(dto.birthday());
        user.setPassword(passwordEncoder.encode(dto.password()));

        if (dto.address() != null && dto.address().cep() != null) {
            String targetCep = dto.address().cep();
            var cepData = viaCepService.fetchAddressByCep(targetCep);

            Address address = new Address();
            address.setCep(targetCep);
            address.setNumber(dto.address().number());
            address.setComplement(dto.address().complement());
            address.setStreet(cepData.logradouro());
            address.setNeighborhood(cepData.bairro());
            address.setCity(cepData.localidade());
            address.setState(cepData.uf());

            address.setUser(user);
            user.setAddress(address);
        }
        return user;
    }
}