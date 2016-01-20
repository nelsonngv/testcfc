//package src.com.pbasolutions.android.authentication;
//
//import android.os.Bundle;
//import android.test.InstrumentationTestCase;
//
//import com.pbasolutions.android.PandoraConstant;
//import com.pbasolutions.android.authentication.PBSServerAuthenticate;
//import com.pbasolutions.android.json.PBSLoginJSON;
//
//import org.junit.Before;
//import org.junit.Test;
//
//import static org.junit.Assert.assertEquals;
//
///**
// * Created by pbadell on 8/19/15.
// */
//
//
//
//public class PBSServerAuthenticateTest{
//    PBSServerAuthenticate serverAuthenticate;
//    @Before
//    public void init(){
//        serverAuthenticate = new PBSServerAuthenticate();
//    }
//
//    @Test(expected=AssertionError.class)
//    public void test() {
//        final int expected = 1;
//        final int reality = 5;
//        assertEquals(expected, reality);
//    }
//
//    @Test
//    public void testCallServer() {
//        PBSLoginJSON pbsLoginJSON = null;
//        Bundle resultBundle = new Bundle();
//        try {
//            resultBundle.putString("testing", "testValue");
//           // resultBundle = serverAuthenticate.callServer("", pbsLoginJSON, PBSLoginJSON.class.getName().toString(), new Bundle());
//           // pbsLoginJSON = (PBSLoginJSON)resultBundle.getSerializable(PandoraConstant.RESULT);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
