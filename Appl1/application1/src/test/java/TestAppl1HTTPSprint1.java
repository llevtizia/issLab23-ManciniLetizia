import org.junit.Before;
import org.junit.Test;
import unibo.basicomm23.http.Appl1HTTPSprint1;
import static org.junit.Assert.fail;

public class TestAppl1HTTPSprint1 {
    protected Appl1HTTPSprint1 appl;

    @Before
    public void initSystem(){
        appl         = new Appl1HTTPSprint1();
        int[] bsteps = appl.getBoundarySteps();
        assert( bsteps[0] == 0 && bsteps[1] == 0 && bsteps[2] == 0 && bsteps[3] == 0 );
    }

    @Test
    public void testBoundary(){
        try {
            appl.walkAtBoundary();
            int[] bsteps = appl.getBoundarySteps();
            assert(appl.checkRobotAtHome());
            assert( bsteps[0] == bsteps[2] && bsteps[1] == bsteps[3] );
        } catch (Exception e) {
            fail("testBoundary");
        }
    }
}
