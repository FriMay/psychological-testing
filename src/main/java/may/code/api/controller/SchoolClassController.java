package may.code.api.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import may.code.api.dto.AckDto;
import may.code.api.dto.SchoolClassDto;
import may.code.api.exeptions.NotFoundException;
import may.code.api.factory.SchoolClassDtoFactory;
import may.code.api.services.ControllerAuthenticationService;
import may.code.api.store.entities.SchoolClassEntity;
import may.code.api.store.entities.SchoolEntity;
import may.code.api.store.repositories.SchoolClassRepository;
import may.code.api.store.repositories.SchoolRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Controller
@Transactional
public class SchoolClassController {

    SchoolRepository schoolRepository;

    SchoolClassRepository schoolClassRepository;

    SchoolClassDtoFactory schoolClassDtoFactory;

    ControllerAuthenticationService authenticationService;

    public static final String FETCH_SCHOOL_CLASSES = "/api/schools/{schoolId}/classes";
    public static final String CREATE_SCHOOL_CLASS = "/api/schools/{schoolId}/classes/{className}";
    public static final String DELETE_SCHOOL_CLASS = "/api/schools/{schoolId}/classes/{classId}";

    @GetMapping(FETCH_SCHOOL_CLASSES)
    public ResponseEntity<List<SchoolClassDto>> fetchSchoolClasses(
            @PathVariable Long schoolId,
            @RequestParam(defaultValue = "") String prefix) {

        SchoolEntity school = getSchoolOrThrowNotFound(schoolId);

        List<SchoolClassEntity> schoolClasses = school
                .getSchoolClasses()
                .stream()
                .filter(it -> it.getName().toLowerCase().startsWith(prefix.toLowerCase()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(schoolClassDtoFactory.createSchoolClassDtoList(schoolClasses));
    }

    @PostMapping(CREATE_SCHOOL_CLASS)
    public ResponseEntity<SchoolClassDto> createSchoolClass(
            @PathVariable Long schoolId,
            @PathVariable String className,
            @RequestHeader(defaultValue = "") String token) {

        authenticationService.authenticate(token);

        SchoolEntity school = getSchoolOrThrowNotFound(schoolId);

        SchoolClassEntity schoolClass = schoolClassRepository
                .saveAndFlush(SchoolClassEntity.makeDefault(className.toUpperCase(), school));

        return ResponseEntity.ok(schoolClassDtoFactory.createSchoolClassDto(schoolClass));
    }

    @DeleteMapping(DELETE_SCHOOL_CLASS)
    public ResponseEntity<AckDto> deleteSchoolClass(
            @PathVariable Long schoolId,
            @PathVariable Long classId,
            @RequestHeader(defaultValue = "") String token) {

        authenticationService.authenticate(token);

        schoolClassRepository.deleteByIdAndSchoolId(classId, schoolId);

        return ResponseEntity.ok(AckDto.makeDefault(true));
    }

    private SchoolEntity getSchoolOrThrowNotFound(Long schoolId) {
        return schoolRepository
                .findById(schoolId)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Школа с идентификатором \"%s\" не найдена.", schoolId))
                );
    }
}
