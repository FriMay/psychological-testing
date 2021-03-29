package may.code.api.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import may.code.api.domains.UserRole;
import may.code.api.dto.AckDTO;
import may.code.api.dto.UserDTO;
import may.code.api.exeptions.NotFoundException;
import may.code.api.factory.UserDTOFactory;
import may.code.store.entities.SchoolClassEntity;
import may.code.store.entities.UserEntity;
import may.code.store.repositories.SchoolClassRepository;
import may.code.store.repositories.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Controller
@Transactional
public class UserController {

    UserRepository userRepository;

    SchoolClassRepository schoolClassRepository;

    UserDTOFactory userDTOFactory;

    public static final String FETCH_USERS = "/api/schools/classes/users";
    public static final String FETCH_USERS_BY_CLASS = "/api/classes/{classId}/users";
    public static final String CREATE_USER = "/api/schools/classes/{classId}/users";
    public static final String DELETE_USER = "/api/schools/classes/users/{userId}";

    @GetMapping(FETCH_USERS_BY_CLASS)
    public ResponseEntity<List<UserDTO>> fetchUsersByClass(
            @RequestParam(defaultValue = "") String filter,
            @PathVariable Long classId) {

        boolean isFiltered = !filter.trim().isEmpty();

        List<UserEntity> users = userRepository.findAllByFilterAndClass(isFiltered, filter, classId);

        return ResponseEntity.ok(userDTOFactory.createUserDTOList(users));
    }

    @GetMapping(FETCH_USERS)
    public ResponseEntity<List<UserDTO>> fetchUsers(@RequestParam(defaultValue = "") String filter) {

        boolean isFiltered = !filter.trim().isEmpty();

        List<UserEntity> users = userRepository.findAllByFilter(isFiltered, filter);

        return ResponseEntity.ok(userDTOFactory.createUserDTOList(users));
    }

    @PostMapping(CREATE_USER)
    public ResponseEntity<UserDTO> createUsers(
            @RequestParam Instant birthday,
            @RequestParam String fullName,
            @RequestParam UserRole userRole,
            @PathVariable Long classId) {

        SchoolClassEntity schoolClass = schoolClassRepository
                .findById(classId)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Класс с идентификатором \"%s\" не найден.", classId))
                );

        UserEntity user = userRepository.saveAndFlush(
                UserEntity.makeDefault(fullName, birthday, userRole, schoolClass)
        );

        return ResponseEntity.ok(userDTOFactory.createUserDTO(user));
    }

    @DeleteMapping(DELETE_USER)
    public ResponseEntity<AckDTO> deleteUser(@PathVariable Long userId) {

        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
        }

        return ResponseEntity.ok(AckDTO.makeDefault(true));
    }
}
