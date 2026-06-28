package za.ac.cput.controller;
/*
TutorSubjectController.java
TutorSubject Controller
Author: Charmaine Dlamini - 222056401
Date: 28/06/2026
*/
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import za.ac.cput.domain.TutorSubject;
import za.ac.cput.domain.TutorSubjectId;
import za.ac.cput.service.TutorSubjectService;

@RestController
@RequestMapping("/tutorsubject")
public class TutorSubjectController {

        private TutorSubjectService service;

        @Autowired
        public TutorSubjectController(TutorSubjectService service) {
            this.service = service;
        }

        @PostMapping("/create")
        public TutorSubject create(@RequestBody TutorSubject tutorSubject) {
            return service.create(tutorSubject);
        }

        @GetMapping("/read/{subjectCode}/{tutorId}")
        public TutorSubject read(@PathVariable String subjectCode,
                                 @PathVariable String tutorId) {

            TutorSubjectId id = new TutorSubjectId(subjectCode, tutorId);
            return service.read(id);
        }

        @PutMapping("/update")
        public TutorSubject update(@RequestBody TutorSubject tutorSubject) {
            return service.update(tutorSubject);
        }

        @DeleteMapping("/delete/{subjectCode}/{tutorId}")
        public boolean delete(@PathVariable String subjectCode,
                              @PathVariable String tutorId) {

            TutorSubjectId id = new TutorSubjectId(subjectCode, tutorId);
            return service.delete(id);
        }

        @GetMapping("/getAll")
        public List<TutorSubject> getAll() {
            return service.getAll();
        }
    }
