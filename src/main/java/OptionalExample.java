import java.util.Arrays;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

public class OptionalExample {

    public static void main(String[] args){
//        String s = null;
//        Optional<String> op = Optional.ofNullable(s);
//        System.out.println(op.map(String::toString));

        /*List Optionals */
        List<String> lst = Arrays.asList("apple", "and",null);
        lst.stream()
                .map(Optional::ofNullable)
                .map(e->e.orElse("Not Present"))
                .collect(Collectors.toSet()).forEach(System.out::println);
        /* End */

        /* Example 2 */
        String s = "Diljit";
        String value = Optional.ofNullable(s).orElse("ReturnThisIfNotPresent");
        System.out.print(value);




    }
}
