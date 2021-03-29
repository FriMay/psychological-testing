package may.code.api.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import may.code.api.dto.AckDTO;
import may.code.api.dto.SchoolClassDTO;
import may.code.api.exeptions.NotFoundException;
import may.code.api.factory.SchoolClassDTOFactory;
import may.code.store.entities.SchoolClassEntity;
import may.code.store.entities.SchoolEntity;
import may.code.store.repositories.SchoolClassRepository;
import may.code.store.repositories.SchoolRepository;
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

    SchoolClassDTOFactory schoolClassDTOFactory;

    public static final String FETCH_SCHOOL_CLASSES = "/api/schools/{schoolId}/classes";
    public static final String CREATE_SCHOOL_CLASS = "/api/schools/{schoolId}/classes/{className}";
    public static final String DELETE_SCHOOL_CLASS = "/api/schools/{schoolId}/classes/{classId}";

    @GetMapping(FETCH_SCHOOL_CLASSES)
    public ResponseEntity<List<SchoolClassDTO>> fetchSchoolClasses(
            @PathVariable Long schoolId,
            @RequestParam(defaultValue = "") String prefix) {

        SchoolEntity school = getSchoolOrThrowNotFound(schoolId);

        List<SchoolClassEntity> schoolClasses = school
                .getSchoolClasses()
                .stream()
                .filter(it -> it.getName().toLowerCase().startsWith(prefix.toLowerCase()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(schoolClassDTOFactory.createSchoolClassDTOList(schoolClasses));
    }

    @PostMapping(CREATE_SCHOOL_CLASS)
    public ResponseEntity<SchoolClassDTO> createSchoolClass(
            @PathVariable Long schoolId,
            @PathVariable String className) {

        SchoolEntity school = getSchoolOrThrowNotFound(schoolId);

        SchoolClassEntity schoolClass = schoolClassRepository
                .saveAndFlush(SchoolClassEntity.makeDefault(className.toUpperCase(), school));

        return ResponseEntity.ok(schoolClassDTOFactory.createSchoolClassDTO(schoolClass));
    }

    @DeleteMapping(DELETE_SCHOOL_CLASS)
    public ResponseEntity<AckDTO> deleteSchoolClass(
            @PathVariable Long schoolId,
            @PathVariable Long classId) {

        schoolClassRepository.deleteByIdAndSchoolId(classId, schoolId);

        return ResponseEntity.ok(AckDTO.makeDefault(true));
    }

    private SchoolEntity getSchoolOrThrowNotFound(Long schoolId) {
        return schoolRepository
                .findById(schoolId)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Школа с идентификатором \"%s\" не найдена.", schoolId))
                );
    }
}
