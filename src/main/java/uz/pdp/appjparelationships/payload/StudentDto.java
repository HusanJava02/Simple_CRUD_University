package uz.pdp.appjparelationships.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class StudentDto {
    private Integer id;
    private String firstName;
    private String lastName;
    private Integer addressId;
    private List<Integer> subjects;
    private Integer groupId;

}
