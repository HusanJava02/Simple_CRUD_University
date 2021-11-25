package uz.pdp.appjparelationships.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uz.pdp.appjparelationships.entity.Student;
import uz.pdp.appjparelationships.entity.Subject;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Integer> {


    Page<Student> findAllByGroup_Faculty_UniversityId(Integer group_faculty_university_id, Pageable pageable);

    Page<Student> findAllByGroup_FacultyId(Integer group_faculty_id, Pageable pageable);

    @Query(value = "select s from Student s where s.group.faculty.id=:group_faculty_id")
    Page<Student> findByFacultyId(Integer group_faculty_id,Pageable pageable);

    List<Student> findAllByGroup_Teacher_Id(Integer group_teacher_id);
}
