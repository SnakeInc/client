import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class HelloWorld {

    @Getter @Setter
    boolean dummy = true;

    public static void main(String[] Args) {
        log.debug("hello world");
    }


}
