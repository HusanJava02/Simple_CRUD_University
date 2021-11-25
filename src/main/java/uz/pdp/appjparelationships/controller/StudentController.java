package uz.pdp.appjparelationships.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import uz.pdp.appjparelationships.entity.Address;
import uz.pdp.appjparelationships.entity.Group;
import uz.pdp.appjparelationships.entity.Student;
import uz.pdp.appjparelationships.entity.Subject;
import uz.pdp.appjparelationships.payload.StudentDto;
import uz.pdp.appjparelationships.repository.AddressRepository;
import uz.pdp.appjparelationships.repository.GroupRepository;
import uz.pdp.appjparelationships.repository.StudentRepository;
import uz.pdp.appjparelationships.repository.SubjectRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/student")
public class StudentController {
    @Autowired
    StudentRepository studentRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    //1. VAZIRLIK
    @GetMapping("/forMinistry")
    public Page<Student> getStudentListForMinistry(@RequestParam int page) {
        //1-1=0     2-1=1    3-1=2    4-1=3
        //select * from student limit 10 offset (0*10)
        //select * from student limit 10 offset (1*10)
        //select * from student limit 10 offset (2*10)
        //select * from student limit 10 offset (3*10)
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAll(pageable);
        return studentPage;
    }

    //2. UNIVERSITY
    @GetMapping("/forUniversity/{universityId}")
    public Page<Student> getStudentListForUniversity(@PathVariable Integer universityId,
                                                     @RequestParam int page) {
        //1-1=0     2-1=1    3-1=2    4-1=3
        //select * from student limit 10 offset (0*10)
        //select * from student limit 10 offset (1*10)
        //select * from student limit 10 offset (2*10)
        //select * from student limit 10 offset (3*10)
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAllByGroup_Faculty_UniversityId(universityId, pageable);
        return studentPage;
    }

    //3. FACULTY DEKANAT
    //4. GROUP OWNER

    @GetMapping(value = "/faculty/{facultyId}")
    public Page<Student> getStudentsByDekanatId(@RequestParam(name = "start", defaultValue = "1") Integer start,
                                                @PathVariable Integer facultyId) {
        Pageable pageable = PageRequest.of(start, 10);
        Page<Student> studentPage = studentRepository.findAllByGroup_FacultyId(facultyId, pageable);
        Page<Student> byFacultyId = studentRepository.findByFacultyId(facultyId, pageable);
        return studentPage;
    }


    //Teacher yani gruppa kuratori id si orqali Studentlarni olish
    @GetMapping(value = "/faculty/groupOwner/ownerId")
    public List<Student> getStudentByOwnerId(@PathVariable Integer ownerId) {
        return studentRepository.findAllByGroup_Teacher_Id(ownerId);
    }

    @PostMapping
    public String addStudent(@RequestBody StudentDto studentDto) {
        Integer addressId = studentDto.getAddressId();
        Integer groupId = studentDto.getGroupId();
        Student student = new Student();
        student.setFirstName(studentDto.getFirstName());
        student.setLastName(studentDto.getLastName());
        List<Integer> subjects = studentDto.getSubjects();
        List<Subject> subjectsAll = subjectRepository.findAllById(subjects);
        student.setSubjects(subjectsAll);
        Optional<Address> optionalAddress = addressRepository.findById(addressId);
        if (optionalAddress.isPresent()) {
            Address address = optionalAddress.get();
            student.setAddress(address);

        } else return "Address of student not found , please insert address of the Student first";
        Optional<Group> optionalGroup = groupRepository.findById(studentDto.getGroupId());
        if (optionalGroup.isPresent()) {
            Group group = optionalGroup.get();
            student.setGroup(group);
        } else return "Group of this student not found";

        studentRepository.save(student);
        return "successfully saved student";
    }

    @PutMapping(value = "/id")
    public String editStudent(@RequestBody StudentDto studentDto, @PathVariable Integer id) {
        String summary = "";
        Optional<Student> optional = studentRepository.findById(id);
        if (optional.isPresent()) {
            Student student = optional.get();
            student.setLastName(studentDto.getLastName());
            student.setFirstName(studentDto.getFirstName());
            Group studentGroup = student.getGroup();
            Optional<Group> optionalGroup = groupRepository.findById(studentDto.getGroupId());
            if (optionalGroup.isPresent()) {
                Group groupFromDb = optionalGroup.get();
                student.setGroup(groupFromDb);
                summary += ", group changed ";
            } else summary += ", group not found ";
            Optional<Address> optionalAddress = addressRepository.findById(studentDto.getAddressId());
            if (optionalAddress.isPresent()) {
                Address address = optionalAddress.get();
                student.setAddress(address);
                summary += ", address changed ";
            } else summary += ", addres not found ";
            List<Subject> listOfSubjects = subjectRepository.findAllById(studentDto.getSubjects());
            student.setSubjects(listOfSubjects);
            studentRepository.save(student);
            return "succcessfully edited " + summary;
        }else return "Student not found";
    }

    @DeleteMapping(value = "/{id}")
    public String deleteById(@PathVariable Integer id){
        boolean existsById = studentRepository.existsById(id);
        if (existsById){
            studentRepository.deleteById(id);
            return "successfully deleted";
        }else return "student not found with this id";
    }


}
