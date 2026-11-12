
package za.ac.cput.controller;

/*
Subject Controller
Author: Wendy Bayise
222828978
01/07/2026
*/

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import za.ac.cput.domain.Subject;
import za.ac.cput.service.SubjectService;
import za.ac.cput.util.Helper;

@RestController
@RequestMapping("/subject")
public class SubjectController {

    private SubjectService service;

    // Receives the subject service when this controller is created.
    @Autowired
    public SubjectController(SubjectService service) {
        this.service = service;
    }

    // Creates a new subject.
    // Only an admin is allowed to add subjects.
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public Subject create(@RequestBody Subject subject) {

        // Checks that the subject details have been provided.
        checkSubject(subject);

        // Saves the subject and returns the created record.
        return service.create(subject);
    }

    // Finds a subject using its subject code.
    // This endpoint can be accessed without an admin role.
    @GetMapping("/read/{subjectCode}")
    public Subject read(@PathVariable String subjectCode) {

        // Searches for the subject using its code.
        Subject subject = service.read(subjectCode);

        // Returns an error if the subject does not exist.
        if (subject == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Subject not found: " + subjectCode);
        }

        return subject;
    }

    // Updates an existing subject.
    // Only an admin is allowed to update subjects.
    @PutMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    public Subject update(@RequestBody Subject subject) {

        // Checks that the subject details are valid.
        checkSubject(subject);

        // Checks whether the subject already exists.
        if (service.read(subject.getSubjectCode()) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Subject not found: " + subject.getSubjectCode() + ". Create it first.");
        }

        // Saves the updated subject details.
        return service.update(subject);
    }

    // Deletes a subject using its subject code.
    // Only an admin is allowed to delete subjects.
    @DeleteMapping("/delete/{subjectCode}")
    @PreAuthorize("hasRole('ADMIN')")
    public boolean delete(@PathVariable String subjectCode) {

        // Checks whether the subject exists before deleting it.
        if (service.read(subjectCode) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Subject not found: " + subjectCode);
        }

        // Deletes the subject and returns the result.
        return service.delete(subjectCode);
    }

    // Checks that all required subject details have been provided.
    // This prevents invalid data from reaching the database.
    private void checkSubject(Subject subject) {

        // Checks whether a subject object was provided.
        if (subject == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A subject is required.");
        }

        // Checks whether the subject code is empty or missing.
        if (Helper.isNullOrEmpty(subject.getSubjectCode())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "subjectCode is required.");
        }

        // Checks whether the subject name is empty or missing.
        if (Helper.isNullOrEmpty(subject.getSubjectName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "subjectName is required.");
        }
    }

    // Returns the list of all subjects.
    @GetMapping("/getAll")
    public List<Subject> getAll() {
        return service.getAll();
    }
}
