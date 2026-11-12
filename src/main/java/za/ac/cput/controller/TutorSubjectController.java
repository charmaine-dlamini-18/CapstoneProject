
package za.ac.cput.controller;

/*
TutorSubjectController.java
TutorSubject Controller
Author: Charmaine Dlamini - 222056401
Date: 28/06/2026
*/

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import za.ac.cput.domain.TutorSubject;
import za.ac.cput.domain.TutorSubjectId;
import za.ac.cput.service.TutorSubjectService;
import za.ac.cput.util.Helper;

@RestController
@RequestMapping("/tutorsubject")
public class TutorSubjectController {

    private TutorSubjectService service;

    // Receives the tutor subject service when this controller is created.
    @Autowired
    public TutorSubjectController(TutorSubjectService service) {
        this.service = service;
    }

    // Assigns a subject to a tutor.
    // Only an admin or the tutor who owns the record can perform this action.
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN') or @accessChecker.isTutor(#tutorSubject.tutorId, authentication)")
    public TutorSubject create(@RequestBody TutorSubject tutorSubject,
                               Authentication authentication) {

        // Checks that the subject code and tutor ID have been provided.
        checkTutorSubject(tutorSubject);

        // Saves the tutor and subject assignment.
        return service.create(tutorSubject);
    }

    // Finds a subject assigned to a specific tutor.
    // Uses both the subject code and tutor ID to identify the record.
    @GetMapping("/read/{subjectCode}/{tutorId}")
    public TutorSubject read(@PathVariable String subjectCode,
                             @PathVariable String tutorId) {

        // Creates an ID using the subject code and tutor ID.
        TutorSubjectId id = new TutorSubjectId(subjectCode, tutorId);

        // Searches for the tutor subject assignment.
        TutorSubject found = service.read(id);

        // Returns an error if the assignment does not exist.
        if (found == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Tutor " + tutorId + " does not teach subject " + subjectCode + ".");
        }

        return found;
    }

    // Updates an existing tutor subject assignment.
    // Only an admin or the tutor who owns the record can perform this action.
    @PutMapping("/update")
    @PreAuthorize("hasRole('ADMIN') or @accessChecker.isTutor(#tutorSubject.tutorId, authentication)")
    public TutorSubject update(@RequestBody TutorSubject tutorSubject,
                               Authentication authentication) {

        // Checks that the required details have been provided.
        checkTutorSubject(tutorSubject);

        // Creates an ID using the subject code and tutor ID.
        TutorSubjectId id = new TutorSubjectId(
                tutorSubject.getSubjectCode(),
                tutorSubject.getTutorId()
        );

        // Checks whether the tutor subject assignment already exists.
        if (service.read(id) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Tutor " + tutorSubject.getTutorId()
                            + " does not teach subject "
                            + tutorSubject.getSubjectCode()
                            + ". Create it first.");
        }

        // Saves the updated tutor subject assignment.
        return service.update(tutorSubject);
    }

    // Deletes a subject assignment from a tutor.
    // Only an admin or the tutor who owns the record can perform this action.
    @DeleteMapping("/delete/{subjectCode}/{tutorId}")
    @PreAuthorize("hasRole('ADMIN') or @accessChecker.isTutor(#tutorId, authentication)")
    public boolean delete(@PathVariable String subjectCode,
                          @PathVariable String tutorId,
                          Authentication authentication) {

        // Creates an ID using the subject code and tutor ID.
        TutorSubjectId id = new TutorSubjectId(subjectCode, tutorId);

        // Attempts to delete the tutor subject assignment.
        boolean deleted = service.delete(id);

        // Returns an error if the assignment could not be deleted.
        if (!deleted) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Tutor " + tutorId + " does not teach subject " + subjectCode + ".");
        }

        return true;
    }

    // Checks that the tutor subject details are provided.
    // This prevents incomplete records from being saved.
    private void checkTutorSubject(TutorSubject tutorSubject) {

        // Checks whether a tutor subject object was provided.
        if (tutorSubject == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "subjectCode and tutorId are required.");
        }

        // Checks whether the subject code is missing.
        if (Helper.isNullOrEmpty(tutorSubject.getSubjectCode())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "subjectCode is required.");
        }

        // Checks whether the tutor ID is missing.
        if (Helper.isNullOrEmpty(tutorSubject.getTutorId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "tutorId is required.");
        }
    }

    // Returns all tutor subject assignments.
    @GetMapping("/getAll")
    public List<TutorSubject> getAll() {
        return service.getAll();
    }
}
