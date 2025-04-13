package ru.hogvartz.school.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogvartz.school.Model.Faculty;
import ru.hogvartz.school.Model.Student;
import ru.hogvartz.school.Service.StudentService;

import java.util.Collection;


@RestController
@RequestMapping("student")
public class StudentController {
    public final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("{id}")
    public Student getStudent(@PathVariable Long id) {
        return studentService.getStudent(id);
    }

    @GetMapping("/all")
    public Collection<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    @GetMapping("filter/{age}")
    public Collection<Student> getStudentsForAge(@PathVariable int age) {
        return studentService.getStudentsForAge(age);
    }

    @GetMapping("filter/{min}/{max}")
    public Collection<Student> getStudentsBetweenAge(@PathVariable int min, @PathVariable int max) {
        return studentService.getStudentsBetweenAge(min, max);
    }

    @GetMapping("faculty/{id}")
    public Faculty getFaculty(@PathVariable Long id) {
        return studentService.getStudent(id).getFaculty();
    }


    @PutMapping
    public Student updateStudent(@RequestBody Student student) {
        return studentService.updateStudent(student);
    }

    @PostMapping
    public Student addStudent(Student student) {
        return studentService.createStudent(student);
    }

    @DeleteMapping("{id}")
    public ResponseEntity deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok().build();
    }
}
