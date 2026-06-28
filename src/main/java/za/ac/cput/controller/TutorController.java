/*
Tutor Controller
Author Imaan Achmat - 230458971
Date: 28/06/2026
 */

package za.ac.cput.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.Tutor;
import za.ac.cput.service.TutorService;
import java.util.List;

@RestController
@RequestMapping ("/tutor")
public class TutorController {

    private TutorService service;

    @Autowired
    public TutorController(TutorService service) {
        this.service = service;
    }

    @PostMapping("/create")
    public Tutor create(@RequestBody Tutor tutor) {
        return service.create(tutor);
    }

    @GetMapping("/read/{tutorId}")
    public Tutor read(@PathVariable String tutorId) {
        return service.read(tutorId);
    }

    @PutMapping("/update")
    public Tutor update(@RequestBody Tutor tutor) {
        return service.update(tutor);
    }

    @DeleteMapping("/delete/{tutorId}")
    public boolean delete(@PathVariable String tutorId) {
        return service.delete(tutorId);
    }

    @GetMapping("/getAll")
    public List <Tutor> getAll() {
        return service.getAll();
    }
}
