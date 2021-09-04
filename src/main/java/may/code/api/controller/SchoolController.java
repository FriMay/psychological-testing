package may.code.api.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import may.code.api.dto.AckDto;
import may.code.api.dto.SchoolDto;
import may.code.api.exeptions.BadRequestException;
import may.code.api.factory.SchoolDtoFactory;
import may.code.api.services.ControllerAuthenticationService;
import may.code.api.store.entities.SchoolEntity;
import may.code.api.store.repositories.SchoolRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Controller
@Transactional
public class SchoolController {

    SchoolRepository schoolRepository;

    SchoolDtoFactory schoolDtoFactory;

    ControllerAuthenticationService authenticationService;

    public static final String FETCH_SCHOOLS = "/api/schools";
    public static final String CREATE_SCHOOL = "/api/schools/{schoolName}";
    public static final String DELETE_SCHOOL = "/api/schools/{schoolId}";

    @GetMapping(FETCH_SCHOOLS)
    public ResponseEntity<List<SchoolDto>> fetchSchools(@RequestParam(defaultValue = "") String filter) {

        boolean isFiltered = !filter.trim().isEmpty();

        List<SchoolEntity> schools = schoolRepository.findAllByFilter(isFiltered, filter);

        return ResponseEntity.ok(schoolDtoFactory.createSchoolDtoList(schools));
    }

    @PostMapping(CREATE_SCHOOL)
    public ResponseEntity<SchoolDto> createSchool(
            @PathVariable String schoolName,
            @RequestHeader(defaultValue = "") String token) {

        authenticationService.authenticate(token);

        if (schoolRepository.existsByName(schoolName)) {
            throw new BadRequestException(String.format("Школа с названием \"%s\" уже существует.", schoolName));
        }

        SchoolEntity school = schoolRepository.saveAndFlush(
                SchoolEntity.makeDefault(schoolName)
        );

        return ResponseEntity.ok(schoolDtoFactory.createSchoolDto(school));
    }

    @DeleteMapping(DELETE_SCHOOL)
    public ResponseEntity<AckDto> deleteSchool(
            @PathVariable Long schoolId,
            @RequestHeader(defaultValue = "") String token) {

        authenticationService.authenticate(token);

        if (schoolRepository.existsById(schoolId)) {
            schoolRepository.deleteById(schoolId);
        }

        return ResponseEntity.ok(AckDto.makeDefault(true));
    }
}
