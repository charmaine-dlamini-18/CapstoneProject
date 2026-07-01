package za.ac.cput.controller;
/*
 Subject Contoller
 Author: Wendy Bayise
 222828978
 01/07/2026
 */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import za.ac.cput.domain.Subject;
import za.ac.cput.service.SubjectService;

@RestController
@RequestMapping("/subject")
public class SubjectController {

    private SubjectService service;

    @Autowired
    public SubjectController(SubjectService service){
        this.service = service;
    }

    @PostMapping("/create")
    public Subject create(@RequestBody Subject subject){
        return service.create(subject);
    }

    @GetMapping("/read/{subjectCode}")
    public Subject read(@PathVariable String subjectCode){
        return service.read(subjectCode);
    }

    @PutMapping("/update")
    public Subject update(@RequestBody Subject subject){
        return service.update(subject);
    }

    @DeleteMapping("/delete/{subjectCode}")
    public boolean delete(@PathVariable String subjectCode){
        return service.delete(subjectCode);
    }

    @GetMapping("/getAll")
    public List<Subject> getAll(){
        return service.getAll();
    }
}


