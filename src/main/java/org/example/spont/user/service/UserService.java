package org.example.spont.user.service;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.example.spont.user.entity.Gender;
import org.example.spont.user.entity.User;
import org.example.spont.user.entity.UserType;
import org.example.spont.user.repository.UserRepo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo  userRepo;
    private final PasswordEncoder passwordEncoder;

    public Optional<User> findUserByEmail(String email){
        return userRepo.findByEmail(email);
    }

    public Optional<User> findUserById(UUID id){
        return userRepo.findById(id);
    }

    public Optional<User> findUserByPhone(String phone){
        return userRepo.findByPhone(phone);
    }

    @Transactional
    public Optional<User> findByIdentifier(String identifier){;
        return userRepo.findByIdentifier(identifier);
    }

    @Transactional
    public User register(String name, String phone, String email, Gender gender, String password) {

        Optional<User> existingUserOpt = userRepo.findByPhone(phone);

        // CASE 1: User does not exist → create new REGISTERED user
        if (existingUserOpt.isEmpty()) {

            if (email != null && userRepo.findByEmail(email).isPresent()) {
                throw new RuntimeException("Email already in use");
            }

            User newUser = new User();
            newUser.setName(name);
            newUser.setPhone(phone);
            newUser.setEmail(email);
            newUser.setGender(gender);
            newUser.setPassword(passwordEncoder.encode(password)); // later encode password
            newUser.setUserType(UserType.REGISTERED);

            User saveduser = userRepo.save(newUser);

            return saveduser;
        }

        User existingUser = existingUserOpt.get();

        // CASE 2: Guest exists → upgrade guest to REGISTERED
        if (existingUser.getUserType() == UserType.GUEST) {

            if (email != null && userRepo.findByEmail(email).isPresent()) {
                throw new RuntimeException("Email already in use");
            }

            existingUser.setName(name);
            existingUser.setEmail(email);
            existingUser.setPassword(passwordEncoder.encode(password)); // later encode password
            existingUser.setUserType(UserType.REGISTERED);

            User saved = userRepo.save(existingUser);

            return saved;
        }

        // CASE 3: Already registered
        throw new RuntimeException("User already registered");
    }


    @Transactional
    public String guestRegistration(String name, String phone){

        Optional<User> existingUser = userRepo.findByPhone(phone);
        if(existingUser.isEmpty()){
            User user = new User();
            user.setName(name);
            user.setPhone(phone);
            user.setUserType(UserType.GUEST);
            userRepo.save(user);

            return "Guest registered successfully";
        }

        return "Guest registered successfully";
    }


    public User guestRegistration(String name, String phone, Gender gender){
        User user = new User();
        user.setName(name);
        user.setPhone(phone);
        user.setUserType(UserType.GUEST);
        user.setGender(gender);

        return userRepo.save(user);
    }
}
